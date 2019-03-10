package org.apache.commons.lang3.concurrent;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class EventCountCircuitBreaker extends AbstractCircuitBreaker<Integer> {
    private static final Map<State, StateStrategy> STRATEGY_MAP = createStrategyMap();
    private final AtomicReference<CheckIntervalData> checkIntervalData;
    private final long closingInterval;
    private final int closingThreshold;
    private final long openingInterval;
    private final int openingThreshold;

    private static class CheckIntervalData {
        private final long checkIntervalStart;
        private final int eventCount;

        CheckIntervalData(int count, long intervalStart) {
            this.eventCount = count;
            this.checkIntervalStart = intervalStart;
        }

        public int getEventCount() {
            return this.eventCount;
        }

        public long getCheckIntervalStart() {
            return this.checkIntervalStart;
        }

        public CheckIntervalData increment(int delta) {
            return delta != 0 ? new CheckIntervalData(getEventCount() + delta, getCheckIntervalStart()) : this;
        }
    }

    private static abstract class StateStrategy {
        protected abstract long fetchCheckInterval(EventCountCircuitBreaker eventCountCircuitBreaker);

        public abstract boolean isStateTransition(EventCountCircuitBreaker eventCountCircuitBreaker, CheckIntervalData checkIntervalData, CheckIntervalData checkIntervalData2);

        private StateStrategy() {
        }

        public boolean isCheckIntervalFinished(EventCountCircuitBreaker breaker, CheckIntervalData currentData, long now) {
            return now - currentData.getCheckIntervalStart() > fetchCheckInterval(breaker);
        }
    }

    private static class StateStrategyClosed extends StateStrategy {
        private StateStrategyClosed() {
            super();
        }

        public boolean isStateTransition(EventCountCircuitBreaker breaker, CheckIntervalData currentData, CheckIntervalData nextData) {
            return nextData.getEventCount() > breaker.getOpeningThreshold();
        }

        protected long fetchCheckInterval(EventCountCircuitBreaker breaker) {
            return breaker.getOpeningInterval();
        }
    }

    private static class StateStrategyOpen extends StateStrategy {
        private StateStrategyOpen() {
            super();
        }

        public boolean isStateTransition(EventCountCircuitBreaker breaker, CheckIntervalData currentData, CheckIntervalData nextData) {
            return nextData.getCheckIntervalStart() != currentData.getCheckIntervalStart() && currentData.getEventCount() < breaker.getClosingThreshold();
        }

        protected long fetchCheckInterval(EventCountCircuitBreaker breaker) {
            return breaker.getClosingInterval();
        }
    }

    public EventCountCircuitBreaker(int openingThreshold, long openingInterval, TimeUnit openingUnit, int closingThreshold, long closingInterval, TimeUnit closingUnit) {
        this.checkIntervalData = new AtomicReference(new CheckIntervalData(0, 0));
        this.openingThreshold = openingThreshold;
        this.openingInterval = openingUnit.toNanos(openingInterval);
        this.closingThreshold = closingThreshold;
        this.closingInterval = closingUnit.toNanos(closingInterval);
    }

    public EventCountCircuitBreaker(int openingThreshold, long checkInterval, TimeUnit checkUnit, int closingThreshold) {
        this(openingThreshold, checkInterval, checkUnit, closingThreshold, checkInterval, checkUnit);
    }

    public EventCountCircuitBreaker(int threshold, long checkInterval, TimeUnit checkUnit) {
        this(threshold, checkInterval, checkUnit, threshold);
    }

    public int getOpeningThreshold() {
        return this.openingThreshold;
    }

    public long getOpeningInterval() {
        return this.openingInterval;
    }

    public int getClosingThreshold() {
        return this.closingThreshold;
    }

    public long getClosingInterval() {
        return this.closingInterval;
    }

    public boolean checkState() {
        return performStateCheck(0);
    }

    public boolean incrementAndCheckState(Integer increment) throws CircuitBreakingException {
        return performStateCheck(1);
    }

    public boolean incrementAndCheckState() {
        return incrementAndCheckState(Integer.valueOf(1));
    }

    public void open() {
        super.open();
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    public void close() {
        super.close();
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    private boolean performStateCheck(int increment) {
        State currentState;
        while (true) {
            currentState = (State) this.state.get();
            CheckIntervalData currentData = (CheckIntervalData) this.checkIntervalData.get();
            CheckIntervalData nextData = nextCheckIntervalData(increment, currentData, currentState, now());
            if (updateCheckIntervalData(currentData, nextData)) {
                break;
            }
        }
        if (stateStrategy(currentState).isStateTransition(this, currentData, nextData)) {
            currentState = currentState.oppositeState();
            changeStateAndStartNewCheckInterval(currentState);
        }
        return AbstractCircuitBreaker.isOpen(currentState) ^ 1;
    }

    private boolean updateCheckIntervalData(CheckIntervalData currentData, CheckIntervalData nextData) {
        if (currentData != nextData) {
            if (!this.checkIntervalData.compareAndSet(currentData, nextData)) {
                return false;
            }
        }
        return true;
    }

    private void changeStateAndStartNewCheckInterval(State newState) {
        changeState(newState);
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    private CheckIntervalData nextCheckIntervalData(int increment, CheckIntervalData currentData, State currentState, long time) {
        if (stateStrategy(currentState).isCheckIntervalFinished(this, currentData, time)) {
            return new CheckIntervalData(increment, time);
        }
        return currentData.increment(increment);
    }

    long now() {
        return System.nanoTime();
    }

    private static StateStrategy stateStrategy(State state) {
        return (StateStrategy) STRATEGY_MAP.get(state);
    }

    private static Map<State, StateStrategy> createStrategyMap() {
        Map<State, StateStrategy> map = new EnumMap(State.class);
        map.put(State.CLOSED, new StateStrategyClosed());
        map.put(State.OPEN, new StateStrategyOpen());
        return map;
    }
}

package com.robotium.solo;

class Sleeper {
    private int miniPauseDuration;
    private int pauseDuration;

    private Sleeper() {
    }

    public Sleeper(int pauseDuration, int miniPauseDuration) {
        this.pauseDuration = pauseDuration;
        this.miniPauseDuration = miniPauseDuration;
    }

    public void sleep() {
        sleep(this.pauseDuration);
    }

    public void sleepMini() {
        sleep(this.miniPauseDuration);
    }

    public void sleep(int time) {
        try {
            Thread.sleep((long) time);
        } catch (InterruptedException e) {
        }
    }
}

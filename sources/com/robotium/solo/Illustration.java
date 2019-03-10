package com.robotium.solo;

import java.util.ArrayList;

public class Illustration {
    private final ArrayList<PressurePoint> points;
    private final int toolType;

    public static class Builder {
        private ArrayList<PressurePoint> builderPoints = new ArrayList();
        private int builderToolType = 1;

        public Builder setToolType(int toolType) {
            this.builderToolType = toolType;
            return this;
        }

        public Builder addPoint(float x, float y, float pressure) {
            this.builderPoints.add(new PressurePoint(x, y, pressure));
            return this;
        }

        public Illustration build() {
            return new Illustration();
        }
    }

    private Illustration(Builder builder) {
        this.toolType = builder.builderToolType;
        this.points = builder.builderPoints;
    }

    public ArrayList<PressurePoint> getPoints() {
        return this.points;
    }

    public int getToolType() {
        return this.toolType;
    }
}

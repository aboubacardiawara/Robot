package algorithms.exercice;

import robotsimulator.Brain;

import static characteristics.IFrontSensorResult.Types.WALL;

public abstract class BaseBrain extends Brain {

    protected static double EPSILON = 0.1;
    protected boolean wallDetected() {
        return detectFront().getObjectType() == WALL;
    }

    protected boolean isSameDirection(double heading, double expectedDirection) {
        return Math.abs(normalize(heading)-normalize(expectedDirection)) < EPSILON;
    }

    private double normalize(double dir){
        double res=dir;
        while (res<0) res+=2*Math.PI;
        while (res>=2*Math.PI) res-=2*Math.PI;
        return res;
    }
}

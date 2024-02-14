package algorithms.exercice;

import characteristics.Parameters;
import robotsimulator.Brain;

import static characteristics.IFrontSensorResult.Types.WALL;

public class SquareWalkBrain extends Brain {
    int state;
    double oldAngle;
    int INIT_STATE = 0;
    int MOVE_STATE = 1;
    int RIGHT_TURN_STATE = 2;
    protected boolean startPointReached = false;
    private static double EPSILON = 0.00001;

    @Override
    public void activate() {
        state = INIT_STATE;
    }

    @Override
    public void step() {

        // init state
        if (state == INIT_STATE && isSameDirection(getHeading(), Parameters.NORTH)) {
            state = MOVE_STATE;
            return;
        }
        if (state == INIT_STATE && !isSameDirection(getHeading(), Parameters.NORTH)) {
            stepTurn(Parameters.Direction.LEFT);
            return;
        }

        // state 1
        if (state == MOVE_STATE && wallDetected()) {
            state = RIGHT_TURN_STATE;
            return;
        }
        if (state == MOVE_STATE && !wallDetected()) {
            oldAngle = getHeading();
            move();
            return;
        }

        // state 2
        if (state == RIGHT_TURN_STATE && isSameDirection(getHeading(), oldAngle+(Math.PI/2))) {
            state = MOVE_STATE;
            return;
        }
        if (state == RIGHT_TURN_STATE && !isSameDirection(getHeading(), oldAngle+(Math.PI/2))) {
            stepTurn(Parameters.Direction.RIGHT);
            return;
        }

    }

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

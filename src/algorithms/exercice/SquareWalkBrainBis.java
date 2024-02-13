package algorithms.exercice;

import characteristics.Parameters;
import robotsimulator.Brain;

import static characteristics.IFrontSensorResult.Types.WALL;

public class SquareWalkBrainBis extends Brain {
    int state;
    double oldAngle;
    int INIT_STATE = 0;
    int MOVE_STATE = 1;
    int TURN_STATE = 2;
    private static double EPSILON = 0.001;

    @Override
    public void activate() {
        state = INIT_STATE;
    }

    @Override
    public void step() {
        sendLogMessage("tir !");
        fire(getHeading());

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
            state = TURN_STATE;
            return;
        }
        if (state == MOVE_STATE && !wallDetected()) {
            oldAngle = getHeading();
            move();
            return;
        }

        // state 2
        if (state == TURN_STATE && isSameDirection(getHeading(), oldAngle+(Math.PI/2))) {
            state = MOVE_STATE;
            return;
        }
        if (state == TURN_STATE && !isSameDirection(getHeading(), oldAngle+(Math.PI/2))) {
            stepTurn(Parameters.Direction.RIGHT);
            return;
        }

    }

    protected boolean wallDetected() {
        return detectFront().getObjectType() == WALL;
    }

    protected boolean isSameDirection(double heading, double expectedDirection) {
        return Math.abs(heading-expectedDirection) < EPSILON;
    }
}

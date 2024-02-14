package algorithms.exercice;

import characteristics.Parameters;

public class CarBrain extends SquareWalkBrain {


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
}

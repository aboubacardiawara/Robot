package algorithms.exercice.stage1;

import algorithms.hunters.brains.core.BaseBrain;
import algorithms.hunters.statemachine.interfaces.IState;
import characteristics.Parameters;

public class SquareWalkBrain extends BaseBrain {
    int state;
    double oldAngle;
    int INIT_STATE = 0;
    int MOVE_STATE = 1;
    int RIGHT_TURN_STATE = 2;
    protected boolean startPointReached = false;

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
        if (state == RIGHT_TURN_STATE && isSameDirection(getHeading(), oldAngle + (Math.PI / 2))) {
            state = MOVE_STATE;
            return;
        }
        if (state == RIGHT_TURN_STATE && !isSameDirection(getHeading(), oldAngle + (Math.PI / 2))) {
            stepTurn(Parameters.Direction.RIGHT);
            return;
        }

    }

    protected IState buildStateMachine() {
        return null;
    }

    @Override
    protected double initialX() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initialX'");
    }

    @Override
    protected double initialY() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initialY'");
    }

    @Override
    protected Robots identifyRobot() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'identifyRobot'");
    }

}

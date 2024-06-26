package algorithms.exercice.stage1;

import algorithms.hunters.brains.core.BaseBrain;
import algorithms.hunters.statemachine.AnyTransitionConditionMetException;
import algorithms.hunters.statemachine.impl.State;
import algorithms.hunters.statemachine.interfaces.IState;
import characteristics.Parameters;

public class SquareWalkBrainRedesigned extends BaseBrain {

    IState currentState;
    double oldAngle;
    @Override
    public void activate() {
            currentState = buildStateMachine();
        }


    @Override
    public void step() {
        if (currentState.hasNext()) {
            try {
                currentState = currentState.next();
            } catch (AnyTransitionConditionMetException e) {
                currentState.performsAction();
            }currentState.performsAction();
        }
    }

    protected IState buildStateMachine() {
        IState initState = new State();
        IState moveState = new State();
        IState turnRightState = new State();

        // define states actions
        initState.addNext(
            moveState,
            () -> isSameDirection(getHeading(), Parameters.NORTH)
        );
        initState.setStateAction(() -> {stepTurn(Parameters.Direction.LEFT);});

        moveState.addNext(
            turnRightState,
            () -> wallDetected()
        );
        moveState.setStateAction(() -> {oldAngle = getHeading(); move();});

        turnRightState.addNext(
            moveState,
            () -> isSameDirection(getHeading(), oldAngle+(Math.PI/2))
        );
        turnRightState.setStateAction(() -> {stepTurn(Parameters.Direction.RIGHT); });

        return initState;
    }


    @Override
    protected Robots identifyRobot() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'identifyRobot'");
    }

}

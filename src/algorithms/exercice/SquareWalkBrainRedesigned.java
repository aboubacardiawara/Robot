package algorithms.exercice;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.compilation.AnyTransitionConditionMetException;
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

    private IState buildStateMachine() {
        IState initState = new State();
        IState moveState = new State();
        IState turnRightState = new State();

        // define states actions
        initState.addNext(
            moveState,
            () -> isSameDirection(getHeading(), Parameters.NORTH)
        );
        initState.setStateAction(() -> {stepTurn(Parameters.Direction.LEFT); return null;});

        moveState.addNext(
            turnRightState,
            () -> wallDetected()
        );
        moveState.setStateAction(() -> {oldAngle = getHeading(); move(); return null;});

        turnRightState.addNext(
            moveState,
            () -> isSameDirection(getHeading(), oldAngle+(Math.PI/2))
        );
        turnRightState.setStateAction(() -> {stepTurn(Parameters.Direction.RIGHT); return null;});

        return initState;
    }
}

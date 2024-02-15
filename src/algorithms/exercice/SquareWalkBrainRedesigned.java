package algorithms.exercice;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.compilation.TransitionConditionNotMetException;
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
            } catch (TransitionConditionNotMetException e) {
                currentState.performsAction();
            }
        }
    }

    private IState buildStateMachine() {
        IState initState = new State();
        IState moveState = new State();
        IState turnRightState = new State();

        // define states actions
        initState.setNext(moveState);
        initState.setTransitionAction(() -> {stepTurn(Parameters.Direction.LEFT); return null;});
        initState.setTransitionCondition(() -> isSameDirection(getHeading(), Parameters.NORTH));

        moveState.setNext(turnRightState);
        moveState.setTransitionAction(() -> {oldAngle = getHeading(); move(); return null;});
        moveState.setTransitionCondition(() -> wallDetected());

        turnRightState.setNext(moveState);
        turnRightState.setTransitionAction(() -> {stepTurn(Parameters.Direction.RIGHT); return null;});
        turnRightState.setTransitionCondition(
            () -> isSameDirection(getHeading(), oldAngle+(Math.PI/2))
        );

        // define transitions

        // define transition conditions
        return initState;
    }
}

package algorithms.exercice.stage4;

import java.util.ArrayList;

import algorithms.aboubacarlyna.brains.core.BaseBrain;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.Parameters;

public class MainBotBrain extends BaseBrain {

    private double targetDirection;

    @Override
    public void move() {
        super.move();
        robotX += Parameters.teamAMainBotSpeed * Math.cos(getHeading());
        robotY += Parameters.teamAMainBotSpeed * Math.sin(getHeading());
    }

    @Override
    protected IState buildStateMachine() {

        IState turnLittleBitLeft = new State();
        IState moveEast = new State();
        IState turnTowardOpponent = new State();
        IState moveBackState = new State();
        IState fireState = new State();

        turnLittleBitLeft.addNext(moveEast, () -> isSameDirection(getHeading(), -Math.PI / 4));
        turnLittleBitLeft.setStateAction(() -> {
            stepTurn(Parameters.Direction.LEFT);
        });

        moveEast.addNext(turnTowardOpponent, () -> opponentDetected());
        moveEast.setStateAction(() -> {
            move();
        });

        turnTowardOpponent.addNext(fireState, () -> isSameDirection(getHeading(), targetDirection));
        turnTowardOpponent.setStateAction(() -> {
            stepTurn(Parameters.Direction.LEFT);
        });

        moveBackState.setStateAction(() -> {
            move();

        });

        fireState.setStateAction(() -> {
            fire(targetDirection);

        });

        return turnLittleBitLeft;
    }

    private boolean opponentDetected() {
        ArrayList<String> messages = fetchAllMessages();
        if (!messages.isEmpty()) {
            String message = messages.get(0);
            String[] elements = message.split(",");
            double enmyX = Double.parseDouble(elements[0]);
            double enmyY = Double.parseDouble(elements[1]);
            sendLogMessage(message);
            // targetDirection = Math.atan2(enmyY - robotY, enmyX - robotX) + Math.PI;
            targetDirection = Math.atan2(enmyY - robotY, enmyX - robotX);
            return true;
        }

        return false;
    }

    @Override
    protected void beforeEachStep() {
        opponentDetected();
        super.beforeEachStep();
    }

}
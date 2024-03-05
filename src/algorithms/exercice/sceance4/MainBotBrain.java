package algorithms.exercice.sceance4;

import java.util.ArrayList;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.exercice.BaseBrain;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class MainBotBrain extends BaseBrain {

    private double targetDirection;

    @Override
    protected IState buildStateMachine() {

        IState turnLittleBitLeft = new State();
        IState moveEast = new State();
        IState turnBackState = new State();
        IState moveBackState = new State();

        turnLittleBitLeft.addNext(moveEast, () -> isSameDirection(getHeading(), -(Math.PI / 4)));
        turnLittleBitLeft.setStateAction(() -> {
            stepTurn(Parameters.Direction.LEFT);
            return null;
        });

        moveEast.addNext(turnBackState, () -> opponentDetected());
        moveEast.setStateAction(() -> {
            move();
            return null;
        });

        turnBackState.addNext(moveBackState, () -> isSameDirection(getHeading(), targetDirection));
        turnBackState.setStateAction(() -> {
            stepTurn(Parameters.Direction.LEFT);
            return null;

        });

        moveBackState.setStateAction(() -> {
            move();
            return null;
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
            targetDirection = Math.atan2(enmyY - robotY, enmyX - robotX) + Math.PI;
            return true;
        }
        return false;
    }

}
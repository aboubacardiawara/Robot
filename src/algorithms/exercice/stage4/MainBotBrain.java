package algorithms.exercice.stage4;

import java.util.ArrayList;
import java.util.Random;

import algorithms.aboubacarlyna.brains.core.BaseBrain;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.Parameters;

public class MainBotBrain extends BaseBrain {

    private double targetDirection;
    // main robot (up|middle|bottom) 
    protected enum Robots {MRUP, MRMIDDLE, MRBOTTOM};
    protected Robots currentRobot;
    Random rn = new Random();
    @Override
    public void move() {
        super.move();
        robotX += Parameters.teamAMainBotSpeed * Math.cos(getHeading());
        robotY += Parameters.teamAMainBotSpeed * Math.sin(getHeading());
    }

    @Override
    protected IState buildStateMachine() {
        Random rn = new Random();
        IState turnLittleBitLeft = new State();
        turnLittleBitLeft.setDescription("turnLittleBitLeft");
        IState moveEast = new State();
        moveEast.setDescription("moveEast");
        IState turnTowardOpponent = new State();
        turnTowardOpponent.setDescription("turnTowardOpponent");
        IState moveBackState = new State();
        moveBackState.setDescription("moveBackState");
        IState fireState = new State();
        fireState.setDescription("fireState");

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

        fireState.addNext(turnLittleBitLeft, () -> false);
        fireState.setStateAction(() -> {
            fire(targetDirection); 
        });
  
        return turnLittleBitLeft;
    }

    private boolean opponentDetected() {
        ArrayList<String> messages = fetchAllMessages();
        if (!messages.isEmpty()) {
            // random between 0 and messages.size()
            int index_random = rn.nextInt(messages.size());
            String message = messages.get(index_random);
            String[] elements = message.split(",");
            double enmyX = Double.parseDouble(elements[0]);
            double enmyY = Double.parseDouble(elements[1]);
            double direction = Double.parseDouble(elements[2]);
            sendLogMessage(message);
            targetDirection =Math.atan2(enmyY - robotY, enmyX - robotX);
            return true;
        }

        return false;
    }

    @Override
    protected void beforeEachStep() {
        opponentDetected();
        this.sendLogMessage("current state: " + this.currentState);
        super.beforeEachStep();
    }

    @Override
    protected double initialX() {
        if (currentRobot == Robots.MRUP) {
            return  Parameters.teamAMainBot1InitX;
        } else if (currentRobot == Robots.MRMIDDLE) {
            return Parameters.teamAMainBot2InitX;
        } else {
            return Parameters.teamAMainBot3InitX;
        }
    }

    @Override
    protected double initialY() {
        if (currentRobot == Robots.MRUP) {
            return Parameters.teamAMainBot1InitY;
        } else if (currentRobot == Robots.MRMIDDLE) {
            return Parameters.teamAMainBot2InitY;
        } else {
            return Parameters.teamAMainBot3InitY;
        }
    }

}
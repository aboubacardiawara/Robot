package algorithms.exercice.stage4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import algorithms.aboubacarlyna.brains.core.BaseBrain;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class MainBotBrain extends BaseBrain {

    private double targetDirection;

    // main robot (up|middle|bottom)
    protected enum Robots {
        MRUP, MRMIDDLE, MRBOTTOM
    };

    protected Robots currentRobot;
    Random rn = new Random();
    protected Map<Robots, double[]> teammatesPositions;
    private boolean shouldFire = false;

    @Override
    public void move() {
        super.move();

        robotX += Parameters.teamAMainBotSpeed * Math.cos(getHeading());
        robotY += Parameters.teamAMainBotSpeed * Math.sin(getHeading());
    }

    @Override
    protected IState buildStateMachine() {
        Random rn = new Random();
        this.teammatesPositions = new HashMap<Robots, double[]>();
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
        IState stopFiring = new State();
        stopFiring.setDescription("stopFiring");

        turnLittleBitLeft.addNext(moveEast,
                () -> isSameDirection(getHeading(), -(Math.random() - 0.5) * 2 + Math.PI / 4));
        turnLittleBitLeft.setStateAction(() -> {
            stepTurn(Parameters.Direction.LEFT);
        });

        moveEast.addNext(turnTowardOpponent, () -> detectOpponents());
        moveEast.setStateAction(() -> {
            move();
        });

        turnTowardOpponent.addNext(fireState, () -> isSameDirection(getHeading(), targetDirection));
        turnTowardOpponent.setStateAction(() -> {
            stepTurn(Parameters.Direction.RIGHT);
        });

        moveBackState.setStateAction(() -> {
            move();
        });

        /// qaund le robot n'est plus dans la meme direction que enmy (target direction
        /// il bouge)
        fireState.addNext(stopFiring, () -> !detectOpponents());
        fireState.setStateAction(() -> {
            for (IRadarResult radarResult : detectRadar()) {
                // todo: si le robot tire vers un robot de son equipe ????)
                if (shouldFire) {
                    fire(targetDirection);
                }
            }
        });

        stopFiring.addNext(fireState, () -> detectOpponents());
        stopFiring.setStateAction(() -> {
            this.shouldFire = false;
        });

        return turnLittleBitLeft;
    }

    private ArrayList<String> filterMessages(ArrayList<String> messages, Predicate<String> f) {
        return messages.stream().filter(f).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private boolean detectOpponents() {
        ArrayList<String> messages = filterMessages(
                fetchAllMessages(),
                msg -> msg.startsWith(this.OPPONENT_POS_MSG_SIGN, 0));

        if (!messages.isEmpty()) {
            // int i = rn.nextInt(messages.size());
            int i = 0;
            String[] elements = parseOpponentsPosMessage(messages.get(i));
            double y = Double.parseDouble(elements[0]);
            double x = Double.parseDouble(elements[1]);
            this.targetDirection = Math.atan2(y - robotY, x - robotX);
            this.shouldFire = true;
            return true;
        }
        return false;
    }

    private String[] parseOpponentsPosMessage(String msg) {
        String[] elements = msg.split(this.MSG_SEPARATOR);
        String[] result = Arrays.copyOfRange(elements, 1, elements.length);
        return result;
    }

    private String[] parseTemmatesPosMessage(String msg) {
        return msg.split(this.MSG_SEPARATOR);
    }

    private void updateTeammatesPositions() {
        ArrayList<String> messages = filterMessages(
                fetchAllMessages(),
                msg -> msg.startsWith(this.TEAM_POS_MSG_SIGN, 0));
        messages.forEach(msg -> {
            String[] elements = parseTemmatesPosMessage(msg);
            double y = Double.parseDouble(elements[1]);
            double x = Double.parseDouble(elements[2]);
            this.teammatesPositions.put(Robots.valueOf(elements[0]), new double[] { x, y });
        });
    }

    @Override
    protected void beforeEachStep() {
        detectOpponents();
        updateTeammatesPositions();
        this.sendLogMessage("current state: " + this.currentState);
        super.beforeEachStep();
    }

    @Override
    protected double initialX() {
        if (currentRobot == Robots.MRUP) {
            return Parameters.teamAMainBot1InitX;
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
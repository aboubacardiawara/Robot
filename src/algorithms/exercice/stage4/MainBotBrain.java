package algorithms.exercice.stage4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
                () -> isSameDirection(getHeading(), Parameters.EAST));
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
        List<Position> positions = extractPositions(messages);
        Position closestPosition = positions.stream().min((p1, p2) -> {
            double d1 = Math.sqrt(Math.pow(p1.getX() - robotX, 2) + Math.pow(p1.getY() - robotY, 2));
            double d2 = Math.sqrt(Math.pow(p2.getX() - robotX, 2) + Math.pow(p2.getY() - robotY, 2));
            return Double.compare(d1, d2);
        }).orElse(null);

        if (closestPosition != null) {
            double y = closestPosition.getY();
            double x = closestPosition.getX();
            double distance = Math.sqrt(Math.pow(x - robotX, 2) + Math.pow(y - robotY, 2));
            if (distance > Parameters.bulletRange) {
                this.shouldFire = false;
                this.targetDirection = Math.atan2(y - robotY, x - robotX) + Math.PI;
                return false;
            }
            this.targetDirection = Math.atan2(y - robotY, x - robotX) + Math.PI;
            this.shouldFire = true;
            return true;
        }
        return false;
    }

    private List<Position> extractPositions(ArrayList<String> messages) {
        return messages.stream().map(msg -> {
            String[] elements = parseOpponentsPosMessage(msg);
            double y = Double.parseDouble(elements[0]);
            double x = Double.parseDouble(elements[1]);
            return new Position(x, y);
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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

class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
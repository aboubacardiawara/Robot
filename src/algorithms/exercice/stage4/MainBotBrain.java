package algorithms.exercice.stage4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import algorithms.aboubacarlyna.brains.core.MainBotBaseBrain;
import algorithms.aboubacarlyna.brains.core.dto.Const;
import algorithms.aboubacarlyna.brains.core.dto.Position;
import algorithms.aboubacarlyna.brains.core.dto.RobotState;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.Parameters;
public class MainBotBrain extends MainBotBaseBrain {

    private double targetDirection;

    protected Robots currentRobot;
    Random rn = new Random();
    protected Map<Robots, RobotState> teammatesPositions;
    private boolean shouldFire = false;

    private ArrayList<String> receivedMessages;


    @Override
    protected IState buildStateMachine() {
        Random rn = new Random();
        this.teammatesPositions = new HashMap<Robots, RobotState>();
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
                // Todo: si le robot tire vers un robot de son equipe ????)
                if (shouldFire && !thereIsTeammatesInTargetDirection()){
                    fire(targetDirection);
                }
        });

        stopFiring.addNext(fireState, () -> detectOpponents());
        stopFiring.setStateAction(() -> {
            this.shouldFire = false;
        });

        return turnLittleBitLeft;
    }
    

    private boolean thereIsTeammatesInTargetDirection() {
         for (Robots robot : teammatesPositions.keySet()) {
            RobotState robotState = teammatesPositions.get(robot);
            Position pos = robotState.getPosition();
            
            System.out.println("state: " + robotState);
            double distance = pos.distanceTo(Position.of(robotX, robotY));

            if ( robotState.getHealth()>=0) {
                double direction = Math.atan2(pos.getY() - robotY, pos.getX() - robotX);
                if (isSameDirection(direction, targetDirection, 0)) {
                    sendLogMessage(robot.name() + " is in the target direction");
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<String> filterMessages(ArrayList<String> messages, Predicate<String> f) {
        return messages.stream().filter(f).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private boolean detectOpponents() {
        ArrayList<String> messages = filterMessages(
                this.receivedMessages,
                msg -> msg.startsWith(Const.OPPONENT_POS_MSG_SIGN, 0));
        List<Position> positions = extractPositions(messages);
        Position closestPos = positions.stream().min((p1, p2) -> {
            double d1 = p1.distanceTo(Position.of(robotX, robotY));
            double d2 = p2.distanceTo(Position.of(robotX, robotY));
            return Double.compare(d1, d2);
        }).orElse(null);

        if (closestPos != null) {
            System.out.println("closest pos: " + closestPos);
            double distance = closestPos.distanceTo(Position.of(robotX, robotY));
            this.targetDirection =  Math.atan2(closestPos.getY() - robotY, closestPos.getX() - robotX);
            if (distance > Parameters.bulletRange ) {
                this.shouldFire = false;
                return false;
            } else {
                this.shouldFire = true;
                return true;
            }
        }
        return false;
    }

    private List<Position> extractPositions(ArrayList<String> messages) {
        return messages.stream().map(msg -> {
            String[] elements = parseOpponentsPosMessage(msg);
            double y = Double.valueOf(elements[1]);
            double x = Double.valueOf(elements[2]);
            return new Position(x, y);
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private String[] parseOpponentsPosMessage(String msg) {
        String[] elements = msg.split(Const.MSG_SEPARATOR);
        return elements;
    }

    private void updateTeammatesPositions() {
        ArrayList<String> messages = filterMessages(
                this.receivedMessages,
                msg -> msg.startsWith(Const.TEAM_POS_MSG_SIGN, 0));
        messages.forEach(msg -> {
            RobotState state = RobotState.of(msg);
            this.teammatesPositions.put(state.getRobotName(), state);
        });
    }

    @Override
    protected void beforeEachStep() {
        this.receivedMessages = fetchAllMessages();
        detectOpponents();
        updateTeammatesPositions();
        logRobotPosition();
        super.beforeEachStep();
    }

    @Override
    protected void afterEachStep() {
        this.sendLogMessage("current state: " + this.currentState);
    }

    private void logTeammatesPositions() {
        String logMessage = "";
        for (Robots robot : teammatesPositions.keySet()) {
            Position pos = teammatesPositions.get(robot).getPosition();
            logMessage += robot.name() + " x: " + pos.getX() + " y: " + pos.getY() + "\n";
        }
        sendLogMessage(logMessage);
    }

}


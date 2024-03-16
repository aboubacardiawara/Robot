package algorithms.exercice.stage4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import algorithms.aboubacarlyna.brains.core.MainBotBaseBrain;
import algorithms.aboubacarlyna.brains.core.dto.Const;
import algorithms.aboubacarlyna.brains.core.dto.Position;
import algorithms.aboubacarlyna.brains.core.dto.RobotState;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.Parameters;
import characteristics.Parameters.Direction;

public class MainBotBrain extends MainBotBaseBrain {

    private double targetDirection;
    private Position targetPosition;
    protected final int teammateRadius = 3;
    protected double findALineOfFireStartAngle;
    protected double findALineOfFireMoveCounter;

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
        IState fireState = new State(2);
        fireState.setDescription("fireState");
        IState stopFiring = new State();
        stopFiring.setDescription("stopFiring");
        IState findALineOfFireStateRotationStep0 = new State();
        findALineOfFireStateRotationStep0.setDescription("findALineOfFirerotation_step_initialisation");
        IState findALineOfFireStateRotationStep = new State();
        findALineOfFireStateRotationStep.setDescription("findALineOfFire_45_rotation_step");
        IState findALineOfFireStateMoveStep0 = new State();
        findALineOfFireStateMoveStep0.setDescription("findALineOfFireMoveState_initialisation");
        IState findALineOfFireStateMoveStep = new State();
        findALineOfFireStateMoveStep.setDescription("findALineOfFire_move_step");

        turnLittleBitLeft.addNext(moveEast,
                () -> isSameDirection(getHeading(), getHeading()));
        turnLittleBitLeft.setStateAction(() -> {
            stepTurn(Parameters.Direction.LEFT);
        });

        moveEast.addNext(turnTowardOpponent, () -> detectOpponents() != DetectionResultCode.NO_OPPONENT);
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

        fireState.addNext(stopFiring, () -> detectOpponents() == DetectionResultCode.NO_OPPONENT);
        fireState.addNext(findALineOfFireStateRotationStep0,
                () -> detectOpponents() == DetectionResultCode.TEAMMATES_IN_LINE_OF_FIRE);
        fireState.setStateAction(() -> {
            if (shouldFire && !anyTeammatesLineOfFire()) {
                fire(targetDirection);
            }
        });

        findALineOfFireStateRotationStep0.addNext(findALineOfFireStateRotationStep);
        findALineOfFireStateRotationStep0.setStateAction(() -> {
            findALineOfFireStartAngle = getHeading();
        });

        findALineOfFireStateRotationStep.addNext(findALineOfFireStateMoveStep0,
                () -> isSameDirection(getHeading(), this.findALineOfFireStartAngle + (Math.PI / 4), true));

        findALineOfFireStateRotationStep.setStateAction(() -> {
            if (currentRobot == Robots.MRBOTTOM) {
                stepTurn(Parameters.Direction.RIGHT);
            } else {
                stepTurn(Parameters.Direction.LEFT);
            }
        });

        findALineOfFireStateMoveStep0.addNext(findALineOfFireStateMoveStep);
        findALineOfFireStateMoveStep0.setStateAction(() -> {
            findALineOfFireMoveCounter = 10;
        });

        findALineOfFireStateMoveStep.addNext(fireState, () -> findALineOfFireMoveCounter == 0);
        findALineOfFireStateMoveStep.setStateAction(() -> {
            move();
            findALineOfFireMoveCounter--;
        });

        stopFiring.addNext(fireState, () -> detectOpponents() != DetectionResultCode.NO_OPPONENT);
        stopFiring.setStateAction(() -> {
            this.shouldFire = false;
        });

        return turnLittleBitLeft;
    }

    protected boolean isSameDirection(double heading, double expectedDirection, boolean log) {
        if (log)
            System.out.println("heading: " + normalize(heading) + " target: " + normalize(expectedDirection));
        return super.isSameDirection(heading, expectedDirection);
    }

    protected Direction fastWayToTurn(double targetDirection) {
        double diff = targetDirection - this.getHeading();
        if (diff > Math.PI) {
            return Parameters.Direction.LEFT;
        } else if (diff < -Math.PI) {
            return Parameters.Direction.RIGHT;
        } else if (diff > 0) {
            return Parameters.Direction.RIGHT;
        } else {
            return Parameters.Direction.LEFT;
        }
    }

    private double findAngleToTurn() {
        boolean anyTeammatesUp = currentRobot == Robots.MRBOTTOM;
        if (anyTeammatesUp) {
            return (Math.PI / 4);
        } else {
            System.out.println(this.findALineOfFireStartAngle - (Math.PI / 4));
            return this.findALineOfFireStartAngle - (Math.PI / 4);
        }
    }

    private boolean anyTeammatesLineOfFire() {
        for (Robots robot : teammatesPositions.keySet()) {
            RobotState robotState = teammatesPositions.get(robot);
            Position pos = robotState.getPosition();
            if (robotState.getHealth() >= 0) {
                if (!pos.equals(Position.of(robotX, robotY)) && isInInLineOfFire(pos, teammateRadius)) {
                    // logger.info("<<" + robot.name() + " is in line of fire >> ~" +
                    // currentRobot.name());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInInLineOfFire(Position teammatePosition, int r) {
        double x1 = robotX;
        double y1 = robotY;
        if (targetPosition == null) {
            return false;
        }
        double x2 = targetPosition.getX();
        double y2 = targetPosition.getY();
        double x3 = teammatePosition.getX();
        double y3 = teammatePosition.getY();
        double numerator = Math.abs((y2 - y1) * x3 - (x2 - x1) * y3 + x2 * y1 - y2 * x1);
        double denominator = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
        double distance = numerator / denominator;

        return distance <= r;
    }

    private ArrayList<String> filterMessages(ArrayList<String> messages, Predicate<String> f) {
        return messages.stream().filter(f).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Check if there is any opponent in the radar
     * 
     * @return 0 if there is no opponent in the radar or they are out of line of
     *         fire
     *         1 if there is at least one opponent in the radar and it is in the
     *         line of fire
     *         2 if the opponents are out of line of fire because of teammates
     *         being in line of fire.s
     */
    private int detectOpponents() {
        ArrayList<String> messages = filterMessages(
                this.receivedMessages,
                msg -> msg.startsWith(Const.OPPONENT_POS_MSG_SIGN, 0));
        List<Position> positions = extractPositions(messages);
        Optional<Position> optionalPosition = candidatEnemyToShot(positions);

        if (optionalPosition.isPresent()) {
            Position closestPos = optionalPosition.get();
            double distance = closestPos.distanceTo(Position.of(robotX, robotY));
            this.targetPosition = closestPos;
            this.targetDirection = Math.atan2(closestPos.getY() - robotY, closestPos.getX() - robotX);
            if (distance > Parameters.bulletRange) {
                this.shouldFire = false;
                return DetectionResultCode.OPPONENT_OUT_OF_LINE_OF_FIRE;
            } else {
                this.shouldFire = true;
                return DetectionResultCode.OPPONENT_IN_LINE_OF_FIRE;
            }
        } else {
            boolean noOpponent = positions.size() == 0;
            if (noOpponent)
                return DetectionResultCode.NO_OPPONENT;
            else {
                return DetectionResultCode.TEAMMATES_IN_LINE_OF_FIRE;
            }
        }
    }

    /**
     * Pick a candidate to shot among the list of positions
     * 
     * @implNote First strategy: the closest to the robot
     * @implNote TODO Enhancement: we choose the closest one outside the line of
     *           fire of
     *           teammates
     * 
     * @param positions
     * @return
     */
    private Optional<Position> candidatEnemyToShot(List<Position> positions) {
        List<Position> outOfLineOfFire = new ArrayList<Position>();
        for (Position pos : positions) {
            if (!anyTeammatesLineOfFire()) {
                outOfLineOfFire.add(pos);
            }
        }
        return outOfLineOfFire.stream().min((p1, p2) -> {
            double d1 = p1.distanceTo(Position.of(robotX, robotY));
            double d2 = p2.distanceTo(Position.of(robotX, robotY));
            return Double.compare(d1, d2);
        }).map(Optional::of).orElse(Optional.empty());
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

    private void logTeammatesPositions() {
        String logMessage = "";
        for (Robots robot : teammatesPositions.keySet()) {
            Position pos = teammatesPositions.get(robot).getPosition();
            logMessage += robot.name() + " x: " + pos.getX() + " y: " + pos.getY() + "\n";
        }
        sendLogMessage(logMessage);
    }

    @Override
    protected void afterEachStep() {
        super.afterEachStep();
        sendLogMessage(currentState.toString());
    }

    @Override
    protected void exportGraphset() {
        super.exportGraphset();
        String fileName = "mainBotStateMachine.dot";
        String graph = currentState.dotify();
        writeToFile(fileName, graph);
    }

    private void writeToFile(String fileName, String graph) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(fileName)) {
            writer.println(graph);
            writer.close();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

/**
 * DetectionResultCode
 */
class DetectionResultCode {
    public static final int NO_OPPONENT = 0;
    public static final int OPPONENT_IN_LINE_OF_FIRE = 1;
    public static final int OPPONENT_OUT_OF_LINE_OF_FIRE = 2;
    public static final int TEAMMATES_IN_LINE_OF_FIRE = 3;
}
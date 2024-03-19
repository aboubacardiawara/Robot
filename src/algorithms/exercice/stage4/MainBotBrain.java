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
    private Position targetPosition = Position.of(0, 0);
    protected double randWalkDirection;
    protected int randWalkMoveCount;
    protected Position rendezVousPosition;
    protected final int teammateRadius = 3;
    protected double findALineOfFireStartAngle;
    protected double findALineOfFireMoveCounter;
    protected double rotationCount;

    Random rn = new Random();
    protected Map<Robots, RobotState> teammatesPositions = new HashMap<>();

    private ArrayList<String> receivedMessages = new ArrayList<>();

    @Override
    protected IState buildStateMachine() {
        IState STMoveEast = new State();
        STMoveEast.setDescription("Move East");
        IState STStartFire = new State();
        STStartFire.setDescription("Start Fire");
        IState STStopFire = new State();
        STStopFire.setDescription("Stop Fire");

        STMoveEast.addNext(STStartFire, () -> detectOpponent() != DetectionResultCode.ANY_OPPONENT);
        STMoveEast.setStateAction(() -> move());

        STStartFire.addNext(STStopFire,
                () -> detectOpponent() == DetectionResultCode.ANY_OPPONENT);
        STStartFire.setStateAction(() -> fire(normalize(targetDirection)));

        STStopFire.addNext(STStartFire, () -> detectOpponent() != DetectionResultCode.OPPONENT_IN_LINE_OF_FIRE);
        return STMoveEast;
    }

    private boolean fireConditionMeet() {

        boolean opponentOnSight = detectOpponents().size() > 0;
        if (opponentOnSight) {
            targetDirection = -1; // TODO
            return true;
        }
        if (this.targetPosition == null) {
            return false;
        }
        double distance = this.targetPosition.distanceTo(Position.of(robotX, robotY));

        return distance < Parameters.bulletRange;
    }

    protected boolean isSameDirection(double heading, double expectedDirection, boolean log) {
        return super.isSameDirection(heading, expectedDirection);
    }

    private boolean anyTeammatesInLineOfFire(Position targetPosition) {
        for (Robots robot : teammatesPositions.keySet()) {
            RobotState robotState = teammatesPositions.get(robot);
            Position pos = robotState.getPosition();
            if (robotState.getHealth() >= 0) {
                boolean notMe = robotState.getRobotName() != this.currentRobot;
                if (notMe && isInInLineOfFire(pos, targetPosition, teammateRadius)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInInLineOfFire(Position teammatePosition, Position targePosition, int r) {
        return teammatePosition.pointBelongToLine(
                robotX, robotY,
                targetPosition.getX(), targetPosition.getY(),
                r);
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
    private int detectOpponent() {
        ArrayList<String> messages = filterMessages(
                this.receivedMessages,
                msg -> msg.startsWith(Const.OPPONENT_POS_MSG_SIGN, 0));
        List<Position> positions = extractPositions(messages);
        Optional<Position> optionalPosition = candidatEnemyToShot(positions);

        if (optionalPosition.isPresent()) {
            System.err.println("enemy to shot");
            Position closestPos = optionalPosition.get();
            double distance = closestPos.distanceTo(Position.of(robotX, robotY));
            this.targetPosition = closestPos;
            this.targetDirection = Math.atan2(closestPos.getY() - robotY, closestPos.getX() - robotX);
            if (distance > Parameters.bulletRange) {
                return DetectionResultCode.OPPONENT_OUT_OF_LINE_OF_FIRE;
            } else {
                return DetectionResultCode.OPPONENT_IN_LINE_OF_FIRE;
            }
        } else {
            System.out.println("no enemy to shot " + positions.size());
            boolean noOpponent = positions.size() == 0;
            if (noOpponent)
                return DetectionResultCode.ANY_OPPONENT;
            else {
                return DetectionResultCode.TEAMMATES_IN_LINE_OF_FIRE;
            }
        }
    }

    /**
     * Pick a candidate to shot among the list of positions.
     * 
     * @param filteredPoints
     * @return
     */
    private Optional<Position> candidatEnemyToShot(List<Position> points) {
        List<Position> filteredPoints = new ArrayList<>();
        for (Position targetPosition : points) {
            if (!anyTeammatesInLineOfFire(targetPosition)) {
                filteredPoints.add(targetPosition);
            }
        }

        // closest to the robot
        if (filteredPoints.size() == 0) {
            return Optional.empty();
        }

        return filteredPoints.stream().min((p1, p2) -> {
            double d1 = p1.distanceTo(Position.of(robotX, robotY));
            double d2 = p2.distanceTo(Position.of(robotX, robotY));
            return Double.compare(d1, d2);
        });
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
        System.out.println("received messages " + this.receivedMessages);
        detectOpponent();
        updateTeammatesPositions();
        logRobotPosition();
        super.beforeEachStep();
    }

    @Override
    protected void afterEachStep() {
        super.afterEachStep();
        sendLogMessage(this.currentState.toString() + " " + this.targetDirection);
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
    public static final int ANY_OPPONENT = 0;
    public static final int OPPONENT_IN_LINE_OF_FIRE = 1;
    public static final int OPPONENT_OUT_OF_LINE_OF_FIRE = 2;
    public static final int TEAMMATES_IN_LINE_OF_FIRE = 3;
}
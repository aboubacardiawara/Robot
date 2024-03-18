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
        // random walk state
        IState STRandomWalk = new State();
        STRandomWalk.setDescription("Random Walk");
        IState STRandomDirection = new State();
        STRandomDirection.setDescription("Choose Random Direction");
        IState STRandomMoveCount = new State();
        STRandomMoveCount.setDescription("Choose Random Move Count");
        STRandomWalk.addNext(STRandomDirection);

        // fight states
        IState STStartFire = new State();
        STStartFire.setDescription("Start Fire");

        // rendez-vous state
        IState STGoToRendezVousPositionTurn = new State();
        STGoToRendezVousPositionTurn.setDescription("Turn toward rendez-vous position");
        IState STGoToRendezVousPositionMove = new State();
        STGoToRendezVousPositionMove.setDescription("Move to rendez-vous position");
        
        STGoToRendezVousPositionTurn.addNext(STGoToRendezVousPositionMove, () -> isSameDirection(getHeading(), targetDirection, 0.1));
        STGoToRendezVousPositionMove.addNext(STStartFire, ()-> fireConditionMeet());
        STGoToRendezVousPositionTurn.setStateAction(() -> {
            targetDirection = normalize(targetDirection);
            stepTurn(fastWayToTurn(Math.atan2(targetPosition.getY() - robotY, targetPosition.getX() - robotX)));
            System.out.println("random walk: "+ randWalkDirection + " "+ this.currentRobot);
        });
        STGoToRendezVousPositionMove.setStateAction(() -> move());
        
        STRandomDirection.setUp(() -> randWalkDirection = rn.nextDouble() * 2 * Math.PI);
        STRandomDirection.addNext(STRandomMoveCount, () -> isSameDirection(getHeading(), randWalkDirection));
        STRandomDirection.setStateAction(() ->  stepTurn(fastWayToTurn(randWalkDirection)));

        STRandomMoveCount.setUp(() -> randWalkMoveCount = rn.nextInt(200, 500) + 1);
        STRandomMoveCount.addNext(STRandomDirection,  () -> randWalkMoveCount == 0 || collisionWithTeammatesOrWall());
        STRandomMoveCount.addNext(STGoToRendezVousPositionTurn,  () -> detectOpponent() != DetectionResultCode.ANY_OPPONENT);
        STRandomMoveCount.addNext(STStartFire, () -> fireConditionMeet());
        STRandomMoveCount.setStateAction(()-> {move(); this.randWalkMoveCount--;});
       
        STStartFire.addNext(STRandomWalk, () -> detectOpponent() == DetectionResultCode.ANY_OPPONENT);
        STStartFire.setStateAction(() -> fire(normalize(targetDirection)));
        return STRandomWalk;
    }

    private boolean fireConditionMeet() {
     
        boolean opponentOnSight = detectOpponents().size() > 0 ;
        if (opponentOnSight ) {
            targetDirection = getHeading();
            return true;
        }
        if (this.targetPosition == null) {
            return false;
        }
        double distance = this.targetPosition.distanceTo(Position.of(robotX, robotY));

        return distance < Parameters.bulletRange  ;
    }

    

    protected boolean isSameDirection(double heading, double expectedDirection, boolean log) {
        //if (log)
            //System.out.println("heading: " + normalize(heading) + " target: " + normalize(expectedDirection));
        return super.isSameDirection(heading, expectedDirection);
    }

    private double findAngleToTurn() {
        boolean anyTeammatesUp = currentRobot == Robots.MRBOTTOM;
        if (anyTeammatesUp) {
            return (Math.PI / 4);
        } else {
            // System.out.println(this.findALineOfFireStartAngle - (Math.PI / 4));
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
    private int detectOpponent() {
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
                return DetectionResultCode.OPPONENT_OUT_OF_LINE_OF_FIRE;
            } else {
                return DetectionResultCode.OPPONENT_IN_LINE_OF_FIRE;
            }
        } else {
            boolean noOpponent = positions.size() == 0;
            if (noOpponent)
                return DetectionResultCode.ANY_OPPONENT;
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
        // filter: position pour les quelles il n'y a pas de coéquipiers dans la ligne de tir.
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
        detectOpponent();
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
        sendLogMessage(currentState.toString()+ " " + this.targetPosition);
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
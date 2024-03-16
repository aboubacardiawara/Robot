package algorithms.aboubacarlyna.brains.core;

import characteristics.IRadarResult;
import characteristics.Parameters;
import robotsimulator.Brain;

import static characteristics.IFrontSensorResult.Types.WALL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import algorithms.aboubacarlyna.brains.core.dto.RobotState;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;

public abstract class BaseBrain extends Brain {

    protected Logger logger = Logger.getLogger("BaseBrain");

    protected boolean leftSide = true;

    // main robot (up|middle|bottom) + secondary robot (up|bottom)
    public enum Robots {
        MRUP, MRMIDDLE, MRBOTTOM, SRUP, SRBOTTOM
    };

    protected Map<Robots, double[]> teammatesPositions;

    protected Robots currentRobot;

    protected double robotX;
    protected double robotY;
    protected IState currentState;
    int position = 0;
    public static String OPPONENT_POS_MSG_SIGN = "OPPONENT_POS_MSG";
    public static String TEAM_POS_MSG_SIGN = "TEAM_POS_MSG";
    public static String MSG_SEPARATOR = ":";

    protected double initialX() {
        return 0;
    }

    protected double initialY() {
        return 0;
    }

    @Override
    public void activate() {
        identifyInitSide();
        currentRobot = identifyRobot();
        this.robotX = initialX();
        this.robotY = initialY();
        currentState = buildStateMachine();
        exportGraphset();
    }

    /**
     * create a file and write the graphset of the state machine in.
     */
    protected void exportGraphset() {
    }

    /**
     * Detect which side our robot is in the field.
     */
    protected void identifyInitSide() {
        this.leftSide = isSameDirection(getHeading(), Parameters.EAST);
    }

    protected abstract Robots identifyRobot();

    protected abstract IState buildStateMachine();

    protected static double EPSILON = 0.05;

    protected boolean wallDetected() {
        boolean res = detectFront().getObjectType() == WALL;
        return res;
    }

    protected void beforeEachStep() {
        sendMyStateToTeammates();
    }

    protected void afterEachStep() {
    }

    private void sendMyStateToTeammates() {
        String message = TEAM_POS_MSG_SIGN + MSG_SEPARATOR
                + currentRobot + MSG_SEPARATOR
                + robotY + MSG_SEPARATOR
                + robotX + MSG_SEPARATOR
                + this.getHealth() + MSG_SEPARATOR
                + "main";
        RobotState robotState = RobotState.of(message);
        broadcast(robotState.toString());
    }

    @Override
    public void step() {
        if (!Objects.isNull(currentState)) {
            try {
                currentState = currentState.next();
            } catch (Exception e) {
                this.beforeEachStep();
                currentState.performsAction();
                this.afterEachStep();
            }
        }
    }

    @Override
    public double getHeading() {
        return normalize(super.getHeading());
    }

    protected boolean isSameDirection(double heading, double expectedDirection) {
        return isSameDirection(heading, expectedDirection, EPSILON);
    }

    protected boolean isSameDirection(double heading, double expectedDirection, double epsilon) {
        return Math.abs(heading - normalize(expectedDirection)) < epsilon;
    }

    protected double normalize(double dir) {
        double res = dir;
        while (res < 0)
            res += 2 * Math.PI;
        while (res >= 2 * Math.PI)
            res -= 2 * Math.PI;
        return res;
    }

    protected void turnRight() {
        stepTurn(Parameters.Direction.RIGHT);
    }

    protected void turnLeft() {
        stepTurn(Parameters.Direction.LEFT);
    }

    protected boolean isOpponentBot(IRadarResult radarResult) {
        return radarResult.getObjectType() == IRadarResult.Types.OpponentMainBot
                || radarResult.getObjectType() == IRadarResult.Types.OpponentSecondaryBot;
    }

    protected void logRobotPosition() {
        sendLogMessage("x: " + robotX + " y: " + robotY);
    }

}

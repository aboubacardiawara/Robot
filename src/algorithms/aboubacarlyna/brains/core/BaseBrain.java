package algorithms.aboubacarlyna.brains.core;

import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;
import characteristics.Parameters;
import robotsimulator.Brain;
import characteristics.IFrontSensorResult.Types;
import static characteristics.IFrontSensorResult.Types.WALL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import algorithms.aboubacarlyna.brains.core.dto.RobotState;
import algorithms.aboubacarlyna.statemachine.AnyTransitionConditionMetException;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;

public abstract class BaseBrain extends Brain {

    protected Logger logger = Logger.getLogger("BaseBrain");

    protected boolean leftSide = true;

    private int stateCounter = 0;

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

    protected boolean temmateDetected() {
        Types objectType = detectFront().getObjectType();
        return objectType == IFrontSensorResult.Types.TeamMainBot 
            || objectType == IFrontSensorResult.Types.TeamSecondaryBot;
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
        boolean res = detectWall();
        return res;
    }

    protected boolean detectWall() {
        return detectFront().getObjectType() == WALL;
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
                if (this.stateCounter == 0) currentState.setUp();
                currentState = currentState.next();
                currentState.tearDown();
                this.stateCounter = 0;
            } catch (AnyTransitionConditionMetException e) {
                this.beforeEachStep();
                currentState.performsAction();
                this.afterEachStep();
                this.stateCounter++;
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
        return Math.abs(normalize(heading) - normalize(expectedDirection)) < epsilon;
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

    protected List<IRadarResult> detectOpponents() {
        return detectRadar().stream().filter(result -> this.isOpponentBot(result) && isNotDead(result)).toList();
    }

    protected boolean isNotDead(IRadarResult radarResult) {
        return radarResult.getObjectType() != IRadarResult.Types.Wreck;
    }

}

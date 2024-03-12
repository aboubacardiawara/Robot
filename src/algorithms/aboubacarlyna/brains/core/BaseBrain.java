package algorithms.aboubacarlyna.brains.core;

import characteristics.IRadarResult;
import characteristics.Parameters;
import robotsimulator.Brain;

import static characteristics.IFrontSensorResult.Types.WALL;

import java.util.Objects;

import algorithms.aboubacarlyna.statemachine.interfaces.IState;

public abstract class BaseBrain extends Brain {

    // main robot (up|middle|bottom) + secondary robot (up|bottom)
    protected enum Robots {
        MRUP, MRMIDDLE, MRBOTTOM, SRUP, SRBOTTOM
    };

    protected Robots currentRobot;

    protected double robotX;
    protected double robotY;
    protected IState currentState;
    int position = 0;
    protected String OPPONENT_POS_MSG_SIGN = "OPPONENT_POS_MSG";
    protected String TEAM_POS_MSG_SIGN = "TEAM_POS_MSG";
    protected String MSG_SEPARATOR = ":";

    protected double initialX() {
        return 0;
    }

    protected double initialY() {
        return 0;
    }

    @Override
    public void activate() {
        currentRobot = identifyRobot();
        this.robotX = initialX();
        this.robotY = initialY();
        currentState = buildStateMachine();
        sendLogMessage("I am " + currentRobot);
    }

    protected abstract Robots identifyRobot();

    protected abstract IState buildStateMachine();

    protected static double EPSILON = 0.1;

    protected boolean wallDetected() {
        boolean res = detectFront().getObjectType() == WALL;
        return res;
    }

    protected void beforeEachStep() {
    }

    protected void afterEachStep() {
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
        return Math.abs(heading- normalize(expectedDirection)) < EPSILON;
    }

    private double normalize(double dir) {
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

}

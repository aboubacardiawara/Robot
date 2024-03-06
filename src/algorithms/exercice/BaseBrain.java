package algorithms.exercice;

import algorithms.compilation.IState;
import characteristics.IRadarResult;
import characteristics.Parameters;
import robotsimulator.Brain;
import robotsimulator.FrontSensorResult;

import static characteristics.IFrontSensorResult.Types.WALL;

import java.util.Objects;

public abstract class BaseBrain extends Brain {

    protected double robotX = 400;
    protected double robotY = 400;
    IState currentState;
    int position = 0;

    @Override
    public void activate() {
        currentState = buildStateMachine();
    }

    protected abstract IState buildStateMachine();

    protected static double EPSILON = 0.05;

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

    protected boolean isSameDirection(double heading, double expectedDirection) {
        return Math.abs(normalize(heading) - normalize(expectedDirection)) < EPSILON;
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

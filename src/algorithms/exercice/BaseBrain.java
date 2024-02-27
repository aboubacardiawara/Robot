package algorithms.exercice;

import algorithms.compilation.IState;
import characteristics.Parameters;
import robotsimulator.Brain;
import robotsimulator.FrontSensorResult;

import static characteristics.IFrontSensorResult.Types.WALL;

public abstract class BaseBrain extends Brain {

    IState currentState;
    int position =0;

    @Override
    public void activate() {
        currentState = buildStateMachine();
    }

    protected abstract IState buildStateMachine();

    protected static double EPSILON = 0.05;
    protected boolean wallDetected() {
        boolean res =  detectFront().getObjectType() == WALL;
        FrontSensorResult object = detectFront();
        if (res) {
            System.out.println("radar: " + this.detectRadar().get(0).getObjectDistance());
        }
        return res;
    }

    @Override
    public void step() {
        if (currentState.hasNext()) {
            try {
                currentState = currentState.next();
            } catch (Exception e) {
                currentState.performsAction();
            }
            currentState.performsAction();
        }
    }

    protected boolean isSameDirection(double heading, double expectedDirection) {
        return Math.abs(normalize(heading)-normalize(expectedDirection)) < EPSILON;
    }

    private double normalize(double dir){
        double res=dir;
        while (res<0) res+=2*Math.PI;
        while (res>=2*Math.PI) res-=2*Math.PI;
        return res;
    }

    protected void turnRight() {
        stepTurn(Parameters.Direction.RIGHT);
    }

    protected void turnLeft() {
        stepTurn(Parameters.Direction.LEFT);
    }
}

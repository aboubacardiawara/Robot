package algorithms.exercice;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.exercice.stage3.Stage3;
import characteristics.IRadarResult;
import characteristics.Parameters;

import java.util.function.Supplier;


public class RendezVous extends Stage3 {

    protected static int QUARTER = 1000;
    protected static int HALF = 1500;
    protected static int THREE_QUARTER = 2000;
    private static final int MARIO = 0x5EC0;
    private static final int ROCKIE = 0x5EC1;
    private int whoAmI;
    private double angle;
    protected double tours = 3;

    @Override
    public void activate() {
        super.activate();
        whoAmI = MARIO;
        for (IRadarResult r: detectRadar()) {
            if (r.getObjectDirection() == Parameters.NORTH) {
                whoAmI = ROCKIE;
            }
        }
    }

    protected IState buildStateMachine() {
        IState turnSouthState  = new State();
        IState moveSouthState  = new State();
        IState turnEastState = new State();
        IState moveEastState = new State();

        IState cycleMoveState = new State();
        IState cycleTurnState = new State();
        IState cycleSaveAngleState = new State();
        IState sink = new State();
        whoAmI = MARIO;

        //state 0
        turnSouthState.addNext(moveSouthState, ()-> isSameDirection(getHeading(), Parameters.SOUTH));
        turnSouthState.setStateAction(() -> {
            turnRight();
            return null;
        });

        // state 1
        moveSouthState.addNext(turnEastState, () -> wallDetected());
        moveSouthState.setStateAction(() -> {moveDecorated();
            return null;
        });

        // state 2a
        turnEastState.addNext(moveEastState, ()-> isSameDirection(getHeading(), Parameters.EAST));
        turnEastState.setStateAction(() -> {turnLeft(); return null;});

        // state 2
        Supplier<Boolean> stopCondition = () -> (
            whoAmI != MARIO
                    ? positionX >= HALF/3
                    : positionX >= QUARTER/3
        );
        moveEastState.addNext(cycleMoveState, stopCondition);
        moveEastState.setStateAction(() -> {moveDecorated(); return null;});

        // dance du feu et de la glace

        cycleMoveState.addNext(cycleSaveAngleState, () -> tours >= 10);
        cycleMoveState.setStateAction(() ->{this.moveDecorated(); return null;});

        cycleSaveAngleState.addNext(cycleTurnState);
        cycleSaveAngleState.setStateAction(() -> {angle = getHeading(); tours = 0; return null;});

        cycleTurnState.addNext(cycleMoveState, () -> isSameDirection(getHeading(), angle - Math.PI/40));
        cycleTurnState.setStateAction(() -> {turnAngle();  return null; });

        // state SINK
        sink.addNext(null );
        sink.setStateAction(() -> {return null;});


        return turnSouthState;
    }

    private Void turnAngle() {
        stepTurn(Parameters.Direction.LEFT);
        return null;
    }

    @Override
    protected void moveDecorated() {
        this.tours++;
        super.moveDecorated();
    }
    private void firePosition(int x, int y){
        if (positionX<=x) fire(Math.atan((y-positionY)/(double)(x-positionX)));
        else fire(Math.PI+Math.atan((y-positionY)/(double)(x-positionX)));
        return;
    }

}

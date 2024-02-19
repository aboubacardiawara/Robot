package algorithms.exercice.stage3;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.exercice.BaseBrain;
import characteristics.Parameters;

public class Stage3 extends BaseBrain {
    IState currentState;
    int ballet = 0;
    int positionX = 0;
    int positionY = 0;

    @Override
    public void activate() {
        currentState = buildStateMachine();
    }

    @Override
    public void step() {
        sendLogMessage("ballet: " + ballet);
        if (currentState.hasNext()) {
            try {
                currentState = currentState.next();
            } catch (Exception e) {
                currentState.performsAction();
            }
            currentState.performsAction();
        }
    }

    @Override
    protected IState buildStateMachine() {
        IState state0  = new State();
        IState state1  = new State();
        IState state2a = new State();
        IState state2 = new State();
        IState state3a = new State();
        IState state3 = new State();
        IState state4a = new State();
        IState state4 = new State(2);
        IState state5a = new State();
        IState state5 = new State();
        IState state6 = new State();
        IState sink = new State();

        //state 0
        state0.addNext(state1);
        state0.setStateAction(() -> {
            ballet =0;
            return null;
        });

        // state 1
        state1.addNext(state2a, ()-> isSameDirection(getHeading(), Parameters.SOUTH));
        state1.setStateAction(() -> {stepTurn(Parameters.Direction.RIGHT);
            return null;
        });

        // state 2a
        state2a.addNext(state2);
        state2a.setStateAction(() -> {moveDecorated(); return null;});

        // state 2
        state2.addNext(state3a, ()-> wallDetected());
        state2.setStateAction(() -> {moveDecorated(); return null;});

        // state 3a
        state3a.addNext(state3);
        state3a.setStateAction(() -> {
            stepTurn(Parameters.Direction.LEFT);
            return null;
        });

        // state 3
        state3.addNext(state4a, () -> isSameDirection(getHeading(), Parameters.EAST));
        state3.setStateAction(() -> {stepTurn(Parameters.Direction.LEFT); return null;});

        // state 4a
        state4a.addNext(state4);
        state4a.setStateAction(() -> {
            moveDecorated();
            return null;
        });

        // state 4
        state4.addNext(
            state5a,
            () -> (
                (ballet ==0 && positionX>=1000)
                || (ballet ==1 && positionX >=1500)
                || (ballet==2 && positionX >= 2000)
            ));
        state4.setStateAction(() -> {moveDecorated(); return null;});
        state4.addNext(sink, () -> wallDetected());

        // state 5a
        state5a.addNext(state5);
        state5a.setStateAction(() -> {ballet++; return null; });

        // state 5
        state5.addNext(state6, () -> isSameDirection(getHeading(), Parameters.WEST));
        state5.setStateAction(() -> {sendLogMessage("etape 5"); ;stepTurn(Parameters.Direction.RIGHT); return null;});

        // state 6
        state6.addNext(state4a, () -> isSameDirection(getHeading(), Parameters.EAST));
        state6.setStateAction(() -> {sendLogMessage("etape 6"); stepTurn(Parameters.Direction.RIGHT); return null;});

        // state SINK
        sink.addNext(null);
        sink.setStateAction(() -> {moveDecorated(); return null;});
        //
        return state0;
    }

    private void moveDecorated() {
        move();
        positionX +=Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
        positionY +=Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
    }
}
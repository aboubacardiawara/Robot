package algorithms.exercice.stage3;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.exercice.BaseBrain;
import characteristics.Parameters;


public class Stage6 extends BaseBrain {
  int positionx = 300;
  int positiony = 150;

  int robotx= 0;
  int roboty= 0;
  @Override
  protected IState buildStateMachine() {
    IState state0, state1, state2, state3, state4;
    state0 = new State();
    state1 = new State();
    state2 = new State();
    state3 = new State();
    state4 = new State();


    //state 0
    state0.addNext(state1,() -> isSameDirection(getHeading(), Parameters.NORTH));
    state0.setStateAction(() ->{stepTurn(Parameters.Direction.LEFT); return null;});

    //state1
    state1.addNext(state2,() -> wallDetected());
    state1.setStateAction(() -> {move(); return null;} );

    //state2
    state2.addNext(state3, () -> isSameDirection(Math.atan((double) positiony /positionx), getHeading()));  //to do: change the value
    state2.setStateAction(() -> {stepTurn(Parameters.Direction.RIGHT); return null;} );

    //state3e
    state3.addNext(state4, () -> false  );
    state3.setStateAction(() -> {moveDecorated(); return null;});


    return state0;
  }

  private void moveDecorated() {
    move();
    robotx +=Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
    roboty +=Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
  }
}

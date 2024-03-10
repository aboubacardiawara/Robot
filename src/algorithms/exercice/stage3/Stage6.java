package algorithms.exercice.stage3;

import algorithms.aboubacarlyna.brains.core.BaseBrain;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.Parameters;


public class Stage6 extends BaseBrain {

  double robotX = 400;
  double robotY = 400;

  final double DISTANCE_LIMITE = 50;
  double positionX = 2500;
  double positionY = 800;

  @Override
  protected IState buildStateMachine() {
    IState state0, state1, state2, state3, state4, statepoc;
    state0 = new State();
    state1 = new State();
    state2 = new State();
    state3 = new State();
    state4 = new State();

    //state 0
    state0.setDescription("Tourne vers le nord");
    state0.addNext(state1,() -> isSameDirection(getHeading(), Parameters.NORTH));
    //state0.addNext(state3);
    state0.setStateAction(() ->{stepTurn(Parameters.Direction.LEFT); });

    //state1
    state1.setDescription("Avance jusqu'au mur");
    state1.addNext(state2,() -> wallDetected());
    state1.setStateAction(() -> {move(); } );

    //state2
    state2.setDescription("Tourne sur ta droite et pointe sur le point de rdv");
    state2.addNext(state3, () -> isSameDirection(Math.atan2((positionY - robotY) , (positionX - robotX)), getHeading()));
    state2.setStateAction(() -> {stepTurn(Parameters.Direction.RIGHT); } );

    //state3
    state3.setDescription("Avance aux abords du point de rdv");
    state3.addNext(state4,  () -> positionAtteinte()); //distance entre le robot et le point(x, y) < Distance_limit
    state3.setStateAction(() -> { moveDecorated(); });

    return state0;
  }

  private boolean positionAtteinte() {
    boolean res = distance_from() <= DISTANCE_LIMITE;
    sendLogMessage("objecitf atteint: " + (int) distance_from());
    return res;
  }

  private void moveDecorated() {
    move();
    robotX +=Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
    robotY +=Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
   // sendLogMessage("x: " + robotX + " y: " + robotY);
  }

  private double distance_from()
  {
    return Math.sqrt(Math.pow((positionX - robotX),2) + Math.pow((positionY - robotY),2));
  }

  @Override
  protected double initialX() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'initialX'");
  }

  @Override
  protected double initialY() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'initialY'");
  }
}

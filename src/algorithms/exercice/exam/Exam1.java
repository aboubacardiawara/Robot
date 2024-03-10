package algorithms.exercice.exam;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.exercice.BaseBrain;
import characteristics.Parameters;
import java.util.ArrayList;
import characteristics.IRadarResult;

public class Exam1 extends BaseBrain {

  double targetX = 400;
  double targetY = 400;
  private double targetDirection;
    double enmyX; 
    double enmyY;
    double enemyDirection=0;

  final double ATTACK_DISTANCE = 200;

  @Override
  protected IState buildStateMachine() {
    IState turnLittleBitLeft = new State();
    IState moveEast = new State();
    IState turnBackState = new State();
    IState startfire = new State();

    return moveEast;
    }


  public boolean isEnemy() {
    ArrayList<IRadarResult> radarResults = detectRadar();

    for (IRadarResult r : radarResults) {
      if (r.getObjectType()==IRadarResult.Types.OpponentMainBot || r.getObjectType()==IRadarResult.Types.OpponentSecondaryBot ) {
        enemyDirection=r.getObjectDirection();
        return true;
      }
    }
    return false;
  }
}

package algorithms.exercice.stage4;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import algorithms.aboubacarlyna.brains.core.SecondaryBotBaseBrain;
import algorithms.aboubacarlyna.brains.core.dto.Const;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.IFrontSensorResult;
import characteristics.IFrontSensorResult.Types;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class SecondaryBotBrain extends SecondaryBotBaseBrain {
    Boolean collisionDetected = false;
    protected double targetHeading;
    private boolean detected;
    private  int moveacount  =100;
    private  int angleturn =1;
    private int move_back_count=30;



    @Override
    protected IState buildStateMachine() {
        IState initState = new State();
        initState.setDescription("Init State");
        IState MoveState = new State();
        MoveState.setDescription("Move State");
        IState TurnRight = new State();
        TurnRight.setDescription("Turn Right");
        IState DetecState = new State();
        DetecState.setDescription("Detect State");
        IState MoveStateTORedect = new State();
        IState STMoveWest = new State();
        STMoveWest.setDescription("Move West");
        IState STTurnWest = new State();
        STTurnWest.setDescription("Turn West");
        IState DeblocState = new State();
        DeblocState.setDescription("Debloc State");
        IState STTurnEast = new State();
        STTurnEast.setDescription("Turn East");

        MoveStateTORedect.setDescription("Move State To Redect");

        if (this.currentRobot.name().equals(Robots.SRUP.name())) {
            this.angleturn = -1;
        } 

        initState.addNext(MoveState, () -> isSameDirection(getHeading(), angleturn*Math.PI / 4));
        initState.setStateAction(() -> {
            if (this.currentRobot.name().equals(Robots.SRUP.name())) {
                turnLeft();
            } else {
                turnRight();
            }
            });

        MoveState.addNext(TurnRight, () ->  moveacount ==0);
        MoveState.setStateAction(() -> {  move(); moveacount--;});

        TurnRight.addNext(DetecState, () -> isSameDirection(getHeading(), Parameters.EAST));
        TurnRight.setStateAction(() -> {
         if (this.currentRobot.name().equals(Robots.SRUP.name())) {
            turnRight();
        } else {
            turnLeft();
        }
        });
  
        DetecState.setStateAction(() -> {
          move_back_count= 30;
          detected = false;
          boolean bullet_detected = false;
          int nombre_robot_vivivant =0;
          for (IRadarResult radarResult : detectRadar()) {
            // current hour-minute-second
            if (isOpponentBot(radarResult) && isNotDead(radarResult)) {
                double opponentPosX = this.robotX
                        + radarResult.getObjectDistance() * Math.cos(radarResult.getObjectDirection());
                double opponentPosY = this.robotY
                        + radarResult.getObjectDistance() * Math.sin(radarResult.getObjectDirection());
                String message = buildOpponentPosMessage(radarResult, opponentPosX, opponentPosY);
                //logger.info(message);
                broadcast(message);
                detected = true;
                nombre_robot_vivivant++;
            }
            
            if (radarResult.getObjectType() == IRadarResult.Types.BULLET && (radarResult.getObjectType() == IRadarResult.Types.OpponentMainBot || radarResult.getObjectType() == IRadarResult.Types.TeamMainBot)) {
              bullet_detected = true;
            }
          }
          if (!detected) {
            move();
          }
          while (bullet_detected && move_back_count != 0) {
            moveBack();
            move_back_count--;
          }
          move_back_count =30;
          bullet_detected= false;
        });

        DetecState.addNext(STTurnWest, ()-> detectWall() && isSameDirection(getHeading(), Parameters.EAST));
        DetecState.addNext(STTurnWest, ()-> detectWall() && isSameDirection(getHeading(), Parameters.NORTH));
        DetecState.addNext(STTurnEast, ()-> detectWall() && isSameDirection(getHeading(), Parameters.SOUTH));
        DetecState.addNext(STTurnEast, ()-> detectWall() && isSameDirection(getHeading(), Parameters.WEST));
        STTurnWest.addNext(DetecState, () -> isSameDirection(getHeading(), Parameters.WEST) );
        STTurnWest.setStateAction(() -> { turnRight(); });
        STTurnEast.addNext(DetecState, () -> isSameDirection(getHeading(), Parameters.EAST) );
        STTurnEast.setStateAction(() -> { turnRight(); });


      return initState;
    }

    private String buildOpponentPosMessage(IRadarResult radarResult, double opponentPosX, double opponentPosY) {
        return Const.OPPONENT_POS_MSG_SIGN
                + Const.MSG_SEPARATOR
                + opponentPosY
                + Const.MSG_SEPARATOR
                + opponentPosX
                + Const.MSG_SEPARATOR
                + getHealth()
                + Const.MSG_SEPARATOR
                + "secondary";
    }

    @Override
    protected void beforeEachStep() {
        super.beforeEachStep();
        this.logRobotPosition();
        sendOpponentPositions();
    }

    private void sendOpponentPositions() {
        List<IRadarResult> opponents = detectOpponents();
        for (IRadarResult radarResult : opponents) {
            double opponentPosX = this.robotX
                    + radarResult.getObjectDistance() * Math.cos(radarResult.getObjectDirection());
            double opponentPosY = this.robotY
                    + radarResult.getObjectDistance() * Math.sin(radarResult.getObjectDirection());
            String message = buildOpponentPosMessage(radarResult, opponentPosX, opponentPosY);
            broadcast(message);
        }
    }

    /** Verifie si j'ai un ennemi devant mois
     * 
     * @return
     */
    private boolean opponentFrontOfMe() {
      // 1. detecte radar: objets
      // 2. si un enemi est devant moi avec une ouverture de  45°, return vrai
      // 3. sinon return faux
      for (IRadarResult radar: detectRadar()) {
          if (radar.getObjectType() == IRadarResult.Types.OpponentMainBot || radar.getObjectType() == IRadarResult.Types.OpponentSecondaryBot) {
              double direction = normalize(radar.getObjectDirection());
              if (direction > -Math.PI/8 && direction < Math.PI/8) {
                  return true;
              }
          }
      }
      return false;
    }

    @Override
    protected void afterEachStep() {
      super.afterEachStep();
      this.logRobotPosition();
      sendLogMessage(this.currentState.toString() );
    }

}

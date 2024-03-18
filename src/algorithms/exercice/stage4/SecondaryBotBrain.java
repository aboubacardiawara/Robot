package algorithms.exercice.stage4;

import java.util.List;

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
    private int moveBackCount;
    private boolean opponentDetected = false;

    @Override
    protected IState buildStateMachine() {
        IState STTurnLeft = new State(2);
        STTurnLeft.setDescription("TURN LEFT");
        IState STMove = new State(2);
        STMove.setDescription("MOVE");
        IState STTurnRight = new State();
        STTurnRight.setDescription("TURN RIGHT");
        IState goBack = new State(2);
        goBack.setDescription("GO BACK");

        STTurnLeft.setUp(() -> { this.targetHeading = getHeading()-(Math.PI/2); });
        STTurnLeft.addNext(STMove, () -> isSameDirection(targetHeading, getHeading(), 0.1));
        STTurnLeft.addNext(STTurnRight, () -> collisionDetected);
        STTurnLeft.setStateAction(() -> this.turnLeft());


        STMove.addNext(STTurnRight, () -> detectWall());
        STMove.setStateAction(() -> {
            if (!opponentDetected) move();
        });
        
        STTurnRight.setUp(() -> this.targetHeading = getHeading()+(Math.PI/2));
        STTurnRight.addNext(STMove, () -> isSameDirection(targetHeading, getHeading()));
        
        STTurnRight.setStateAction(() -> this.turnRight());

         
        
        return STTurnLeft;
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
        this.logRobotPosition();
        sendOpponentPositions();
        detectCollision();
        super.beforeEachStep();
    }

    private void detectCollision() {
        if (temmateDetected() && this.moveBackCount == 0) {
            System.out.println("TEMMATE DETECTED");
            collisionDetected = true;
            IState colisionResolver = new State();
            colisionResolver.setDescription("COLISION RESOLVER");
            colisionResolver.setUp(() -> this.moveBackCount = 10);
            colisionResolver.addNext(this.currentState, () -> this.moveBackCount == 10);
            colisionResolver.setStateAction(() -> {
                this.moveBack();
                this.moveBackCount++;
            });
            this.currentState = colisionResolver;
        }
       
    }

    private boolean bulletDetected() {
        Types objectType = detectFront().getObjectType();
        return objectType == IFrontSensorResult.Types.BULLET;
    }

    private void sendOpponentPositions() {
        opponentDetected = false;
        List<IRadarResult> opponents = detectOpponents();
        for (IRadarResult radarResult : opponents) {
            opponentDetected = true;
            double opponentPosX = this.robotX
                    + radarResult.getObjectDistance() * Math.cos(radarResult.getObjectDirection());
            double opponentPosY = this.robotY
                    + radarResult.getObjectDistance() * Math.sin(radarResult.getObjectDirection());
            String message = buildOpponentPosMessage(radarResult, opponentPosX, opponentPosY);
            broadcast(message);
        }
    }

    @Override
    protected void afterEachStep() {
        super.afterEachStep();
        sendLogMessage(this.currentState.toString());
    }
    

}

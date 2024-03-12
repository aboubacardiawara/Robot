package algorithms.exercice.stage4;

import algorithms.aboubacarlyna.brains.core.SecondaryBotBaseBrain;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class SecondaryBotBrain extends SecondaryBotBaseBrain {
    Boolean detected = false;

    @Override
    protected IState buildStateMachine() {
        IState initState = new State();

        initState.setStateAction(() -> {

            for (IRadarResult radarResult : detectRadar()) {
                if (isOpponentBot(radarResult)) {
                    double opponentPosX = this.robotX
                            + radarResult.getObjectDistance() * Math.cos(radarResult.getObjectDirection());
                    double opponentPosY = this.robotY
                            + radarResult.getObjectDistance() * Math.sin(radarResult.getObjectDirection());
                    String message = buildOpponentPosMessage(radarResult, opponentPosX, opponentPosY);
                    broadcast(message);
                    
                    //moveBack();
                    detected = true;
                }
            }
            if (!detected) {
                move();
            }
            
        });
        return initState;
    }


    private String buildOpponentPosMessage(IRadarResult radarResult, double opponentPosX, double opponentPosY) {
        return this.OPPONENT_POS_MSG_SIGN 
        + this.MSG_SEPARATOR 
        + opponentPosY 
        + this.MSG_SEPARATOR 
        + opponentPosX 
        + this.MSG_SEPARATOR
        + this.getHealth() 
        + this.MSG_SEPARATOR
        + "secondary";
    }




    @Override
    protected void afterEachStep() {
        sendLogMessage("x: " + robotX + " y: " + robotY);
        super.afterEachStep();
    }


    @Override
    protected void beforeEachStep() {
        this.logRobotPosition();
        super.beforeEachStep();
    }


}

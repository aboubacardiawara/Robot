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
                    String message = opponentPosX + "," + opponentPosY +"," +radarResult.getObjectDirection();
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

    @Override
    protected double initialX() {
        if (currentRobot == Robots.SRUP) {
            return Parameters.teamASecondaryBot1InitX;
        } else{
            return Parameters.teamASecondaryBot2InitX;
        }
    }

    @Override
    protected double initialY() {
        if (currentRobot == Robots.SRUP) {
            return Parameters.teamASecondaryBot1InitY;
        } else{
            return Parameters.teamASecondaryBot2InitY;
        }
    }

}

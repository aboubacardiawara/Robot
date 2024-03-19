package algorithms.exercice.stage4;

import java.util.List;
import java.util.Random;

import algorithms.aboubacarlyna.brains.core.SecondaryBotBaseBrain;
import algorithms.aboubacarlyna.brains.core.dto.Const;
import algorithms.aboubacarlyna.statemachine.impl.State;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;
import characteristics.IFrontSensorResult;
import characteristics.IFrontSensorResult.Types;
import characteristics.IRadarResult;

public class SecondaryBotBrain extends SecondaryBotBaseBrain {
    Boolean collisionDetected = false;
    protected double targetHeading;
    private boolean detected;

    @Override
    protected IState buildStateMachine() {
        IState initState = new State();

        initState.setStateAction(() -> {

            for (IRadarResult radarResult : detectRadar()) {
                if (isOpponentBot(radarResult) && isNotDead(radarResult)) {
                    double opponentPosX = this.robotX
                            + radarResult.getObjectDistance() * Math.cos(radarResult.getObjectDirection());
                    double opponentPosY = this.robotY
                            + radarResult.getObjectDistance() * Math.sin(radarResult.getObjectDirection());
                    String message = buildOpponentPosMessage(radarResult, opponentPosX, opponentPosY);
                    broadcast(message);
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
        super.beforeEachStep();
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

    @Override
    protected void afterEachStep() {
        super.afterEachStep();
    }

}

package algorithms.exercice.sceance4;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.exercice.BaseBrain;
import algorithms.exercice.SecondaryBotBaseBrain;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class SecondaryBotBrain extends SecondaryBotBaseBrain {
    Boolean detected = false;

    @Override
    protected IState buildStateMachine() {
        IState initState = new State();
        IState finalState = new State();

        initState.addNext(finalState, () -> false);
        initState.setStateAction(() -> {

            for (IRadarResult radarResult : detectRadar()) {
                if (isOpponentBot(radarResult)) {
                    double opponentPosX = this.robotX
                            + radarResult.getObjectDistance() * Math.cos(radarResult.getObjectDirection());
                    double opponentPosY = this.robotY
                            + radarResult.getObjectDistance() * Math.sin(radarResult.getObjectDirection());
                    String message = opponentPosX + "," + opponentPosY;
                    broadcast(message);
                    moveBack();
                    detected = true;
                }
            }
            if (!detected) {
                move();
            }
            return null;
        });
        return initState;
    }

}

package algorithms.exercice.sceance4;

import algorithms.compilation.IState;
import algorithms.compilation.State;
import algorithms.exercice.BaseBrain;
import characteristics.IRadarResult;

public class SecondaryBotBrain extends BaseBrain {

    @Override
    protected IState buildStateMachine() {
        IState initState = new State();
        IState finalState = new State();

        initState.addNext(finalState, () -> false);
        initState.setStateAction(() -> {
            move();
            for (IRadarResult radarResult : detectRadar()) {
                if (isOpponentBot(radarResult)) {
                    double opponentPosX = this.robotX
                            + radarResult.getObjectDistance() * Math.cos(radarResult.getObjectDirection());
                    double opponentPosY = this.robotY
                            + radarResult.getObjectDistance() * Math.sin(radarResult.getObjectDirection());
                    String message = opponentPosX + "," + opponentPosY;
                    broadcast(message);
                }
            }
            return null;
        });
        return initState;
    }

}

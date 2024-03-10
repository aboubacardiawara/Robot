package algorithms.aboubacarlyna.brains.core;

import characteristics.Parameters;

public abstract class MainBotBaseBrain extends BaseBrain {
    @Override
    public void move() {
        super.move();
        robotX += Parameters.teamAMainBotSpeed * Math.cos(getHeading());
        robotY += Parameters.teamAMainBotSpeed * Math.sin(getHeading());
    }
}
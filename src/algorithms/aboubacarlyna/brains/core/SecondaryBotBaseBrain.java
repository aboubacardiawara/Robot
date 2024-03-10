package algorithms.aboubacarlyna.brains.core;

import characteristics.Parameters;

public abstract class SecondaryBotBaseBrain extends BaseBrain {
	
	// secondary robot (up|bottom)
    protected enum Robots {SRUP, SRBOTTOM};
    protected Robots currentRobot;

	@Override
	public void move() {
		super.move();
		robotX += Parameters.teamASecondaryBotSpeed * Math.cos(getHeading());
		robotY += Parameters.teamASecondaryBotSpeed * Math.sin(getHeading());
	}

	@Override
    protected double initialX() {
        if (currentRobot == Robots.SRUP) {
            return  Parameters.teamASecondaryBot1InitX;
        } else {
            return Parameters.teamASecondaryBot2InitX;
        }
    }

    @Override
    protected double initialY() {
        if (currentRobot == Robots.SRUP) {
            return Parameters.teamASecondaryBot1InitY;
        } else {
            return Parameters.teamASecondaryBot2InitY;
        }
    }
}

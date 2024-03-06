package algorithms.aboubacarlyna.brains.core;

import characteristics.Parameters;

public abstract class SecondaryBotBaseBrain extends BaseBrain {
	@Override
	public void move() {
		super.move();
		robotX += Parameters.teamASecondaryBotSpeed * Math.cos(getHeading());
		robotY += Parameters.teamASecondaryBotSpeed * Math.sin(getHeading());
	}
}

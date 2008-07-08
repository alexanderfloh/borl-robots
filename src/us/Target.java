package us;

import robocode.ScannedRobotEvent;

public class Target extends ScannedRobotEvent {

	public Target(ScannedRobotEvent event) {
		super(event.getName(), event.getEnergy(), event.getBearingRadians(), event.getDistance(),
				event.getHeadingRadians(), event.getVelocity());
	}

	@Override
	public String toString() {
		return getName() + ": " + getEnergy() + " " + getHeading();
	}
}

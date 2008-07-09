package us;

import robocode.ScannedRobotEvent;

/**
 * @author Stefan Untereichner
 * 
 */
public class Target extends ScannedRobotEvent {

	/**
	 * @param event
	 */
	public Target(ScannedRobotEvent event) {
		super(event.getName(), event.getEnergy(), event.getBearingRadians(), event.getDistance(), event
				.getHeadingRadians(), event.getVelocity());
	}

	@Override
	public String toString() {
		return getName() + ": " + getEnergy() + " " + getHeading();
	}
}

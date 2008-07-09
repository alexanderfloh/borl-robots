package us;

import robocode.AdvancedRobot;
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
	
	public double getAbsoluteBearing(AdvancedRobot bot) {
		return Utils.normalizeAngle(getBearing() + bot.getHeading());
	}

	@Override
	public String toString() {
		return getName() + ": " + getEnergy() + " " + getHeading();
	}
}

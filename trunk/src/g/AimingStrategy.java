package g;

import static g.Utils.absoluteDirection;
import static g.Utils.bulletSpeed;
import static g.Utils.normalizeAngle;

import java.awt.geom.Point2D;

import robocode.AdvancedRobot;

public class AimingStrategy {

	private final AdvancedRobot geckBot;
	private double linarTargetingFactor = 1.0;
	private final String targetName;

	public AimingStrategy(TehGeckBot geckBot, String targetName) {
		this.geckBot = geckBot;
		this.targetName = targetName;
		if (targetName == null)
			throw new NullPointerException();
	}

	public void aimAtTarget(Target target, double bulletPower) {
		// distance = bulletspeed * ticks
		long ticks = (long) (target.getDistance() / bulletSpeed(bulletPower));

		Point2D.Double futurePosition = target.futurePosition(ticks, linarTargetingFactor);
		TehGeckBot.lastFuturePosition = futurePosition;
		double absoluteBearing = absoluteDirection(geckBot.getX(), geckBot.getY(), futurePosition.x, futurePosition.y);
		geckBot.setTurnGunRight(normalizeAngle(absoluteBearing - geckBot.getGunHeading()));
	}

	public void useNewLinearTargetingFactor() {
		if (linarTargetingFactor == 1)
			linarTargetingFactor = 0;
		else
			linarTargetingFactor = 1;
		geckBot.out.println("          changing aiming strategy for " + targetName + " to factor "
				+ linarTargetingFactor + "!");
	}
}

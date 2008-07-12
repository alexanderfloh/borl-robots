package g;

import static g.Utils.*;
import static robocode.util.Utils.*;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import robocode.AdvancedRobot;

public class AimingStrategy {

	private final AdvancedRobot geckBot;
	private int success = 0;
	private double linarTargetingFactor = 1.0;
	private final String targetName;
	private Double lastFuturePosition;

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
		double absoluteBearing = absoluteBearing(geckBot.getX(), geckBot.getY(), futurePosition.x, futurePosition.y);
		geckBot.setTurnGunRight(normalizeAngle(absoluteBearing - geckBot.getGunHeading()));
	}

	public void succeeded() {
		success++;
		updateTargetingFactor();
	}

	public void failed() {
		success--;
		updateTargetingFactor();
	}

	private void updateTargetingFactor() {
		// change strategy if we lost more often than we won
		if (success < 0) {
			if (isNear(0, linarTargetingFactor))
				linarTargetingFactor = 1;
			else
				linarTargetingFactor = 0;
		}
		geckBot.out.println(" changing aiming strategy for " + targetName + " to factor " + linarTargetingFactor + "!");
	}

	public void onPaint(Graphics2D g) {

	}
}

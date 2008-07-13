package g;

import static g.Utils.*;

import java.awt.geom.Point2D;

public class LinarGun extends VirtualGun {

	double linarTargetingFactor;

	/**
	 * @param linearTargetingFactor
	 *            1 = the target is estimated to move linear<br>
	 *            0 = the target is estimated to be at the same position as it is now, i.e. this creates a direct gun.<br>
	 *            negative value = the target is move in the opposite direction as it is moving now
	 */
	public LinarGun(TehGeckBot geckBot, double linearTargetingFactor) {
		super(geckBot);
		this.linarTargetingFactor = linearTargetingFactor;
	}

	@Override
	double getAbsoluteBearingDegrees(Target target) {
		// distance = bulletspeed * ticks
		long ticks = (long) (target.getDistance() / bulletSpeed(getPower(target)));

		Point2D.Double futurePosition = target.futurePosition(ticks, target.getVelocity() * linarTargetingFactor);
		return absoluteBearingDegrees(geckBot.getX(), geckBot.getY(), futurePosition.x, futurePosition.y);
	}

	@Override
	public String toString() {
		return "linear gun (" + linarTargetingFactor + ") hit ratio = " + getHitRatio() + " hits = " + hitBullets
				+ " missedBullets = " + missedBullets;
	}
}

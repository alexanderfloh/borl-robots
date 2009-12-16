package g;

import static g.Utils.*;
import static java.lang.Math.*;

import java.util.Random;

import robocode.CustomEvent;

public class StrafeMovement implements Movement {
	private TehGeckBot geckBot;

	private final Random random = new Random();
	private long lastCloseToWallEventTime;
	private long lastTargetFiredEventTime;

	private long lastStrafeDirectionChange;
	private int strafeDirectionFactor = +1;
	private int STRAFE_DIRECTION_CHANGE_TIME_DELTA = 10;

	public StrafeMovement(TehGeckBot geckBot) {
		this.geckBot = geckBot;
	}

	public void move(Target target) {
		if (shouldRamTarget(target)) {
			ramTarget(target);
		} else {
			strafe(target);
		}
	}

	private boolean shouldRamTarget(Target target) {
		return isOneOnOneFight() && target.exists() && (target.getEnergy() < geckBot.getEnergy() || target.getEnergy() < 20)
				&& geckBot.getTime() - lastTargetFiredEventTime > 10;
	}

	private boolean isOneOnOneFight() {
		return geckBot.getOthers() == 1;
	}

	private void ramTarget(Target target) {
		geckBot.setTurnRight(target.getAbsoluteBearing() - geckBot.getHeading());
		geckBot.setAhead(500);
	}

	private void strafe(Target target) {
		// turn ~90° to target
		geckBot.setTurnRight(normalizeRelativeAngle(target.getBearing() + 90 - 30 * strafeDirectionFactor));
		// change strafe direction after a random number of ticks
		if (geckBot.getTime() % (1 + random.nextInt(100)) == 0) {
			changeStrafeDirection();
		}
		geckBot.setAhead(min(150, geckBot.wallDistance() - 10) * strafeDirectionFactor);
	}

	private void changeStrafeDirection() {
		if (geckBot.getTime() - lastStrafeDirectionChange >= STRAFE_DIRECTION_CHANGE_TIME_DELTA) {
			strafeDirectionFactor *= -1;
			lastStrafeDirectionChange = geckBot.getTime();
		}
	}

	public void onCustomEvent(CustomEvent event) {
		if (event.getCondition().getName().equals(TargetFiredCondition.NAME)) {
			lastTargetFiredEventTime = geckBot.getTime();
		} else if (event.getCondition().getName().equals(CloseToWallCondition.NAME)) {
			if (geckBot.getTime() - lastCloseToWallEventTime > 20) {
				geckBot.println("close to wall event - changing strafe direction");
				changeStrafeDirection();
				geckBot.setAhead(150 * strafeDirectionFactor);
			}
			lastCloseToWallEventTime = geckBot.getTime();
		}
	}
}

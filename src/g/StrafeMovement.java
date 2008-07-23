package g;

import static g.Utils.*;
import static java.lang.Math.*;

import java.util.Random;

import robocode.CustomEvent;

public class StrafeMovement implements Movement {
	private TehGeckBot geckBot;

	private final Random random = new Random();
	private long lastCloseToWallEventTime = 0;
	private long lastStrafeDirectionChange;
	private int STRAFE_DIRECTION_CHANGE_TIME_DELTA = 10;
	private int strafeDirection = 1;

	public StrafeMovement(TehGeckBot geckBot) {
		this.geckBot = geckBot;
	}

	public void move(Target target) {
		// turn 90° to target
		geckBot.setTurnRight(normalizeRelativeAngle(target.getBearing() + 90 - 30 * strafeDirection));
		// change strafe direction after a random number of ticks
		if (geckBot.getTime() % (1 + random.nextInt(100)) == 0) {
			changeStrafeDirection();
		}
		geckBot.setAhead(min(150, geckBot.wallDistance() - 10) * strafeDirection);
	}

	private void changeStrafeDirection() {
		if (geckBot.getTime() - lastStrafeDirectionChange >= STRAFE_DIRECTION_CHANGE_TIME_DELTA) {
			strafeDirection *= -1;
			lastStrafeDirectionChange = geckBot.getTime();
		}
	}

	public void onCustomEvent(CustomEvent event) {
		if (event.getCondition().getName().equals(CloseToWallCondition.NAME)) {
			if (geckBot.getTime() - lastCloseToWallEventTime > 20) {
				geckBot.println("close to wall event - changing strafe direction");
				changeStrafeDirection();
				geckBot.setAhead(150 * strafeDirection);
			}
			lastCloseToWallEventTime = geckBot.getTime();
		} else if (event.getCondition().getName().equals(TargetFiredCondition.NAME)) {
			// TODO do something here
		}
	}
}

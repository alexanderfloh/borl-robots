package g;

import java.util.Random;

import robocode.CustomEvent;

public class CrazyMovement implements Movement {

	private Random random = new Random();
	private final TehGeckBot geckBot;
	private int direction = 1;
	private long lastDirectionChange;

	public CrazyMovement(TehGeckBot geckBot) {
		this.geckBot = geckBot;
	}

	public void move(Target target) {
		geckBot.setTurnRight(random.nextInt(100) - 50);
		geckBot.setAhead(100 * direction);
		if (geckBot.getTime() - lastDirectionChange > 50) {
			changeDirection();
		}
	}

	private void changeDirection() {
		direction *= -1;
		lastDirectionChange = geckBot.getTime();
	}

	public void onCustomEvent(CustomEvent event) {
		if (event.getCondition().getName().equals(TargetFiredCondition.NAME)) {
			if (geckBot.getTime() - lastDirectionChange > 20) {
				// changeDirection();
			}
		}
	}
}

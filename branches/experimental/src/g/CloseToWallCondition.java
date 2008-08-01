package g;

import robocode.Condition;

public class CloseToWallCondition extends Condition {

	public static final String NAME = "closeToWall";

	private final TehGeckBot geckBot;
	private final int minWallDistance = 100;

	public CloseToWallCondition(TehGeckBot geckBot) {
		super(NAME);
		this.geckBot = geckBot;
	}

	@Override
	public boolean test() {
		return geckBot.wallDistance() < minWallDistance;
	}

}
package g;

import robocode.Condition;

public class TargetFiredCondition extends Condition {

	public static final String NAME = "targetFired";
	private final Target target;

	public TargetFiredCondition(Target target) {
		super(NAME);
		this.target = target;
	}

	@Override
	public boolean test() {
		if (!target.exists())
			return false;
		return target.getEnergyDelta() <= -0.1 && target.getEnergyDelta() >= -3;
	}
}
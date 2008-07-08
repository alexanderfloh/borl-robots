package us;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;

/**
 * UBot - a robot by Stefan Untereichner
 */
public class UBot extends AdvancedRobot {
	private static double DISTANCE = 1000;
	private static double SPACE = 40;
	private boolean _movingForward = true;
	private Target _target = null;

	/**
	 * run: UBot default behavior
	 */
	public void run() {
		setBodyColor(Color.black);
		setGunColor(Color.white);
		setRadarColor(Color.green);
		setBulletColor(Color.red);
		setScanColor(Color.blue);

		if (getBattleFieldHeight() >= getBattleFieldWidth()) {
			DISTANCE = (getBattleFieldHeight() / 7 + getBattleFieldWidth()) / 2;
		} else {
			DISTANCE = (getBattleFieldHeight() + getBattleFieldWidth() / 3) / 2;
		}
		out.println("Setting the default distance to " + DISTANCE + " BattleField: "
				+ getBattleFieldWidth() + "x" + getBattleFieldHeight());

		DISTANCE = DISTANCE * 0.76;
		DISTANCE = 40000;

		setAdjustRadarForGunTurn(true);
		// setAdjustGunForRobotTurn(true);
		int i = -1;
		moveToCenter();

		while (true) {
			if (_target != null && _target.getEnergy() > 0) {
				double amount = (_target.getBearing() + getHeading()) % 360;
				setTurnGunRight(amount - getGunHeading());
				setTurnRadarRight((amount - getRadarHeading()) + (45 / 2) * i);

				if (Math.abs(getGunHeading() - (_target.getBearing() + getHeading())) < 5) {
					if (_target.getDistance() < 150)
						setFire(3);
					else {
						setFire(1);
					}
				}

			} else {
				setTurnRadarLeft(45);
			}
			// setAhead(20);
			// setTurnLeft(45);
			i *= -1;
			execute();
		}
	}

	private void moveToCenter() {
		Point2D.Double center = new Point2D.Double(getBattleFieldWidth() / 2,
				getBattleFieldHeight() / 2);
		Point2D.Double position = new Point2D.Double(getX(), getY());

		double a = center.x - position.x;
		double b = center.y - position.y;
		double c = Math.sqrt(a * a + b * b);

		double alpha = Math.atan(a / b);

		out.println(c + " " + Math.toDegrees(alpha));

		setTurnLeft(getHeading() + Math.toDegrees(alpha));
		waitFor(new TurnCompleteCondition(this));
		setAhead(c);
		execute();
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		_target = new Target(event);
		out.println("scanned robot: " + (_target.getBearing() + getHeading()) % 360);
	}
}

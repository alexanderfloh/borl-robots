package us;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.RadarTurnCompleteCondition;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;

/**
 * UBot - a robot by Stefan Untereichner
 * 
 * @author Stefan Untereichner
 */
public class UBot extends AdvancedRobot {
	protected static double DISTANCE = 1000;

	protected static double SPACE = 40;

	private final AimStorage _aims = new AimStorage();

	/**
	 * run: UBot default behavior
	 */
	@Override
	public void run() {
		setColors(Color.black, Color.white, Color.green, Color.red, Color.blue);

		setAdjustRadarForGunTurn(true);
		// setAdjustGunForRobotTurn(true);

		out.println("Aims: " + _aims.getCount());

		int i = -1;
		moveToPoint(getCenter());

		setTurnRadarLeft(360);
		waitFor(new RadarTurnCompleteCondition(this));
		execute();
		Target aim = null;
		while (true) {
			Target newAim = _aims.getWeakest();
			if (newAim != null)
				aim = newAim;
			if (aim != null) {
				IAimingStrategy aimingStrategy = _aims.getAimingStrategy(aim);

				double amount = Math
						.abs((aim.getBearing() + getHeading()) % 360);
				amount = aimingStrategy.getTurnGunRightAmount(aim, this);
				setTurnGunRight(amount);

				setFire(3);
				// waitFor(new GunTurnCompleteCondition(this));
				// setTurnRadarRight((amount - getRadarHeading()) + (45 / 2) *
				// i);

				// if (Math.abs(getGunHeading()
				// - (aim.getBearing() + getHeading())) < 5) {
				// if (aim.getDistance() < 150)
				// setFire(3);
				// else {
				// setFire(1);
				// }
				// }

			}
			setTurnRadarLeft(45);
			// setAhead(20);
			// setTurnLeft(45);
			i *= -1;
			execute();
		}
	}

	private void moveToPoint(Point2D.Double point) {
		Point2D.Double position = getCurrentPosition();

		double a = point.x - position.x;
		double b = point.y - position.y;
		double c = Point2D.distance(point.x, point.y, position.x, position.y);

		double alpha = Math.toDegrees(Math.atan(a / b));
		double angle = 0;

		if (position.y > point.y) {
			angle = 180 - getHeading() + alpha;
		} else {
			angle = alpha - getHeading();
		}

		if (Math.abs(angle) > 180) {
			angle = 360 - Math.abs(angle);
		}
		angle = angle % 360;

		out.println(alpha + " " + getHeading() + " " + angle + " ");

		setTurnRight(angle);
		waitFor(new TurnCompleteCondition(this));
		setAhead(c);
		execute();
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		_aims.addTarget(new Target(event));
		out.println("scanned robot: " + event.getName());
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.drawLine((int) (getCenter().x - 5), (int) (getCenter().y),
				(int) (getCenter().x + 5), (int) (getCenter().y));
		g.drawLine((int) (getCenter().x), (int) (getCenter().y - 5),
				(int) (getCenter().x), (int) (getCenter().y + 5));

		g.drawLine((int) (getCurrentPosition().x),
				(int) (getCurrentPosition().y), (int) (getCenter().x),
				(int) (getCenter().y));
		g.drawLine((int) (getCenter().x), (int) (getCenter().y),
				(int) (getCurrentPosition().x), (int) (getCenter().y));
		g.drawLine((int) (getCenter().x), (int) (getCenter().y),
				(int) (getCenter().x), (int) (getCurrentPosition().y));
		g.drawLine((int) (getCenter().x), (int) (getCurrentPosition().y),
				(int) (getCurrentPosition().x), (int) (getCurrentPosition().y));
		g.drawLine((int) (getCurrentPosition().x), (int) (getCenter().y),
				(int) (getCurrentPosition().x), (int) (getCurrentPosition().y));
	}

	private Point2D.Double getCenter() {
		return new Point2D.Double(getBattleFieldWidth() / 2,
				getBattleFieldHeight() / 2);
	}

	private Point2D.Double getCurrentPosition() {
		return new Point2D.Double(getX(), getY());
	}
}

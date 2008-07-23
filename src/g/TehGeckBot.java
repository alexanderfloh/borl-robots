package g;

import static g.Utils.*;
import static java.lang.Math.*;
import static robocode.util.Utils.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import robocode.AdvancedRobot;
import robocode.Bullet;
import robocode.CustomEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/**
 * @author michi
 * @since 27.06.2008
 */
public class TehGeckBot extends AdvancedRobot {

	public static final int ROBOT_SIZE = 36;

	private static final int maxGunTurnRemainingForFire = 3;
	private static final int maxScanAge = 20;

	private static final Map<String, VirtualGunArray> virtualGunsPerTarget = new HashMap<String, VirtualGunArray>();
	private Rectangle2D.Double battleField;
	private int radarDirection = 1;
	private long lastTimeTargetFired;
	private final Target target = new Target(this);
	private Movement movement = new StrafeMovement(this);

	@Override
	public void run() {
		// make body, gun, and radar move independent of each other
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		setColors(Color.cyan, Color.cyan, Color.BLUE);
		setBulletColor(Color.cyan);

		addCustomEvent(new CloseToWallCondition(this));
		addCustomEvent(new TargetFiredCondition(target));

		battleField = new Rectangle2D.Double(0, 0, getBattleFieldWidth(), getBattleFieldHeight());
		while (true) {
			if (!target.exists() || isTargetScanTooOld()) {
				target.reset();
				searchForTarget();
			} else {
				turnRadarToTarget();
				move();
				fight();
			}
			execute();
		}
	}

	private void move() {
		movement.move(target);
	}

	private boolean isOneOnOneFight() {
		return getOthers() == 1;
	}

	private VirtualGunArray getVirtualGunArrayForTarget(String targetName) {
		if (!virtualGunsPerTarget.containsKey(targetName))
			virtualGunsPerTarget.put(targetName, new VirtualGunArray(this));
		return virtualGunsPerTarget.get(targetName);
	}

	private boolean isTargetScanTooOld() {
		long scanAge = getTime() - target.getTimeStamp();
		boolean tooOld = scanAge > maxScanAge;
		if (tooOld)
			println("scan too old (" + scanAge + " ticks). Searching for new target.");
		return tooOld;
	}

	double wallDistance() {
		double distance = Double.MAX_VALUE;
		if (getX() < getBattleFieldWidth() / 2)
			distance = getX();
		else
			distance = getBattleFieldWidth() - getX();
		if (getY() < getBattleFieldHeight() / 2)
			distance = Math.min(distance, getY());
		else
			distance = Math.min(distance, getBattleFieldHeight() - getY());
		return distance;
	}

	private void searchForTarget() {
		setTurnLeft(50);
		setAhead(50);
		setTurnRadarRight(360);
	}

	private void fight() {
		VirtualGunArray gunArray = getVirtualGunArrayForTarget(target.getName());
		VirtualGun bestGun = gunArray.getBestGun();
		setTurnGunRight(normalizeRelativeAngle(bestGun.getAbsoluteBearingDegrees(target) - getGunHeading()));
		double bulletPower = bestGun.getPower(target);
		double gunTurnRemaining = abs(getGunTurnRemaining());

		boolean shouldRamTarget = isOneOnOneFight() && target.exists()
				&& (target.getEnergy() < getEnergy() || target.getEnergy() < 20)
				&& getTime() - lastTimeTargetFired > 10;

		if (shouldRamTarget) {
			ramTarget();
		}
		if (getEnergy() < 10) {
			// our last shots - only fire if we kill our target for sure
			if (isNear(gunTurnRemaining, 0) && target.exists() && isNear(target.getEnergy(), 0)) {
				doFire(gunArray, bulletPower);
			}
		} else {
			if (gunTurnRemaining <= maxGunTurnRemainingForFire) {
				doFire(gunArray, bulletPower);
			}
		}
	}

	private void doFire(VirtualGunArray gunArray, double bulletPower) {
		Bullet bullet = setFireBullet(bulletPower);
		gunArray.simulateFire(target, bullet != null);
	}

	private void ramTarget() {
		setTurnRight(target.getAbsoluteBearing() - getHeading());
		setAhead(500);
	}

	void println(String string) {
		out.println(string);
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		boolean isCurrentTarget = e.getName().equals(target.getName());
		if (!target.exists() || isCurrentTarget || isBetterTarget(e)) {
			target.update(e);
		}
	}

	private boolean isBetterTarget(ScannedRobotEvent e) {
		if (isNear(e.getEnergy(), 0))
			return true;
		if (isNear(e.getVelocity(), 0))
			return true;
		return e.getDistance() < target.getDistance();
	}

	private void turnRadarToTarget() {
		setTurnRadarRight(normalizeRelativeAngle(target.getAbsoluteBearing() - getRadarHeading() + 22.5
				* radarDirection));
		radarDirection *= -1;
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		println("hit wall :(");
	}

	@Override
	public void onCustomEvent(CustomEvent event) {
		movement.onCustomEvent(event);
		if (event.getCondition().getName().equals("targetFired")) {
			lastTimeTargetFired = getTime();
		}
	}

	@Override
	public void onPaint(Graphics2D g) {
		if (target.exists()) {
			target.onPaint(g);
			getVirtualGunArrayForTarget(target.getName()).onPaint(g);
		}
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		if (target.getName().equals(event.getName())) {
			target.reset();
		}
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		setTurnRadarRight(normalizeRelativeAngle(getHeading() + event.getBearing() - getRadarHeading()));
		setTurnGunRight(normalizeRelativeAngle(getHeading() + event.getBearing() - getGunHeading()));
		if (getGunTurnRemaining() < 10)
			setFire(3);
		execute();
	}

	public Point2D.Double getPosition() {
		return new Point2D.Double(getX(), getY());
	}

	public Rectangle2D.Double getBattleField() {
		return battleField;
	}
}
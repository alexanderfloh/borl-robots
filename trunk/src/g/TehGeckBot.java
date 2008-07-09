package g;

import static g.Utils.normalizeAngle;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.DeathEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/**
 * @author michi
 * @since 27.06.2008
 */
public class TehGeckBot extends AdvancedRobot {

	private static final Map<String, AimingStrategy> aimingStrategyForEnemy = new HashMap<String, AimingStrategy>();

	private final int maxGunTurnRemainingForFire = 7;
	private final int maxScanAge = 20;
	private final int minWallDistance = 50;

	private int strafeDirection = 1;
	private int radarDirection = 1;

	private final Target target = new Target(this);

	private final Random random = new Random();

	private static boolean isOneOnOneFight;

	@Override
	public void run() {
		init();
		while (true) {
			if (!target.exists() || isTargetScanTooOld()) {
				target.reset();
				searchForTarget();
			}
			execute();
		}
	}

	private void init() {
		// make body, gun, and radar move independent of each other
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		setAllColors(Color.cyan);

		isOneOnOneFight = getOthers() == 1;

		Condition closeToWallCondition = new Condition("closeToWall") {
			@Override
			public boolean test() {
				return wallDistance() < minWallDistance;
			}

		};
		addCustomEvent(closeToWallCondition);
	}

	private AimingStrategy getAimingStragegyForTarget(String targetName) {
		if (!aimingStrategyForEnemy.containsKey(targetName))
			aimingStrategyForEnemy.put(targetName, new AimingStrategy(this, targetName));
		return aimingStrategyForEnemy.get(targetName);
	}

	private double wallDistance() {
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

	private boolean isTargetScanTooOld() {
		long scanAge = getTime() - target.getScanTime();
		boolean tooOld = scanAge > maxScanAge;
		if (tooOld)
			println("scan too old (" + scanAge + " ticks). Searching for new target.");
		return tooOld;
	}

	private void searchForTarget() {
		println("searching for target");
		// move around and turn radar
		setTurnLeft(50);
		setAhead(50);
		setTurnRadarRight(360);
	}

	private void fight() {
		double bulletPower = bulletPower();
		getAimingStragegyForTarget(target.getName()).aimAtTarget(target, bulletPower);
		double gunTurnRemaining = abs(getGunTurnRemaining());
		if (getEnergy() < 10) {
			// our last shots - only fire if we kill our target for sure
			if (gunTurnRemaining == 0 && target.exists() && target.getEnergy() == 0) {
				setFire(bulletPower);
				println("firing last bullet at disabled target");
			}
		} else {
			// fire if we are already in the right position
			if (gunTurnRemaining < maxGunTurnRemainingForFire) {
				setFire(bulletPower);
			} else {
				println("cannot fire - still need to turn gun " + Math.round(gunTurnRemaining) + "°");
			}
		}
	}

	private void strafe() {
		// turn 90° to target
		setTurnRight(normalizeAngle(target.getBearing() + 90));
		// change strafe direction after a random number of ticks
		if (getTime() % (1 + random.nextInt(100)) == 0) {
			changeStrafeDirection();
		}
		setAhead(min(150, wallDistance() - 10) * strafeDirection);
	}

	private void println(String string) {
		// out.println(string);
	}

	private double bulletPower() {
		if (target.getEnergy() <= 12)
			return target.getEnergy() / 4.0;
		else
			return max(1.5, (500 / target.getDistance()));
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		boolean isCurrentTarget = e.getName().equals(target.getName());

		if (isCurrentTarget) {
			// dodge bullet if target fired a bullet
			double energyLoss = e.getEnergy() - target.getEnergy();
			if (energyLoss > 0 && energyLoss <= 3)
				dodgeBullet();
		}

		if (!target.exists() || isCurrentTarget || isBetterTarget(e)) {
			target.update(e);
		}

		turnRadarToTarget(e);
		fight();
		strafe();
		execute();
	}

	private boolean isBetterTarget(ScannedRobotEvent e) {
		if (e.getEnergy() == 0)
			return true;
		return e.getDistance() < target.getDistance();
	}

	private void turnRadarToTarget(ScannedRobotEvent e) {
		setTurnRadarRight(normalizeAngle(getHeading() - getRadarHeading() + e.getBearing() + 22.5 * radarDirection));
		radarDirection *= -1;
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		println("hit wall :(");
	}

	private long lastCloseToWallEventTime = 0;

	@Override
	public void onCustomEvent(CustomEvent event) {
		if (event.getCondition().getName().equals("closeToWall")) {
			if (getTime() - lastCloseToWallEventTime > 20) {
				println("close to wall event - changing strafe direction");
				changeStrafeDirection();
				setAhead(150 * strafeDirection);
			}
			lastCloseToWallEventTime = getTime();
		}
	}

	// for debugging with painting only
	static Point2D.Double lastFuturePosition;

	@Override
	public void onPaint(Graphics2D g) {
		if (target.exists()) {
			drawCircle(g, 100, target.getPosition());
			if (lastFuturePosition != null)
				drawCircle(g, 10, lastFuturePosition);
		}
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		// remove dead robot as target
		if (target.getName().equals(event.getName())) {
			target.reset();
		}
		isOneOnOneFight = getOthers() == 1;
	}

	private void changeStrafeDirection() {
		strafeDirection *= -1;
	}

	private void dodgeBullet() {
		changeStrafeDirection();
	}

	private void drawCircle(Graphics2D g, int radius, Point2D.Double center) {
		g.drawOval((int) center.getX() - radius / 2, (int) center.getY() - radius / 2, radius, radius);
	}

	@Override
	public void onDeath(DeathEvent event) {
		if (isOneOnOneFight) {
			getAimingStragegyForTarget(target.getName()).useNewLinearTargetingFactor();
		}
	}
}
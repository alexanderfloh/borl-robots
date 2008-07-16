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
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.Condition;
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

	private static final Map<String, VirtualGunArray> virtualGunsPerTarget = new HashMap<String, VirtualGunArray>();

	public static final int ROBOT_SIZE = 36;

	private final int maxGunTurnRemainingForFire = 3;
	private final int maxScanAge = 20;
	private final int minWallDistance = 100;

	private int strafeDirection = 1;
	private int radarDirection = 1;

	private long lastTimeTargetFired;

	private final Target target = new Target(this);

	private final Random random = new Random();

	@Override
	public void run() {
		init();
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
		strafe();
	}

	private boolean isOneOnOneFight() {
		return getOthers() == 1;
	}

	private void init() {
		// make body, gun, and radar move independent of each other
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		setColors(Color.cyan, Color.cyan, Color.BLUE);
		setBulletColor(Color.cyan);

		Condition closeToWallCondition = new Condition("closeToWall") {
			@Override
			public boolean test() {
				return wallDistance() < minWallDistance;
			}

		};
		addCustomEvent(closeToWallCondition);

		Condition targetFired = new Condition("targetFired") {
			@Override
			public boolean test() {
				if (!target.exists())
					return false;
				return target.getEnergyDelta() <= -0.1 && target.getEnergyDelta() >= -3;
			}
		};
		addCustomEvent(targetFired);

		battleField = new Rectangle2D.Double(0, 0, getBattleFieldWidth(), getBattleFieldHeight());
	}

	private VirtualGunArray getVirtualGunArrayForTarget(String targetName) {
		if (!virtualGunsPerTarget.containsKey(targetName))
			virtualGunsPerTarget.put(targetName, new VirtualGunArray(this));
		return virtualGunsPerTarget.get(targetName);
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
		long scanAge = getTime() - target.getTimeStamp();
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
		VirtualGunArray gunArray = getVirtualGunArrayForTarget(target.getName());
		VirtualGun bestGun = gunArray.getBestGun();
		println("best gun = " + bestGun);

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
				setFire(bulletPower);
				gunArray.simulateFire(target);
			}
		} else {
			if (gunTurnRemaining <= maxGunTurnRemainingForFire) {
				setFire(bulletPower);
				gunArray.simulateFire(target);
			}
		}
	}

	private void ramTarget() {
		setTurnRight(target.getAbsoluteBearing() - getHeading());
		setAhead(500);
	}

	private void strafe() {
		// turn 90° to target
		setTurnRight(normalizeRelativeAngle(target.getBearing() + 90));
		// change strafe direction after a random number of ticks
		if (getTime() % (1 + random.nextInt(100)) == 0) {
			changeStrafeDirection();
		}
		setAhead(min(150, wallDistance() - 10) * strafeDirection);
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

	private long lastCloseToWallEventTime = 0;

	private Rectangle2D.Double battleField;

	@Override
	public void onCustomEvent(CustomEvent event) {
		if (event.getCondition().getName().equals("closeToWall")) {
			if (getTime() - lastCloseToWallEventTime > 20) {
				println("close to wall event - changing strafe direction");
				changeStrafeDirection();
				setAhead(150 * strafeDirection);
			}
			lastCloseToWallEventTime = getTime();
		} else if (event.getCondition().getName().equals("targetFired")) {
			dodgeBullet();
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
		setFire(3);
		execute();
	}

	private void changeStrafeDirection() {
		strafeDirection *= -1;
	}

	private void dodgeBullet() {
		changeStrafeDirection();
	}

	public Point2D.Double getPosition() {
		return new Point2D.Double(getX(), getY());
	}

	public Rectangle2D.Double getBattleField() {
		return battleField;
	}
}
package g;

import static java.lang.Math.*;

import java.awt.geom.Point2D;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Target {
	private final Robot geckBot;

	private double distance;
	private double bearing;
	private double energy;
	private long timeStamp;
	private double velocity;
	private String name = "";
	private double heading;
	private Point2D.Double position;
	private double energyDelta;
	private double myHeading;

	public Target(Robot me) {
		this.geckBot = me;
	}

	public void update(ScannedRobotEvent scanEvent) {
		if (scanEvent.getName().equals(name))
			energyDelta = scanEvent.getEnergy() - energy;
		else
			energyDelta = 0;
		energy = scanEvent.getEnergy();
		distance = scanEvent.getDistance();
		myHeading = geckBot.getHeading();
		bearing = scanEvent.getBearing();
		timeStamp = scanEvent.getTime();
		velocity = scanEvent.getVelocity();
		name = scanEvent.getName();
		heading = scanEvent.getHeading();
		position = calculatePosition();
	}

	private Point2D.Double calculatePosition() {
		double absoluteBearing = myHeading + bearing;
		double x = geckBot.getX() + sin(toRadians(absoluteBearing)) * distance;
		double y = geckBot.getY() + cos(toRadians(absoluteBearing)) * distance;
		return new Point2D.Double(x, y);
	}

	/**
	 * 
	 * @param ticks
	 * @param estimatedSpeedRatio
	 *            1 = the target is estimated to move linear<br>
	 *            0 = the target is estimated to be at the same position as it is now <br>
	 *            negative value = the target is move in the opposite direction as it is moving now
	 * @return
	 */
	public Point2D.Double futurePosition(long ticks, double estimatedSpeedRatio) {
		double distance = ticks * velocity;
		double xMovement = distance * sin(toRadians(heading));
		double yMovement = distance * cos(toRadians(heading));
		double futureX = getPosition().getX() + xMovement * estimatedSpeedRatio;
		double futureY = getPosition().getY() + yMovement * estimatedSpeedRatio;
		return new Point2D.Double(futureX, futureY);
	}

	public void reset() {
		name = "";
	}

	public boolean exists() {
		return !("".equals(name));
	}

	public double getDistance() {
		return distance;
	}

	public double getEnergy() {
		return energy;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public double getVelocity() {
		return velocity;
	}

	public String getName() {
		return name;
	}

	public double getHeading() {
		return heading;
	}

	public Point2D.Double getPosition() {
		return position;
	}

	public double getMyHeading() {
		return myHeading;
	}

	public double getBearing() {
		return bearing;
	}

	public double getEnergyDelta() {
		return energyDelta;
	}

}

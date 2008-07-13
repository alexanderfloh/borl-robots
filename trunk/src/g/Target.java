package g;

import static java.lang.Math.*;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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

	private double absoluteBearing;

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
		bearing = scanEvent.getBearing();
		timeStamp = scanEvent.getTime();
		velocity = scanEvent.getVelocity();
		name = scanEvent.getName();
		heading = scanEvent.getHeading();
		absoluteBearing = geckBot.getHeading() + bearing;

		double x = geckBot.getX() + sin(toRadians(absoluteBearing)) * distance;
		double y = geckBot.getY() + cos(toRadians(absoluteBearing)) * distance;
		position = new Point2D.Double(x, y);
	}

	public Point2D.Double futurePosition(long ticks, double estimatedVelocity) {
		double distance = ticks * estimatedVelocity;
		double futureX = getPosition().getX() + (distance * sin(toRadians(heading)));
		double futureY = getPosition().getY() + (distance * cos(toRadians(heading)));
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

	public Rectangle2D.Double getRectangle() {
		int robotSize = TehGeckBot.ROBOT_SIZE;
		return new Rectangle2D.Double(position.x - robotSize / 2, position.y - robotSize / 2, robotSize, robotSize);
	}

	public double getAbsoluteBearing() {
		return absoluteBearing;
	}

	public double getBearing() {
		return bearing;
	}

	public double getEnergyDelta() {
		return energyDelta;
	}

	public void onPaint(Graphics2D g) {
		Utils.drawCircle(g, 100, getPosition());
	}

}

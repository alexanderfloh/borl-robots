package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class Target {
  
  private final AdvancedRobot robot;
  private final String targetName;
  private double headingRadians;
  private double bearingRadians;
  private double distance;
  private double velocity;
  private double energy;
  private long timeStamp;
  private double myHeadingRadians;
  
  private Rectangle2D.Double rectangle;
  private Point2D.Double enemyPos;
  private Point2D.Double myPos;

  public Target(AdvancedRobot robot, ScannedRobotEvent event) {
    this.robot = robot;
    this.targetName = event.getName();
    
    this.rectangle = new Rectangle2D.Double();
    rectangle.height = DasBot.ROBOT_SIZE;
    rectangle.width = DasBot.ROBOT_SIZE;
    
    update(event);
  }

  public void update(ScannedRobotEvent event) {
    myHeadingRadians = robot.getHeadingRadians();

    this.headingRadians = event.getHeadingRadians();
    this.distance = event.getDistance();
    this.velocity = event.getVelocity();
    this.timeStamp = event.getTime();
    this.energy = event.getEnergy();
    this.bearingRadians = getEnemyBearingRadians(event.getBearingRadians(), myHeadingRadians);    
    
    myPos = new Point2D.Double(robot.getX(), robot.getY());
    enemyPos = Utils.translate(myPos, bearingRadians, distance);
    
    rectangle.x = enemyPos.x - DasBot.ROBOT_SIZE / 2;
    rectangle.y = enemyPos.y - DasBot.ROBOT_SIZE / 2;
    
  }

  public double projectBearingRadians(double power, double velocityFactor) {
    double ticks = ticksToTarget(power);

    Point2D.Double projectedPos = Utils.translate(enemyPos, headingRadians, ticks * (velocity * velocityFactor));

    double relPosX = projectedPos.x - myPos.x;
    double relPosY = projectedPos.y - myPos.y;
    double projectedDistance = myPos.distance(projectedPos);

    double projectedBearing = getProjectedBearing(relPosX, relPosY, projectedDistance);
    return projectedBearing;
  }

  public String getTargetName() {
    return targetName;
  }

  public double getMaxFirePower() {
    // if the enemy has less than 0.1 energy left, it is most likely disabled
    // and cant move
    // shoot with the least amount of energy possible.
    if (energy < 0.1)
      return 0.1;
    
    if(robot.getEnergy() < 10)
      return 0;

    double ticks = 50; // TODO hardcoded
    double requiredVelocity = distance / ticks;
    double power = (20 - requiredVelocity) / 3;
    return Math.min(3, power);
  }
  
  public Rectangle2D.Double getRectangle() {
    return rectangle;
  }

  public void onPaint(Graphics2D g) {
    g.setColor(Color.RED);
    g.draw(rectangle);

    double ticksToTarget = ticksToTarget(1);
    Point2D.Double projectedPos = Utils.translate(enemyPos, headingRadians, velocity * ticksToTarget);
    Line2D.Double l1 = new Line2D.Double(enemyPos, projectedPos);
    g.draw(l1);

    double relPosX = projectedPos.x - myPos.x;
    double relPosY = projectedPos.y - myPos.y;
    double projectedDistance = myPos.distance(projectedPos);

    double projectedBearing = getProjectedBearing(relPosX, relPosY, projectedDistance);
    Point2D.Double projectedBearingPos = Utils.translate(myPos, projectedBearing, projectedDistance);
    Line2D.Double l2 = new Line2D.Double(myPos, projectedBearingPos);
    g.draw(l2);
  }

  public double getHeadingRadians() {
    return headingRadians;
  }

  public double getBearingRadians() {
    return bearingRadians;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public double getDistance() {
    return distance;
  }

  private double getProjectedBearing(double relPosX, double relPosY, double projectedDistance) {
    double projectedBearing;
    if (relPosY >= 0) {
      projectedBearing = Math.asin(relPosX / projectedDistance);
    } else {
      projectedBearing = Math.PI - Math.asin(relPosX / projectedDistance);
    }
    return projectedBearing;
  }

  private double ticksToTarget(double power) {
    double bulletVelocity = 20 - (3 * power);
    double ticks = (distance / bulletVelocity);
    return ticks;
  }

  private double getEnemyBearingRadians(double originalBearingRadians, double myHeadingRadians) {
    double enemyBearingRadians = ((2 * Math.PI)
        + (myHeadingRadians + originalBearingRadians)) % (2 * Math.PI);
    return enemyBearingRadians;
  }
}

package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
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
    this.bearingRadians = normalizeEnemyBearingRadians(event.getBearingRadians(), myHeadingRadians);    
    
    myPos = new Point2D.Double(robot.getX(), robot.getY());
    enemyPos = Utils.translate(myPos, bearingRadians, distance);
    
    rectangle.x = enemyPos.x - DasBot.ROBOT_SIZE / 2;
    rectangle.y = enemyPos.y - DasBot.ROBOT_SIZE / 2;

    double newEnergy = event.getEnergy();
    double energyDiff = this.energy - newEnergy;
    if(energyDiff > 0 && energyDiff < 3.1) {
      // target fired a shot
    }
    this.energy = newEnergy;
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
    double power = Math.min(3, (20 - requiredVelocity) / 3);
    
    if(robot.getEnergy() < 40) {
      power = Math.min(0.1, power);
    }
    
    return power;
  }
  
  public Rectangle2D.Double getRectangle() {
    return rectangle;
  }

  public void onPaint(Graphics2D g) {
    g.setColor(Color.RED);
    g.draw(rectangle);
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

  public double getVelocity() {
    return velocity;
  }

  public Point2D.Double getEnemyPos() {
    return enemyPos;
  }

  public void setHeadingRadians(double headingRadians) {
    this.headingRadians = headingRadians;
  }

  private double normalizeEnemyBearingRadians(double originalBearingRadians, double myHeadingRadians) {
    return robocode.util.Utils.normalAbsoluteAngle(myHeadingRadians + originalBearingRadians);
  }
}

package lexx.target;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

import lexx.Angle;
import lexx.DasBot;

public class EnemyState {
  private final Point2D.Double position;
  private final Rectangle2D.Double rectangle;
  private final Angle bearing;
  private final Angle heading;
  private final double velocity;
  private final long timeStamp;
  private final double distance;
  private final double energy;
  
  public EnemyState(Double enemyPosition, Angle heading, Angle bearing, double velocity, double distance, double energy, long timeStamp) {
    this.position = enemyPosition;
    this.velocity = velocity;
    this.timeStamp = timeStamp;
    this.bearing = bearing;
    this.heading = heading;
    this.distance = distance;
    this.energy = energy;
    
    double x = enemyPosition.x - DasBot.ROBOT_SIZE / 2;
    double y = enemyPosition.y - DasBot.ROBOT_SIZE / 2;
    rectangle = new Rectangle2D.Double(x, y, DasBot.ROBOT_SIZE, DasBot.ROBOT_SIZE);
  }

  public Point2D.Double getPosition() {
    return position;
  }

  public double getBearingRadians() {
    return bearing.getAngle();
  }

  public double getHeadingRadians() {
    return heading.getAngle();
  }

  public double getVelocity() {
    return velocity;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public Angle getBearing() {
    return bearing;
  }

  public Angle getHeading() {
    return heading;
  }

  public double getDistance() {
    return distance;
  }

  public double getEnergy() {
    return energy;
  }

  public Rectangle2D.Double getRectangle() {
    return rectangle;
  }
  
  
}

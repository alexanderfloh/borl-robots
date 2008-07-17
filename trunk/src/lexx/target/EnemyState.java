package lexx.target;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class EnemyState {
  private Point2D.Double enemyPosition;
  private double bearingRadians;
  private double headingRadians;
  private double velocity;
  private long timeStamp;
  
  public EnemyState(Double enemyPosition, double headingRadians, double velocity, double bearingRadians, long timeStamp) {
    this.enemyPosition = enemyPosition;
    this.headingRadians = headingRadians;
    this.velocity = velocity;
    this.bearingRadians = bearingRadians;
    this.timeStamp = timeStamp;
  }

  public Point2D.Double getEnemyPosition() {
    return enemyPosition;
  }

  public double getBearingRadians() {
    return bearingRadians;
  }

  public double getHeadingRadians() {
    return headingRadians;
  }

  public double getVelocity() {
    return velocity;
  }

  public long getTimeStamp() {
    return timeStamp;
  }
  
  
}

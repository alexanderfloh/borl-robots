package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

import lexx.target.Target;

public class Wave {
  private final Angle heading;
  private final Point2D.Double origin;
  private final long timeStamp;
  private final double velocity;
  private final double power;
  private Point2D.Double currentPos;
  
  public Wave(Double origin, Angle heading, double velocity, double power, long timeStamp) {
    this.origin = origin;
    this.heading = heading;
    this.velocity = velocity;
    this.power = power;
    this.timeStamp = timeStamp;
    this.currentPos = origin;
  }

  public Angle getHeading() {
    return heading;
  }

  public Point2D.Double getOrigin() {
    return origin;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public double getVelocity() {
    return velocity;
  }
  
  public double getPower() {
    return power;
  }

  public Point2D.Double getCurrentPosition() {
    return currentPos;
  }

  public void update(long currentTime) {
    long tickDiff = currentTime - (timeStamp - 1);
    currentPos = heading.projectPoint(origin, velocity * tickDiff);
  }
  
  public boolean isTargetHit(Target target) {
    return target.getCurrentState().getRectangle().contains(currentPos);
  }
  
  public boolean isWithinBattleField(Rectangle2D.Double battleField) {
    return battleField.contains(currentPos);
  }
  
  public double getMovedDistance() {
    return origin.distance(currentPos);
  }
  
  public void onPaint(Graphics2D g) {
    g.setColor(Color.RED);
    double movedDistance = getMovedDistance();
    g.drawOval((int)(origin.x - movedDistance), (int)(origin.y - movedDistance), (int)(2 * movedDistance), (int)(2 * movedDistance));
  }
}

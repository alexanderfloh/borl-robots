package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
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
    long tickDiff = currentTime - timeStamp;
    currentPos = heading.projectPoint(origin, velocity * tickDiff);
  }
  
  public boolean isTargetHit(Target target) {
    return target.getCurrentState().getRectangle().contains(currentPos);
  }
  
  public boolean isWithinBattleField(Rectangle2D.Double battleField) {
    return battleField.contains(currentPos);
  }
  
  public Line2D.Double getWaveLine() {
    double distance = origin.distance(currentPos);
    Angle normalAngle = heading.add(Angle.EAST);
    Double pointA = normalAngle.projectPoint(currentPos, distance / 2);
    Double pointB = normalAngle.projectPoint(currentPos, -distance / 2);
    return new Line2D.Double(pointA, pointB);
  }
  
  public Line2D.Double getSecondWaveLine() {
    Point2D.Double shiftedPos = heading.projectPoint(currentPos, -DasBot.ROBOT_SIZE / 2);
    double distance = origin.distance(shiftedPos);
    Angle normalAngle = heading.add(Angle.EAST);
    Double pointA = normalAngle.projectPoint(shiftedPos, distance / 2);
    Double pointB = normalAngle.projectPoint(shiftedPos, -distance / 2);
    return new Line2D.Double(pointA, pointB);
  }

  public void onPaint(Graphics2D g) {
    g.setColor(Color.RED);
    g.draw(getWaveLine());
    g.setColor(Color.BLUE);
    g.draw(getSecondWaveLine());
  }
}

package lexx;

import java.awt.geom.Point2D;

import robocode.util.Utils;

public class Angle {
  private final double angle;
  
  public static final Angle NORTH = new Angle(0);
  public static final Angle EAST = new Angle(Math.PI / 2);
  public static final Angle SOUTH = new Angle(Math.PI);
  public static final Angle WEST = new Angle(Math.PI * 3 / 2);

  public Angle(double angle) {
    this.angle = angle;
  }

  public double getAngle() {
    return angle;
  }
  
  public Angle add(Angle other) {
    return normalizedAbsoluteAngle(angle + other.getAngle());
  }
  
  public Angle substract(Angle other) {
    return normalizedAbsoluteAngle(angle - other.getAngle());
  }
  
  public Point2D.Double projectPoint(Point2D.Double origin, double distance) {
    return lexx.Utils.translate(origin, angle, distance);
  }
  
  public static Angle normalizedAbsoluteAngle(double angle) {
    return new Angle(Utils.normalAbsoluteAngle(angle));
  }
}

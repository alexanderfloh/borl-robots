package lexx;

import java.awt.geom.Point2D;

import robocode.util.Utils;

public class Angle {
  private final double angle;

  public Angle(double angle) {
    this.angle = angle;
  }

  public double getAngle() {
    return angle;
  }
  
  public Angle addAngle(Angle other) {
    return normalizedAbsoluteAngle(angle + other.getAngle());
  }
  
  public Point2D.Double projectPoint(Point2D.Double origin, double distance) {
    return lexx.Utils.translate(origin, angle, distance);
  }
  
  public static Angle normalizedAbsoluteAngle(double angle) {
    return new Angle(Utils.normalAbsoluteAngle(angle));
  }
}

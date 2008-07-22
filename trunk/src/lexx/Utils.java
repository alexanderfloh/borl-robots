package lexx;

import java.awt.geom.Point2D;

public class Utils {
  public static Point2D.Double translate(Point2D.Double point, double angleRadians, double distance) {
    double deltaX = Math.sin(angleRadians) * distance;
    double deltaY = Math.cos(angleRadians) * distance;
    Point2D.Double result = new Point2D.Double(point.x + deltaX, point.y + deltaY); 
    
    return result;
  }
  
  public static double getHeadingRadians(Point2D.Double origin, Point2D.Double target) {
    double relPosX = target.x - origin.x;
    double relPosY = target.y - origin.y;
    double projectedDistance = origin.distance(target);
    double heading;
    if (relPosY >= 0) {
      heading = Math.asin(relPosX / projectedDistance);
    } else {
      heading = Math.PI - Math.asin(relPosX / projectedDistance);
    }

    return heading;
  }
  
  public static Angle getHeading(Point2D.Double origin, Point2D.Double target) {
    return Angle.normalizedAbsoluteAngle(getHeadingRadians(origin, target));
  }
  
  public static double powerToVelocity(double power) {
    double bulletVelocity = 20 - (3 * power);
    return bulletVelocity;
  }
  
  public static double velocityToPower(double velocity) {
    double power = (20 - velocity) / 3;
    return power;
  }
}

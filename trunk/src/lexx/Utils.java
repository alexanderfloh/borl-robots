package lexx;

import java.awt.geom.Point2D;

public class Utils {
  public static Point2D.Double translate(Point2D.Double point, double angleRadians, double distance) {
    double deltaX = Math.sin(angleRadians) * distance;
    double deltaY = Math.cos(angleRadians) * distance;
    Point2D.Double result = new Point2D.Double(point.x + deltaX, point.y + deltaY); 
    
    return result;
  }
  
  public static double getBearingForPointRadians(double deltaX, double deltaY, double distance) {
    double bearing;
    if (deltaY >= 0) {
      bearing = Math.asin(deltaX / distance);
    } else {
      bearing = Math.PI - Math.asin(deltaX / distance);
    }
    return bearing;
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

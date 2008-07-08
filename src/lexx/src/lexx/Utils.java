package lexx;

import java.awt.geom.Point2D;

public class Utils {
  public static Point2D.Double translate(Point2D.Double point, double angleRadians, double distance) {
    double deltaX = Math.sin(angleRadians) * distance;
    double deltaY = Math.cos(angleRadians) * distance;
    Point2D.Double result = new Point2D.Double(point.x + deltaX, point.y + deltaY); 
    
    return result;
  }
}

package g;

import static java.lang.Math.*;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Utils {
	/**
	 * @return an angle that is equal to given one and that is within the range [-180 .. 180]
	 */
	static double normalizeRelativeAngle(double direction) {
		while (direction < -180)
			direction += 360;
		while (direction > 180)
			direction -= 360;
		return direction;
	}

	static double bulletSpeed(double bulletPower) {
		return 20 - max(3, bulletPower) * 3;
	}

	static double absoluteBearingDegrees(double x1, double y1, double x2, double y2) {
		return toDegrees(atan2(x2 - x1, y2 - y1));
	}

	static void drawCircle(Graphics2D g, int radius, Point2D.Double center) {
		g.drawOval((int) center.getX() - radius / 2, (int) center.getY() - radius / 2, radius, radius);
	}
}

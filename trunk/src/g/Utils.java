package g;

import static java.lang.Math.*;

public class Utils {
	/**
	 * @return an angle that is equal to given one and that is within the range [-180 .. 180]
	 */
	static double normalizeAngle(double direction) {
		while (direction < -180)
			direction += 360;
		while (direction > 180)
			direction -= 360;
		return direction;
	}

	static double bulletSpeed(double bulletPower) {
		return 20 - max(3, bulletPower) * 3;
	}

	static double absoluteBearing(double x1, double y1, double x2, double y2) {
		return toDegrees(atan2(x2 - x1, y2 - y1));
	}
}

package g;

import static java.lang.Math.*;

import java.awt.geom.Point2D;

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
		return 20 - bulletPower * 3;
	}

	static double absoluteDirection(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = toDegrees(asin(xo / hyp));
		double bearing = 0;
		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
	}
}

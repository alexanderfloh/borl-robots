package us;

public final class Utils {
	
	public static double normalizeAngle(double direction) {
		if (direction < -180)
			direction += 360;
		if (direction > 180)
			direction -= 360;
		if (direction < 0)
			direction += 360;
		
		return direction;
	}

}

package g;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

public class VirtualBullet {
	private long time;
	private double veloctiy;
	private double directionDegrees;
	private Point2D.Double position;
	private boolean isReal;

	public VirtualBullet(double directionDegrees, Double position, long time, double veloctiy, boolean isReal) {
		this.directionDegrees = directionDegrees;
		this.position = position;
		this.time = time;
		this.veloctiy = veloctiy;
		this.isReal = isReal;
	}

	void update(long time) {
		double distanceTravelled = (time - this.time) * veloctiy;
		position.x += sin(toRadians(directionDegrees)) * distanceTravelled;
		position.y += cos(toRadians(directionDegrees)) * distanceTravelled;

		this.time = time;
	}

	public boolean isReal() {
		return isReal;
	}

	boolean isWithinBattleField(Rectangle2D.Double battleFied) {
		return battleFied.contains(position);
	}

	boolean hitTarget(Target target) {
		return target.getRectangle().contains(position);
	}

	public void onPaint(Graphics2D g) {
		g.setColor(isReal ? Color.cyan : Color.white);
		Utils.drawCircle(g, 3, position);
	}
}

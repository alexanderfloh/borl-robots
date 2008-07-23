package g;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VirtualGunArray {
	private List<VirtualGun> guns = new ArrayList<VirtualGun>();

	private Comparator<VirtualGun> bestGunComparator = new BestGunComparator();

	public VirtualGunArray(TehGeckBot geckBot) {
		guns.add(new LinarGun(geckBot, 1));
		for (int i = -20; i < 20; i++) {
			double linearTargetingFactor = 0.1 * i;
			if (linearTargetingFactor != 1)
				guns.add(new LinarGun(geckBot, linearTargetingFactor));
		}
	}

	/**
	 * @param target
	 * @param isRealBullet
	 *            <code>true</code> indicates that the robot fired a real bullet.
	 */
	public void simulateFire(Target target, boolean isRealBullet) {
		for (VirtualGun gun : guns)
			gun.simulateFire(target, isRealBullet);
	}

	public VirtualGun getBestGun() {
		Collections.sort(guns, bestGunComparator);
		return guns.get(0);
	}

	public void onPaint(Graphics2D g) {
		for (VirtualGun gun : guns) {
			gun.onPaint(g);
		}

		// draw the best guns
		Collections.sort(guns, bestGunComparator);
		int gunCount = 10;
		int startY = 10 * gunCount;
		for (int i = 0; i < gunCount; i++)
			g.drawString(i + ") " + guns.get(i).toString(), 10, startY - 10 * i);
	}

	private class BestGunComparator implements Comparator<VirtualGun> {
		public int compare(VirtualGun o1, VirtualGun o2) {
			double hitRatio1 = o1.getHitRatio();
			double hitRatio2 = o2.getHitRatio();
			if (hitRatio1 == hitRatio2) {
				return 0;
			} else if (hitRatio1 < hitRatio2)
				return 1;
			else
				return -1;
		}
	}
}

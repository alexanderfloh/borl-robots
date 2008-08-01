package g;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class VirtualGunArray {
	private List<VirtualGun> guns = new ArrayList<VirtualGun>();

	// only for logging
	private VirtualGun lastBestGun;
	private TehGeckBot geckBot;

	public VirtualGunArray(TehGeckBot geckBot) {
		this.geckBot = geckBot;
		for (int i = -20; i < 20; i++) {
			guns.add(new LinarGun(geckBot, 0.1 * i));
		}
	}

	/**
	 * @param target
	 * @param importance
	 *            the importance of the bullet. E.g. virtual bullets that are fired at the same as real bullets are more
	 *            important than other virtual bullets
	 */
	public void simulateFire(Target target, int importance) {
		for (VirtualGun gun : guns)
			gun.simulateFire(target, importance);
	}

	public VirtualGun getBestGun() {
		VirtualGun bestGun = guns.get(guns.size() / 2); // pick gun in the middle as default gun
		for (VirtualGun gun : guns) {
			if (gun.getHitRatio() > bestGun.getHitRatio())
				bestGun = gun;
		}

		// logging
		if (lastBestGun != bestGun) {
			geckBot.println("best gun = " + bestGun);
			lastBestGun = bestGun;
		}

		return bestGun;
	}

	public void onPaint(Graphics2D g) {
		for (VirtualGun gun : guns)
			gun.onPaint(g);
	}
}

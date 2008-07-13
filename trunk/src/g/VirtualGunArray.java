package g;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class VirtualGunArray {
	private List<VirtualGun> guns = new ArrayList<VirtualGun>();

	public VirtualGunArray(TehGeckBot geckBot) {
		for (int i = -20; i < 20; i++) {
			guns.add(new LinarGun(geckBot, 0.1 * i));
		}
	}

	public void simulateFire(Target target) {
		for (VirtualGun gun : guns)
			gun.simulateFire(target);
	}

	public VirtualGun getBestGun() {
		VirtualGun bestGun = guns.get(guns.size() / 2); // pick gun in the middle as default gun
		for (VirtualGun gun : guns) {
			if (gun.getHitRatio() > bestGun.getHitRatio())
				bestGun = gun;
		}
		return bestGun;
	}

	public void onPaint(Graphics2D g) {
		for (VirtualGun gun : guns)
			gun.onPaint(g);
	}
}

package g;

import static g.Utils.*;
import static java.lang.Math.*;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class VirtualGun {
	protected int hitBullets = 0;
	protected int missedBullets = 0;
	protected Collection<VirtualBullet> flyingBullets = new LinkedList<VirtualBullet>();

	protected TehGeckBot geckBot;

	public VirtualGun(TehGeckBot geckBot) {
		this.geckBot = geckBot;
	}

	void simulateFire(Target target) {
		long time = geckBot.getTime();
		updateFlyingBullets(target, time);
		double firePower = getPower(target);
		VirtualBullet bullet = new VirtualBullet(getAbsoluteBearingDegrees(target), geckBot.getPosition(), time,
				bulletSpeed(firePower));
		flyingBullets.add(bullet);
	}

	private void updateFlyingBullets(Target target, long time) {
		// let all bullets fly
		for (VirtualBullet bullet : flyingBullets)
			bullet.update(time);

		// check which bullets are still flying, and which missed or hit a target
		for (Iterator<VirtualBullet> it = flyingBullets.iterator(); it.hasNext();) {
			VirtualBullet bullet = it.next();
			if (bullet.hitTarget(target)) {
				hitBullets++;
				it.remove();
			} else if (!bullet.isWithinBattleField(geckBot.getBattleField())) {
				missedBullets++;
				it.remove();
			}
		}

		// trim hit data -> thereby newer virtual bullets have more weight
		int allBullets = hitBullets + missedBullets;
		if(allBullets > 100) {
			hitBullets = (int) round(hitBullets / 2.0);
			missedBullets = (int) round(missedBullets / 2.0);
			geckBot.println("trimming hit data");
		}
	}

	double getPower(Target target) {
		if(geckBot.getEnergy() < 20 && getHitRatio() < 0.25)
			return 0.1;
		if (target.getEnergy() <= 12)
			return target.getEnergy() / 4.0;
		return max(1.1, (min(3.0, 1200 / target.getDistance())));
	}

	abstract double getAbsoluteBearingDegrees(Target target);

	double getHitRatio() {
		int allBullets = hitBullets + missedBullets;
		if (allBullets == 0)
			return 1;
		else
			return (double) hitBullets / (double) allBullets;
	}

	public void onPaint(Graphics2D g) {
		for (VirtualBullet bullet : new ArrayList<VirtualBullet>(flyingBullets))
			bullet.onPaint(g);
	}
}

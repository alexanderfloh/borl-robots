package lexx.aim;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lexx.DasBot;
import lexx.VirtualBullet;
import lexx.target.Target;

public abstract class VirtualGun {

  private List<VirtualBullet> trackedBullets = new LinkedList<VirtualBullet>();
  
  private int bulletsFired;
  private int bulletsHit;
  private int bulletsMissed;
  
  protected DasBot robot;
  protected Target target;
  
  protected VirtualGun(DasBot robot, Target target) {
    this.robot = robot;
    this.target = target;
  }
  
  public void doFire() {
    trackedBullets.add(new VirtualBullet(robot.getPosition(), getTargetHeadingRadians(), getPower(), robot.getTime()));
    bulletsFired++;
  }
  
  public final void update() {
    for (Iterator<VirtualBullet> it = trackedBullets.iterator(); it.hasNext();) {
      VirtualBullet bullet = it.next();
      bullet.update(robot.getTime());
      if(bullet.isTargetHit(target)) {
        bulletsHit++;
        it.remove();
      } else if(!bullet.isWithinBattleField(robot.getBattleField())) {
        bulletsMissed++;
        it.remove();
      }
    }
  }
  
  public final void updateTarget(Target target) {
    this.target = target;
  }
  
  public int getBulletsFired() {
    return bulletsFired;
  }

  public int getBulletsHit() {
    return bulletsHit;
  }

  public int getBulletsMissed() {
    return bulletsMissed;
  }

  public abstract double getTargetHeadingRadians();
  
  public abstract double getPower();
  
  protected abstract void onUpdate();

  public void onPaint(Graphics2D g) {
    for (VirtualBullet bullet : trackedBullets) {
      bullet.onPaint(g);
    }
  }
  
  @Override
  public String toString() {
    return getClass() + " f/h/m " + bulletsFired + "/" + bulletsHit + "/" + bulletsMissed;
  }
}

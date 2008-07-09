package lexx;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class VirtualGun {

  private List<TrackedBullet> trackedBullets = new LinkedList<TrackedBullet>();
  
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
    robot.out.println("creating bullet with p = " + getPower() + " and v = " + Utils.powerToVelocity(getPower()));
    trackedBullets.add(new TrackedBullet(robot.getPosition(), getTargetHeadingRadians(), getPower(), robot.getTime()));
    bulletsFired++;
  }
  
  public final void update() {
    for (Iterator<TrackedBullet> it = trackedBullets.iterator(); it.hasNext();) {
      TrackedBullet bullet = it.next();
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

  protected abstract double getTargetHeadingRadians();
  
  protected abstract double getPower();
  
  protected abstract void onUpdate();

  public void onPaint(Graphics2D g) {
    for (TrackedBullet bullet : trackedBullets) {
      bullet.onPaint(g);
    }
  }
  
  @Override
  public String toString() {
    return getClass() + " f/h/m " + bulletsFired + "/" + bulletsHit + "/" + bulletsMissed;
  }
}

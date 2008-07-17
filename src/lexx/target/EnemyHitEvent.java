package lexx.target;

public class EnemyHitEvent {
  private final long timeStamp;
  private final double bulletPower;
  
  public EnemyHitEvent(double bulletPower, long timeStamp) {
    this.bulletPower = bulletPower;
    this.timeStamp = timeStamp;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public double getBulletPower() {
    return bulletPower;
  }
  
  public double getDamage() {
    return 4 * bulletPower + 2 * Math.max(bulletPower - 1 , 0);
  }

  
}

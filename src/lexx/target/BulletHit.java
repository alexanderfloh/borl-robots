package lexx.target;

import lexx.Angle;

public class BulletHit {
  private final Angle bearing;
  private final long timeStamp;
  private final double bulletPower;
  
  public BulletHit(Angle bearing, double bulletPower, long timeStamp) {
    this.bearing = bearing;
    this.bulletPower = bulletPower;
    this.timeStamp = timeStamp;
  }

  public Angle getBearing() {
    return bearing;
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

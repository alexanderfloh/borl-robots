package lexx.target;

import robocode.Condition;

public class EnemyFiredCondition extends Condition {

  private Target target;
  private double lastEnergy;
  private double bulletPower;
  
  public EnemyFiredCondition(Target target) {
    this.target = target;
    this.lastEnergy = target.getEnergy();
  }
  
  @Override
  public boolean test() {
    bulletPower = lastEnergy - target.getEnergy();
    lastEnergy = target.getEnergy();
    return (bulletPower > 0 && bulletPower < 3.1);
  }

  public double getBulletPower() {
    return bulletPower;
  }
}

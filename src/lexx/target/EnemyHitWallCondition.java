package lexx.target;

import robocode.Condition;

public class EnemyHitWallCondition extends Condition {

  final Target target;
  double lastVelocity;
  
  double collisionDamage;
  
  public EnemyHitWallCondition(Target target) {
    this.target = target;
    this.lastVelocity = target.getCurrentState().getVelocity();
  }
  
  @Override
  public boolean test() {
    EnemyState currentTargetState = target.getCurrentState();
    double acceleration = Math.abs(Math.abs(lastVelocity) - Math.abs(currentTargetState.getVelocity()));
    if(acceleration > 2.1) {
      collisionDamage = Math.max(0, Math.abs(lastVelocity) / 2 - 1);
      return true;
    }
    lastVelocity = currentTargetState.getVelocity();
    return false;
  }

  public double getEnergyLoss() {
    return collisionDamage;
  }
  
}

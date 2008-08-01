package lexx.aim;

import java.awt.geom.Point2D;

import lexx.DasBot;
import lexx.Utils;
import lexx.target.EnemyState;
import lexx.target.Target;

public class DirectGun extends VirtualGun {
  private double percentOfMaxVelocity;
  
  public DirectGun(DasBot robot, Target target, double percentOfMaxVelocity) {
    super(robot, target);
    this.percentOfMaxVelocity = percentOfMaxVelocity;
  }

  @Override
  public double getPower() {
    return target.getMaxFirePower();
  }

  @Override
  public double getTargetHeadingRadians() {
    return projectBearingRadiansFixedSpeed(getPower(), percentOfMaxVelocity);
  }

  @Override
  protected void onUpdate() {
    
  }
  
  private double projectBearingRadiansFixedSpeed(double power, double percentOfMaxVelocity) {
    double ticks = ticksToTarget(power);

    EnemyState currentTargetState = target.getCurrentState();
    double projectedDistance = ticks * (DasBot.MAX_SPEED * percentOfMaxVelocity);
    Point2D.Double targetPosition = currentTargetState.getPosition();
    
    Point2D.Double projectedPos = currentTargetState.getHeading().projectPoint(targetPosition, projectedDistance);
    Point2D.Double myPosition = robot.getPosition();

    return Utils.getHeadingRadians(myPosition, projectedPos);
  }
  
  private double ticksToTarget(double power) {
    return (target.getCurrentState().getDistance() / Utils.powerToVelocity(power));
  }
}

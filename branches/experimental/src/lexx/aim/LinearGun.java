package lexx.aim;

import java.awt.geom.Point2D;

import lexx.Angle;
import lexx.DasBot;
import lexx.Utils;
import lexx.target.EnemyState;
import lexx.target.Target;

public class LinearGun extends VirtualGun {
  
  protected LinearGun(DasBot robot, Target target) {
    super(robot, target);
  }

  @Override
  public double getPower() {
    return target.getMaxFirePower();
  }

  @Override
  public double getTargetHeadingRadians() {
    return projectBearingRadians(getPower(), 1);
  }

  @Override
  protected void onUpdate() {
    
  }
  
  private double projectBearingRadians(double power, double velocityFactor) {
    double ticks = ticksToTarget(power);

    EnemyState currentTargetState = target.getCurrentState();
    Point2D.Double targetPosition = currentTargetState.getPosition();
    Angle targetHeading = currentTargetState.getHeading();
    double projectedDistance = ticks * (currentTargetState.getVelocity() * velocityFactor);
    
    Point2D.Double projectedPos = targetHeading.projectPoint(targetPosition, projectedDistance);
    Point2D.Double myPosition = robot.getPosition();
    
    double projectedBearing = Utils.getHeadingRadians(myPosition, projectedPos);
    return projectedBearing;
  }


  
  private double ticksToTarget(double power) {
    return (target.getCurrentState().getDistance() / Utils.powerToVelocity(power));
  }
}

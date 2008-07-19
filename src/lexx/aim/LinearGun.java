package lexx.aim;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

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
    Point2D.Double projectedPos = Utils.translate(currentTargetState.getPosition(), currentTargetState.getHeadingRadians(), ticks * (currentTargetState.getVelocity() * velocityFactor));

    Double myPosition = robot.getPosition();
    double relPosX = projectedPos.x - myPosition.x;
    double relPosY = projectedPos.y - myPosition.y;
    double projectedDistance = myPosition.distance(projectedPos);

    double projectedBearing = Utils.getBearingForPointRadians(relPosX, relPosY, projectedDistance);
    return projectedBearing;
  }
  
  private double ticksToTarget(double power) {
    return (target.getCurrentState().getDistance() / Utils.powerToVelocity(power));
  }
}

package lexx;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class LinearGun extends VirtualGun {
  
  protected LinearGun(DasBot robot, Target target) {
    super(robot, target);
  }

  @Override
  protected double getPower() {
    return target.getMaxFirePower();
  }

  @Override
  protected double getTargetHeadingRadians() {
    return projectBearingRadians(getPower(), 1);
  }

  @Override
  protected void onUpdate() {
    
  }
  
  private double projectBearingRadians(double power, double velocityFactor) {
    double ticks = ticksToTarget(power);

    Point2D.Double projectedPos = Utils.translate(target.getEnemyPos(), target.getHeadingRadians(), ticks * (target.getVelocity() * velocityFactor));

    Double myPosition = robot.getPosition();
    double relPosX = projectedPos.x - myPosition.x;
    double relPosY = projectedPos.y - myPosition.y;
    double projectedDistance = myPosition.distance(projectedPos);

    double projectedBearing = Utils.getBearingForPointRadians(relPosX, relPosY, projectedDistance);
    return projectedBearing;
  }
  
  private double ticksToTarget(double power) {
    return (target.getDistance() / Utils.powerToVelocity(power));
  }
}

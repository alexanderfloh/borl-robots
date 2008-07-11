package lexx;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class DirectGun extends VirtualGun {
  private double percentOfMaxVelocity;
  
  public DirectGun(DasBot robot, Target target, double percentOfMaxVelocity) {
    super(robot, target);
    this.percentOfMaxVelocity = percentOfMaxVelocity;
  }

  @Override
  protected double getPower() {
    return target.getMaxFirePower();
  }

  @Override
  protected double getTargetHeadingRadians() {
    return projectBearingRadiansFixedSpeed(getPower(), percentOfMaxVelocity);
  }

  @Override
  protected void onUpdate() {
    
  }
  
  private double projectBearingRadiansFixedSpeed(double power, double percentOfMaxVelocity) {
    double ticks = ticksToTarget(power);

    Point2D.Double projectedPos = Utils.translate(target.getEnemyPos(), target.getHeadingRadians(), ticks * (DasBot.MAX_SPEED * percentOfMaxVelocity));

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

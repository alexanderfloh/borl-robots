package lexx;

public class LinearGun extends VirtualGun {
  private double velocityFactor;
  
  protected LinearGun(DasBot robot, Target target, double velocityFactor) {
    super(robot, target);
    this.velocityFactor = velocityFactor;
  }

  @Override
  protected double getPower() {
    return target.getMaxFirePower();
  }

  @Override
  protected double getTargetHeadingRadians() {
    return target.projectBearingRadians(getPower(), velocityFactor);
  }

  @Override
  protected void onUpdate() {
    
  }
  
  @Override
  public String toString() {
    return super.toString() + " velocityFactor " + velocityFactor;
  }

}

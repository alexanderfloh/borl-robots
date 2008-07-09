package lexx;

public class DirectGun extends VirtualGun {
  
  public DirectGun(DasBot robot, Target target) {
    super(robot, target);
  }

  @Override
  protected double getPower() {
    return target.getMaxFirePower();
  }

  @Override
  protected double getTargetHeadingRadians() {
    return target.getBearingRadians();
  }

  @Override
  protected void onUpdate() {
    
  }

}

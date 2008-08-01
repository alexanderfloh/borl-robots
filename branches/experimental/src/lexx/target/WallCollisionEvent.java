package lexx.target;

public class WallCollisionEvent {
  private final double energyLoss;
  private final long timeStamp;

  public WallCollisionEvent(double energyLoss, long timeStamp) {
    this.energyLoss = energyLoss;
    this.timeStamp = timeStamp;
  }

  public double getEnergyLoss() {
    return energyLoss;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

}

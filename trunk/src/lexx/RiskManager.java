package lexx;

import java.awt.geom.Point2D;

import robocode.util.Utils;

public class RiskManager {
  private static final int HIT_BUCKETS = 10;
  private final int[] hitBuckets = new int[HIT_BUCKETS];
  
  public void logHitByEnemy(Wave wave, Point2D.Double myPosition) {
    Angle actualHeading = lexx.Utils.getHeading(wave.getOrigin(), myPosition);
    Angle originalHeading = wave.getHeading();
    Angle diff = actualHeading.substract(originalHeading);
    int slot = (HIT_BUCKETS / 2) + (int)(Math.toDegrees(Utils.normalRelativeAngle(diff.getAngle())) / (180 / HIT_BUCKETS));
    //TODO check array bounds
    hitBuckets[slot] += 1;
  }
  
  private int getAverageBucketValue() {
    int sum = 0;
    for (int value : hitBuckets) {
      sum += value;
    }
    return sum / HIT_BUCKETS;
  }
  
  public double getMovementAdvice() {
    int average = getAverageBucketValue();
    int safeSlot = -1;
    boolean foundSafeSlot = false;
    int clockWiseIndex = HIT_BUCKETS / 2;
    int counterClockWiseIndex = clockWiseIndex;
    while(!foundSafeSlot && clockWiseIndex >= 0 && counterClockWiseIndex < HIT_BUCKETS) {
      if(hitBuckets[clockWiseIndex] < average) {
        safeSlot = clockWiseIndex;
        foundSafeSlot = true;
      }
      if(hitBuckets[counterClockWiseIndex] < average) {
        safeSlot = counterClockWiseIndex;
        foundSafeSlot = true;
      }
      clockWiseIndex++;
      counterClockWiseIndex++;
    }
    
    safeSlot -= HIT_BUCKETS / 2;
    
    return Math.toRadians(safeSlot * 180 / HIT_BUCKETS);
  }
}

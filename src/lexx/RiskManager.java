package lexx;

import java.awt.geom.Point2D;
import java.util.Arrays;

import robocode.util.Utils;

public class RiskManager {
  private static final int HIT_BUCKETS = 10;
  private final int[] hitBuckets = new int[HIT_BUCKETS];
  private final DasBot robot;
  
  public RiskManager(DasBot robot) {
    this.robot = robot;
  }
  
  public void logHitByEnemy(Wave wave, Point2D.Double myPosition) {
    Angle actualHeading = lexx.Utils.getHeading(wave.getOrigin(), myPosition);
    Angle originalHeading = wave.getHeading();
    Angle diff = actualHeading.substract(originalHeading);
    int slot = (HIT_BUCKETS / 2) + (int)(Math.toDegrees(Utils.normalRelativeAngle(diff.getAngle())) / (90 / HIT_BUCKETS));
    //TODO check array bounds
    hitBuckets[slot] += 1;
    robot.out.println(Arrays.toString(hitBuckets));
  }
  
  private int getAverageBucketValue() {
    int sum = 0;
    for (int value : hitBuckets) {
      sum += value;
    }
    return sum / HIT_BUCKETS;
  }
  
  public Angle getMovementAdvice() {
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
    
    return Angle.normalizedAbsoluteAngle(Math.PI + Math.toRadians(safeSlot * 90 / HIT_BUCKETS));
  }
}

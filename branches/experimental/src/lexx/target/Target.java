package lexx.target;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lexx.Angle;
import lexx.DasBot;
import lexx.RiskManager;
import lexx.Wave;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Target {

  private static final int MAX_HISTORY_SIZE = 100;

  private final DasBot robot;
  private final String targetName;
  
  private EnemyState currentEnemyState;
  
  private final List<EnemyState> history;
  private final List<BulletHit> myHitsLog;
  private final List<WallCollisionEvent> enemyWallCollisionLog;
  private final List<Wave> enemyWaves = new LinkedList<Wave>();
  
  private final RiskManager riskManager;

  public Target(DasBot robot, ScannedRobotEvent event) {
    this.robot = robot;
    this.targetName = event.getName();
    this.history = new LinkedList<EnemyState>();
    this.myHitsLog = new LinkedList<BulletHit>();
    this.enemyWallCollisionLog = new LinkedList<WallCollisionEvent>();
    this.riskManager = new RiskManager(robot);

    update(event);
  }

  public void update(ScannedRobotEvent event) {
    double distance = event.getDistance();
    double energy = event.getEnergy();

    if (currentEnemyState != null)
      history.add(0, currentEnemyState);

    if (history.size() > MAX_HISTORY_SIZE)
      history.remove(history.size() - 1);

    Angle myHeading = new Angle(robot.getHeadingRadians());
    Angle targetHeading = new Angle(event.getHeading());
    Angle targetBearing = new Angle(event.getBearingRadians());
    Angle targetAbsoluteBearing = myHeading.add(targetBearing);

    Point2D.Double myPos = robot.getPosition();
    Point2D.Double enemyPos = targetAbsoluteBearing.projectPoint(myPos, distance);
    currentEnemyState = new EnemyState(enemyPos, targetHeading, targetAbsoluteBearing, event.getVelocity(), distance, energy, event.getTime());
  }
  
  public void update() {
    for (Iterator<Wave> it = enemyWaves.iterator(); it.hasNext();) {
      Wave wave = it.next();
      wave.update(robot.getTime());
      if(wave.getMovedDistance() > (wave.getOrigin().distance(robot.getPosition()) + DasBot.ROBOT_SIZE)) {
        it.remove();
      }
    }
  }

  public String getTargetName() {
    return targetName;
  }

  public double getMaxFirePower() {
    // if the enemy has less than 0.1 energy left, it is most likely disabled
    // and cant move
    // shoot with the least amount of energy possible.
    if (currentEnemyState.getEnergy() < 0.1)
      return 0.1;

    if (robot.getEnergy() < 10)
      return 0;

    double ticks = 50; // TODO hardcoded
    double requiredVelocity = currentEnemyState.getDistance() / ticks;
    double power = Math.min(3, (20 - requiredVelocity) / 3);

    if (robot.getEnergy() < 40) {
      power = Math.min(0.1, power);
    }

    return power;
  }
  
  public void logHit(double bulletPower, long timeStamp) {
    myHitsLog.add(0, new BulletHit(Angle.NORTH, bulletPower, timeStamp));
  }
  
  public void logWallCollision(double energyLoss, long timeStamp) {
    enemyWallCollisionLog.add(0, new WallCollisionEvent(energyLoss, timeStamp));
  }
  
  public void logHitByEnemy(Angle angle, double bulletPower, long timeStamp) {
//    Rectangle2D.Double robotRectangle = robot.getRectangle();
    for (Wave wave : enemyWaves) {
      double distanceDiff = wave.getMovedDistance() -  wave.getOrigin().distance(robot.getPosition());
      boolean intersectsWave = distanceDiff >= 0 && distanceDiff <= DasBot.ROBOT_SIZE;
      if(intersectsWave && Utils.isNear(bulletPower, wave.getPower())) {
        // this is very likely the bullet that hit us...
        // calculate the difference between the original heading and the current heading towards the enemy
        riskManager.logHitByEnemy(wave, robot.getPosition());
      }
    }
  }
  
  public BulletHit findHitEventForTimeStamp(long timeStamp) {
    for (BulletHit hitEvent : myHitsLog) {
      if(timeStamp == hitEvent.getTimeStamp())
        return hitEvent;
      else if (timeStamp > hitEvent.getTimeStamp())
        return null;
    }
    return null;
  }
  
  public WallCollisionEvent findWallCollisionEventForTimeStamp(long timeStamp) {
    for(WallCollisionEvent event : enemyWallCollisionLog) {
      if(timeStamp == event.getTimeStamp())
        return event;
      else if(timeStamp > event.getTimeStamp())
        return null;
    }
    return null;
  }

  public EnemyState getHistoricState(int ticks) {
    if(ticks < 0 || ticks >= history.size())
      return null;
    return history.get(ticks);
  }
  
  public void trackEnemyWave(double power, long time) {
    int ticksDiff = (int) (robot.getTime() - time);
    EnemyState fireState = getHistoricState(ticksDiff);
    EnemyState scannedState = getHistoricState(ticksDiff + 1);
    BulletHit enemyHitEvent = findHitEventForTimeStamp(time - 1);
    WallCollisionEvent enemyWallCollisionEvent = findWallCollisionEventForTimeStamp(time);
   
    boolean hitByBullet = enemyHitEvent != null && Utils.isNear(power, enemyHitEvent.getDamage());
    boolean wallCollision = enemyWallCollisionEvent != null;
    if (fireState != null && scannedState != null && !hitByBullet && !wallCollision) {
      Wave wave = new Wave(fireState.getPosition(), new Angle(scannedState.getBearingRadians() + Math.PI), lexx.Utils.powerToVelocity(power), power, fireState.getTimeStamp());
      enemyWaves.add(wave);
    }
  }
  
  public Wave getNextWave() {
    if(!enemyWaves.isEmpty())
      return enemyWaves.get(0);
    return null;
  }

  public void onPaint(Graphics2D g) {
    g.setColor(Color.RED);
    g.draw(currentEnemyState.getRectangle());
    for (Wave wave : enemyWaves) {
      wave.onPaint(g);
    }
  }

  public EnemyState getCurrentState() {
    return currentEnemyState;
  }

  public RiskManager getRiskManager() {
    return riskManager;
  }
}

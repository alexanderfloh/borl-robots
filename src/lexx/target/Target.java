package lexx.target;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import lexx.Angle;
import lexx.DasBot;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class Target {

  private static final int MAX_HISTORY_SIZE = 20;

  private final AdvancedRobot robot;
  private final String targetName;
  
  private EnemyState currentEnemyState;
  
  private final List<EnemyState> history;
  private final List<EnemyHitEvent> enemyHitLog;
  private final List<WallCollisionEvent> enemyWallCollisionLog; 

  public Target(AdvancedRobot robot, ScannedRobotEvent event) {
    this.robot = robot;
    this.targetName = event.getName();
    this.history = new LinkedList<EnemyState>();
    this.enemyHitLog = new LinkedList<EnemyHitEvent>();
    this.enemyWallCollisionLog = new LinkedList<WallCollisionEvent>();

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
    Angle targetAbsoluteBearing = myHeading.addAngle(targetBearing);

    Point2D.Double myPos = new Point2D.Double(robot.getX(), robot.getY());
    Point2D.Double enemyPos = targetAbsoluteBearing.projectPoint(myPos, distance);
    currentEnemyState = new EnemyState(enemyPos, targetHeading, targetAbsoluteBearing, event.getVelocity(), distance, energy, event.getTime());
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
    enemyHitLog.add(0, new EnemyHitEvent(bulletPower, timeStamp));
  }
  
  public void logWallCollision(double energyLoss, long timeStamp) {
    enemyWallCollisionLog.add(0, new WallCollisionEvent(energyLoss, timeStamp));
  }
  
  public EnemyHitEvent findHitEventForTimeStamp(long timeStamp) {
    for (EnemyHitEvent hitEvent : enemyHitLog) {
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
    return history.get(ticks);
  }

  public void onPaint(Graphics2D g) {
    g.setColor(Color.RED);
    g.draw(currentEnemyState.getRectangle());
    
    int transparency = 10;
    int age = 0;
    g.setPaint(new Color(age, 255 - age, 0, transparency));
    for (EnemyState state : history) {
      Ellipse2D.Double circle = new Ellipse2D.Double(
          state.getPosition().x - DasBot.ROBOT_SIZE / 2, 
          state.getPosition().y - DasBot.ROBOT_SIZE / 2, 
          DasBot.ROBOT_SIZE, 
          DasBot.ROBOT_SIZE);
      g.fill(circle);
      age += 255 / MAX_HISTORY_SIZE;
      transparency = 255 / age;
      g.setPaint(new Color(age, 255 - age, 0, transparency));
    }
  }

  public EnemyState getCurrentState() {
    return currentEnemyState;
  }
}

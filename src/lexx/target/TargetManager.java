package lexx.target;

import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map;

import lexx.DasBot;
import lexx.aim.GunManager;
import robocode.Condition;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TargetManager {
  private Map<String, GunManager> gunManagers = new LinkedHashMap<String, GunManager>();
  private GunManager currentGunManager;

  private Target currentTarget;
  private Map<String, Target> oldTargets = new LinkedHashMap<String, Target>();

  private DasBot robot;

  // TODO move this functionality
  
  private Condition currentEnemyFiredCondition;
  private Condition currentEnemyHitWallCondition;

  public TargetManager(DasBot robot) {
    this.robot = robot;
  }

  public void update() {
    if (currentTarget != null && robot.getTime() - currentTarget.getCurrentState().getTimeStamp() > 3) {
      currentTarget = null;
    }

    for (GunManager gunManager : gunManagers.values()) {
      gunManager.update();
    }
  }

  public void startRound() {
    currentTarget = null;
  }

  public void onScannedRobot(ScannedRobotEvent event) {
    updateTarget(event);
    updateGunManager(event, currentTarget);
  }

  public Target getCurrentTarget() {
    return currentTarget;
  }

  public Target getTargetForName(String name) {
    if (currentTarget != null && currentTarget.getTargetName().equals(name))
      return currentTarget;
    else
      return oldTargets.get(name);
  }

  public GunManager getCurrentGunManager() {
    return currentGunManager;
  }

  private void updateTarget(ScannedRobotEvent event) {
    if (currentTarget != null && currentTarget.getTargetName().equals(event.getName())) {
      currentTarget.update(event);
    } else if (currentTarget == null || currentTarget.getCurrentState().getDistance() > event.getDistance()) {
      // save old target
      if (currentTarget != null) {
        oldTargets.put(currentTarget.getTargetName(), currentTarget);
        robot.removeCustomEvent(currentEnemyFiredCondition);
        robot.removeCustomEvent(currentEnemyHitWallCondition);
      }

      currentTarget = findOrCreateTarget(robot, event);
      currentEnemyFiredCondition = new EnemyFiredCondition(currentTarget);
      currentEnemyHitWallCondition = new EnemyHitWallCondition(currentTarget);
      robot.addCustomEvent(currentEnemyFiredCondition);
      robot.addCustomEvent(currentEnemyHitWallCondition);

    }
  }

  private Target findOrCreateTarget(DasBot robot, ScannedRobotEvent event) {
    Target target = oldTargets.get(event.getName());
    if (target == null)
      target = new Target(robot, event);
    return target;
  }

  private void updateGunManager(ScannedRobotEvent event, Target newTarget) {
    GunManager newManager = gunManagers.get(event.getName());
    if (newManager == null) {
      newManager = new GunManager(robot, newTarget);
      gunManagers.put(event.getName(), newManager);
    }
    currentGunManager = newManager;
    currentGunManager.updateTarget(newTarget);
  }

  public void onPaint(Graphics2D g) {
    if (currentTarget != null) {
      currentTarget.onPaint(g);
    }
    if (currentGunManager != null) {
      currentGunManager.onPaint(g);
    }
  }

  public void onRobotDeath(RobotDeathEvent event) {
    if (currentTarget != null && currentTarget.getTargetName().equals(event.getName())) {
      currentTarget = null;
    }
  }
}

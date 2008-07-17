package lexx.target;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lexx.DasBot;
import lexx.VirtualBullet;
import lexx.aim.GunManager;

import robocode.Condition;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TargetManager {
  private Map<String, GunManager> gunManagers = new LinkedHashMap<String, GunManager>();
  private GunManager currentGunManager;
  
  private Target currentTarget;
  private Map<String, Target> oldTargets = new LinkedHashMap<String, Target>();
  private Condition currentEnemyFiredCondition;
  
  private DasBot robot;
  
  // TODO move this functionality
  private List<VirtualBullet> enemyBullets = new LinkedList<VirtualBullet>();

  public TargetManager(DasBot robot) {
    this.robot = robot;
  }
  
  public void update() {
    if(currentTarget != null && robot.getTime() - currentTarget.getTimeStamp() > 3) {
      currentTarget = null;
    }
    
    for (GunManager gunManager : gunManagers.values()) {
      gunManager.update();
    }
    
    for(Iterator<VirtualBullet> it = enemyBullets.iterator(); it.hasNext(); ) {
      VirtualBullet bullet = it.next();
      bullet.update(robot.getTime());
      if(!bullet.isWithinBattleField(robot.getBattleField())) {
        it.remove();
      }
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

  public GunManager getCurrentGunManager() {
    return currentGunManager;
  }

  private void updateTarget(ScannedRobotEvent event) {
    if(currentTarget != null && currentTarget.getTargetName().equals(event.getName())) {
      currentTarget.update(event);
    } else if(currentTarget == null || currentTarget.getDistance() > event.getDistance()) {
      // save old target
      if(currentTarget != null) {
        oldTargets.put(currentTarget.getTargetName(), currentTarget);
        robot.removeCustomEvent(currentEnemyFiredCondition);
      }
      
      currentTarget = findOrCreateTarget(robot, event);
      currentEnemyFiredCondition = new EnemyFiredCondition(currentTarget);
      robot.addCustomEvent(currentEnemyFiredCondition);
      
    }
  }
  
  private Target findOrCreateTarget(DasBot robot, ScannedRobotEvent event) {
    Target target = oldTargets.get(event.getName());
    if(target == null) 
      target = new Target(robot, event);
    return target;
  }
  
  private void updateGunManager(ScannedRobotEvent event, Target newTarget) {
    GunManager newManager = gunManagers.get(event.getName());
    if(newManager == null) {
      newManager = new GunManager(robot, newTarget);
      gunManagers.put(event.getName(), newManager);
    }
    currentGunManager = newManager;
    currentGunManager.updateTarget(newTarget);
  }

  public void onPaint(Graphics2D g) {
    if(currentTarget != null) {
      currentTarget.onPaint(g);
    }
    if(currentGunManager != null) {
      currentGunManager.onPaint(g);
    }
    for (VirtualBullet bullet : enemyBullets) {
      bullet.onPaint(g);
    }
  }

  public void onRobotDeath(RobotDeathEvent event) {
    if (currentTarget != null && currentTarget.getTargetName().equals(event.getName())) {
      currentTarget = null;
    }    
  }

  public void trackEnemyBullet(double power, long time) {
    int ticksDiff = (int)(robot.getTime() - time);
    EnemyState fireState = currentTarget.getHistoricState(ticksDiff);
    EnemyState scannedState = currentTarget.getHistoricState(ticksDiff + 1); 
    
    VirtualBullet enemyBullet = new VirtualBullet(fireState.getEnemyPosition(), scannedState.getBearingRadians() + Math.PI, power, fireState.getTimeStamp());
    enemyBullet.setColor(Color.RED);
    enemyBullets.add(enemyBullet);
  }

}

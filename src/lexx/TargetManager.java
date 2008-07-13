package lexx;

import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map;

import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TargetManager {
  private Map<String, GunManager> gunManagers = new LinkedHashMap<String, GunManager>();
  private GunManager currentGunManager;
  
  private Target currentTarget;
  private Map<String, Target> oldTargets = new LinkedHashMap<String, Target>();
  
  private DasBot robot;

  public TargetManager(DasBot robot) {
    this.robot = robot;
  }
  
  public void update() {
    for (GunManager gunManager : gunManagers.values()) {
      gunManager.update();
    }
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
      }
      
      // create new target
      currentTarget = new Target(robot, event);
    }
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
    
  }

  public void onRobotDeath(RobotDeathEvent event) {
    if (currentTarget != null && currentTarget.getTargetName().equals(event.getName())) {
      currentTarget = null;
    }    
  }

}

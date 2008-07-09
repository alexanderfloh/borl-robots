package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class DasBot extends AdvancedRobot {
  public static final int ROBOT_SIZE = 36;

  // this is our memory ;)
  /* static */ private Map<String, GunManager> gunManagers;
  
  private Target currentTarget;
  private Map<String, Target> oldTargets = new LinkedHashMap<String, Target>();
  private GunManager gunManager;

  private int direction = 1;
  private int radarDirection = 1;
  private Random random = new Random();
  private double wallStick = 120;
  private Rectangle2D.Double battleField;
  
  public DasBot() {
    if(gunManagers == null) {
      gunManagers = new LinkedHashMap<String, GunManager>();
    }
  }
  
  @Override
  public void run() {
    if(battleField == null) {
      battleField = new Rectangle2D.Double(0, 0, getBattleFieldWidth(), getBattleFieldHeight());
      setColors(new Color(0, 128, 0), Color.green, Color.gray, Color.magenta, Color.magenta);
    }
    
    while (true) {
      // if the target hasnt been updated for 3 ticks, remove it.
      if (currentTarget != null && currentTarget.getTimeStamp() < getTime() - 3) {
        currentTarget = null;
      }

      if (currentTarget == null) {
        setAdjustRadarForGunTurn(false);
        setAdjustRadarForRobotTurn(false);
        setTurnRadarLeftRadians(Math.PI / 4);
      }

      if (currentTarget != null && gunManager != null) {
//        double maxFirePower = currentTarget.getMaxFirePower();
//        double actualPower = random.nextDouble() * maxFirePower;
//        double projectedBearingRadians = currentTarget.projectBearingRadians(actualPower);
//        
        gunManager.update();
        VirtualGun bestGun = gunManager.getBestGun();
        out.println("Using gun: " + bestGun.getClass() + " with " + bestGun.getBulletsHit() + " hits.");
        double maxFirePower = bestGun.getPower();
        double projectedBearingRadians = bestGun.getTargetHeadingRadians();
        

        // moving
        double headingRadians = getHeadingRadians();
        double turnRadians = getTurnRadians(currentTarget.getBearingRadians(), headingRadians);
        // turnRadians = checkForWalls(turnRadians, headingRadians);

        // shooting
        double turnGunRadians = Utils.normalRelativeAngle((projectedBearingRadians - getGunHeadingRadians()));

        // scanning
        double turnRadarRadians = Utils.normalRelativeAngle(currentTarget.getBearingRadians() - getRadarHeadingRadians());

        if (Math.abs(turnGunRadians) < Math.PI / 100) {
          if (maxFirePower < 0.1) {
            // TODO: get closer to the target
          } else {
            if(Utils.isNear(getGunHeat(), 0)) {
              setFireBullet(maxFirePower);
              gunManager.trackFire();
            }
          }
        }

        setAdjustRadarForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        
        avoidWalls(turnRadians, headingRadians);
        setTurnGunRightRadians(turnGunRadians);

        if (getRadarTurnRemainingRadians() == 0) {
          setTurnRadarRightRadians(turnRadarRadians + ((Math.PI / 12) * radarDirection));
          radarDirection *= -1;
        }

        if (getDistanceRemaining() == 0) {
          reverseDirection();
          double distanceToMove = 100 * (1 + random.nextDouble()) * direction;
          setAhead(distanceToMove);
        }
      }

      execute();
    }
  }

  private void avoidWalls(double turnRadians, double headingRadians) {
    double newHeading = headingRadians + turnRadians;
    Double myPos = getPosition();
    Point2D.Double p1 = lexx.Utils.translate(myPos, newHeading, wallStick * direction);
    
    if (!battleField.contains(p1)) {
      reverseDirection();
      double distanceToMove = 120 * (1 + random.nextDouble()) * direction;
      setAhead(distanceToMove);
    } else {
      setTurnRightRadians(turnRadians);
    }
  }

  private void reverseDirection() {
    direction = direction * -1;
  }

  private double getTurnRadians(double projectedBearingRadians, double headingRadians) {
    double robotTurnRight = projectedBearingRadians - headingRadians + Math.PI / 2;

    double robotTurnLeft = -(Math.PI - robotTurnRight);

    double minTurn;
    if (Math.abs(robotTurnRight) < Math.abs(robotTurnLeft)) {
      minTurn = robotTurnRight;
    } else {
      minTurn = robotTurnLeft;
    }
    return minTurn;
  }

  @Override
  public void onHitWall(HitWallEvent event) {
    out.println("ouch, hit a wall!");
  }

  @Override
  public void onHitRobot(HitRobotEvent event) {
    reverseDirection();
  }

  @Override
  public void onScannedRobot(ScannedRobotEvent event) {
    if (currentTarget == null) {
      currentTarget = new Target(this, event);
      if(gunManagers.containsKey(event.getName())) {
        gunManager = gunManagers.get(event.getName());
        gunManager.updateTarget(currentTarget);
      } else {
        gunManager = new GunManager(this, currentTarget);
        gunManagers.put(event.getName(), gunManager);
      }
      
    } else {
      String currentTargetName = currentTarget.getTargetName();
      String newTargetName = event.getName();
      
      if (newTargetName.equals(currentTargetName)) {
        currentTarget.update(event);
      } else if (event.getDistance() < currentTarget.getDistance()) {
        oldTargets.put(currentTargetName, currentTarget);
        
        if (oldTargets.containsKey(newTargetName)) {
          currentTarget = oldTargets.get(newTargetName);
          gunManager = gunManagers.get(newTargetName);
          currentTarget.update(event);
          
        } else {
          currentTarget = new Target(this, event);
          if(gunManagers.containsKey(event.getName())) {
            gunManager = gunManagers.get(event.getName());
            gunManager.updateTarget(currentTarget);
          } else {
            gunManager = new GunManager(this, currentTarget);
            gunManagers.put(event.getName(), gunManager);
          }
        }
      }
    }
  }

  @Override
  public void onRobotDeath(RobotDeathEvent event) {
    if (currentTarget != null && currentTarget.getTargetName().equals(event.getName())) {
      out.println("die other robots!");
      currentTarget = null;
    }
  }

  @Override
  public void onPaint(Graphics2D g) {
    super.onPaint(g);
    if (currentTarget != null) {
      currentTarget.onPaint(g);
    }
    if(gunManager != null) {
      gunManager.onPaint(g);
    }
  }
  
  public Point2D.Double getPosition() {
    return new Point2D.Double(getX(), getY());
  }
  
  public Rectangle2D.Double getBattleField() {
    return battleField;
  }
}

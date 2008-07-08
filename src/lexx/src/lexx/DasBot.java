package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.Bullet;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class DasBot extends AdvancedRobot {

  private Target currentTarget;
  private int direction = 1;
  private int radarDirection = 1;
  private Random random = new Random();
  private double wallStick = 60;
  private Rectangle2D.Double battleField;
  private List<TrackedBullet> trackedBullets = new LinkedList<TrackedBullet>();
  private Map<String, Target> oldTargets = new LinkedHashMap<String, Target>();

  @Override
  public void run() {
    setColors(Color.green, Color.green, Color.green, Color.magenta, Color.magenta);
    if(battleField == null) {
      battleField = new Rectangle2D.Double(36, 36, getBattleFieldWidth() - 36, getBattleFieldHeight() - 36);
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

      if (currentTarget != null) {
        double maxFirePower = currentTarget.getMaxFirePower();
        double actualPower = random.nextDouble() * maxFirePower;
        double projectedBearingRadians = currentTarget.projectBearingRadians(actualPower);

        checkTrackedBullets();
        
        // moving
        double headingRadians = getHeadingRadians();
        double turnRadians = getTurnRadians(currentTarget.getBearingRadians(), headingRadians);
        // turnRadians = checkForWalls(turnRadians, headingRadians);

        // shooting
        double turnGunRadians = getTurnGunRadians(projectedBearingRadians);

        // scanning
        double turnRadarRadians = getTurnRadarRadians();

        if (Math.abs(turnGunRadians) < Math.PI / 100) {
          if (maxFirePower < 0.1) {
            //TODO: get closer to the target
          } else {
            Bullet bullet = setFireBullet(actualPower);
            if(bullet != null) {
              trackedBullets.add(new TrackedBullet(this, currentTarget, bullet));
            }
          }
        }

        setAdjustRadarForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        setTurnRightRadians(turnRadians);
        avoidWalls(turnRadians, headingRadians);
        setTurnGunRightRadians(turnGunRadians);
        
        if(getRadarTurnRemainingRadians() == 0) {
          setTurnRadarRightRadians(turnRadarRadians + ((Math.PI / 12) * radarDirection));
          radarDirection *= -1;
        }

        if (getDistanceRemaining() == 0) {
          reverseDirection();
          double distanceToMove = 60 * (1 + random.nextDouble()) * direction;
          setAhead(distanceToMove);
        }
      }

      execute();
    }
  }
  
  private void checkTrackedBullets() {
    for (TrackedBullet bullet : trackedBullets) {
      bullet.update();
    }
    
  }

  private void avoidWalls(double turnRadians, double headingRadians) {
    double newHeading = headingRadians + turnRadians;
    Point2D.Double p1 = new Point2D.Double(getX() + Math.cos(newHeading) * wallStick * direction, getY() + Math.sin(newHeading) * wallStick * direction);
    
    if(!battleField.contains(p1)) {
      reverseDirection();
      double distanceToMove = 60 * (1 + random.nextDouble()) * direction;
      setAhead(distanceToMove);
    }
  }

  private void reverseDirection() {
    direction = direction * -1;
  }

  private double getTurnRadarRadians() {
    double bearingRadians = ((2 * Math.PI) + currentTarget.getBearingRadians()) % (2 * Math.PI);
    double turnRadarRight = (bearingRadians - getRadarHeadingRadians());
    double turnRadarLeft = (2 * Math.PI - turnRadarRight) % (2 * Math.PI);
    double turnRadarRadians;
    if (Math.abs(turnRadarRight) < Math.abs(turnRadarLeft)) {
      turnRadarRadians = turnRadarRight;
    } else {
      turnRadarRadians = -turnRadarLeft;
    }
    return turnRadarRadians;
  }

  private double getTurnGunRadians(double projectedBearingRadians) {
    double gunHeadingRadians = getGunHeadingRadians();
    double turnGunRight = ((2 * Math.PI) + projectedBearingRadians - gunHeadingRadians)
        % (2 * Math.PI);

    double turnGunLeft = (2 * Math.PI - turnGunRight) % (2 * Math.PI);
    double turnGunRadians;
    if (Math.abs(turnGunRight) < Math.abs(turnGunLeft)) {
      turnGunRadians = turnGunRight;
    } else {
      turnGunRadians = -turnGunLeft;
    }
    return turnGunRadians;
  }

  private double getTurnRadians(double projectedBearingRadians, double headingRadians) {
    double robotTurnRight = projectedBearingRadians - headingRadians + Math.PI / 2;
    
    // if the needed turn is less than 30¡, do not turn
//    if(Math.abs(robotTurnRight - (headingRadians + Math.PI/2)) < Math.PI / 12)
//      return 0;
    
    double robotTurnLeft = -(Math.PI - robotTurnRight);
    
    double minTurn;
    if(Math.abs(robotTurnRight) < Math.abs(robotTurnLeft)) {
      minTurn = robotTurnRight;
    } else {
      minTurn = robotTurnLeft;
    }
    return minTurn;
  }

  @Override
  public void onHitWall(HitWallEvent event) {
//    reverseDirection();
  }

  @Override
  public void onHitRobot(HitRobotEvent event) {
    reverseDirection();
  }

  @Override
  public void onScannedRobot(ScannedRobotEvent event) {
    if (currentTarget == null) {
      currentTarget = new Target(this, event);
    } else {
      if (event.getName().equals(currentTarget.getTargetName())) {
        currentTarget.update(event);
      } else if (event.getDistance() < currentTarget.getDistance()) {
        oldTargets.put(currentTarget.getTargetName(), currentTarget);
        if(oldTargets.containsKey(event.getName())) {
          currentTarget = oldTargets.get(event.getName());
          currentTarget.update(event);
        }
        else {
          currentTarget = new Target(this, event);
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
  }
}

package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.Random;

import lexx.aim.VirtualGun;
import lexx.target.EnemyFiredCondition;
import lexx.target.EnemyHitWallCondition;
import lexx.target.EnemyState;
import lexx.target.Target;
import lexx.target.TargetManager;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.CustomEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class DasBot extends AdvancedRobot {
  public static final int ROBOT_SIZE = 36;
  public static final double MAX_SPEED = 8;

  private static TargetManager targetManager;

  private int direction = 1;
  private int radarDirection = 1;
  private Random random = new Random();
  private double wallStick = 120;
  private Rectangle2D.Double battleField;

  public DasBot() {
    if (targetManager == null) {
      targetManager = new TargetManager(this);
    }
    targetManager.startRound();
  }

  @Override
  public void run() {
    if (battleField == null) {
      battleField = new Rectangle2D.Double(0, 0, getBattleFieldWidth(), getBattleFieldHeight());
      setColors(new Color(0, 128, 0), Color.green, Color.gray, Color.magenta, Color.magenta);
    }

    while (true) {
      targetManager.update();
      if (targetManager.getCurrentTarget() == null) {
        setAdjustRadarForGunTurn(false);
        setAdjustRadarForRobotTurn(false);
        setTurnRadarLeftRadians(Math.PI / 4);
      }

      if (targetManager.getCurrentTarget() != null) {
        VirtualGun bestGun = targetManager.getCurrentGunManager().getBestGun();
//        out.println("Using gun: " + bestGun);
        double maxFirePower = bestGun.getPower();
        double projectedBearingRadians = bestGun.getTargetHeadingRadians();

        // moving
        double headingRadians = getHeadingRadians();
        EnemyState currentTargetState = targetManager.getCurrentTarget().getCurrentState();
        double turnRadians = getTurnRadians(currentTargetState.getBearingRadians(), headingRadians);
        doMove(turnRadians, headingRadians);

        // shooting
        double turnGunRadians = Utils.normalRelativeAngle((projectedBearingRadians - getGunHeadingRadians()));
        setTurnGunRightRadians(turnGunRadians);

        // scanning
        double turnRadarRadians = Utils.normalRelativeAngle(currentTargetState.getBearingRadians()
            - getRadarHeadingRadians());

        if (Math.abs(turnGunRadians) < Math.PI / 100) {
          if (maxFirePower < 0.1) {
            // TODO: get closer to the target
          } else {
            if (Utils.isNear(getGunHeat(), 0)) {
              setFireBullet(maxFirePower);
              targetManager.getCurrentGunManager().trackFire();
            }
          }
        }

        setAdjustRadarForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        if (getRadarTurnRemainingRadians() == 0) {
          setTurnRadarRightRadians(turnRadarRadians + ((Math.PI / 12) * radarDirection));
          radarDirection *= -1;
        }

      }

      execute();
    }
  }

  private void doMove(double turnRadians, double headingRadians) {
    Angle newHeading = Angle.normalizedAbsoluteAngle(headingRadians + turnRadians);
    Double myPos = getPosition();
    Point2D.Double p1 = newHeading.projectPoint(myPos, (getVelocity() + wallStick) * direction); 

    if (!battleField.contains(p1)) {
      reverseDirection();
      double distanceToMove = 120 * (1 + random.nextDouble()) * direction;
      setAhead(distanceToMove);
    } else {
      setTurnRightRadians(turnRadians);

      if (getDistanceRemaining() == 0) {
        reverseDirection();
        double distanceToMove = 120 * (1 + random.nextDouble()) * direction;
        setAhead(distanceToMove);
      }
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
    targetManager.onScannedRobot(event);
  }

  @Override
  public void onRobotDeath(RobotDeathEvent event) {
    targetManager.onRobotDeath(event);
  }
  
  @Override
  public void onBulletHit(BulletHitEvent event) {
    Target currentTarget = targetManager.getTargetForName(event.getName());
    if(currentTarget != null) {
      currentTarget.logHit(event.getBullet().getPower(), event.getTime());
    }
  }
  
  @Override
  public void onCustomEvent(CustomEvent event) {
    if (event.getCondition() instanceof EnemyHitWallCondition) {
      EnemyHitWallCondition cond = (EnemyHitWallCondition) event.getCondition();
      Target currentTarget = targetManager.getCurrentTarget();
      if(currentTarget != null) {
        currentTarget.logWallCollision(cond.getEnergyLoss(), event.getTime());
      }
    }
    else if(event.getCondition() instanceof EnemyFiredCondition) {
      EnemyFiredCondition cond = (EnemyFiredCondition) event.getCondition();
      targetManager.trackEnemyBullet(cond.getBulletPower(), event.getTime());
    } 
  }

  @Override
  public void onPaint(Graphics2D g) {
    super.onPaint(g);
    targetManager.onPaint(g);
  }

  public Point2D.Double getPosition() {
    return new Point2D.Double(getX(), getY());
  }

  public Rectangle2D.Double getBattleField() {
    return battleField;
  }
}

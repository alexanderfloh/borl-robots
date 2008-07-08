package lexx;

import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.Bullet;

public class TrackedBullet {
  private Point2D.Double origin;
  private double originalBearingRadians;
  
  private Point2D.Double currentPos;
  private long lastUpdate;
  
  private double velocity;
  
  private AdvancedRobot robot;
  private Target target;
  private Bullet bullet;
  
  public TrackedBullet(AdvancedRobot robot, Target target, Bullet bullet) {
    this.robot = robot;
    this.target = target;
    
    this.origin = new Point2D.Double(robot.getX(), robot.getY());
    this.currentPos = origin;
    
    this.bullet = bullet;
    this.velocity = bullet.getVelocity();
  }
  
  public void update() {
    long tickDiff = robot.getTime() - lastUpdate;
    currentPos = Utils.translate(currentPos, originalBearingRadians, velocity * tickDiff);
    
  }
  
  public boolean isActive() {
    return bullet.isActive();
  }
  
  public String getVictim() {
    return bullet.getVictim();
  }
  
  public boolean isTargetHit() {
    return target.getRectangle().contains(currentPos);
  }
}

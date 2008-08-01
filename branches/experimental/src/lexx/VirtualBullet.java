package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import lexx.target.Target;

public class VirtualBullet {
  private Point2D.Double origin;
  private double headingRadians;
  
  private Point2D.Double currentPos;
  private long timeStamp;
  
  private double power;
  private double velocity;
  private Color color;
  
  public VirtualBullet(Point2D.Double origin, double headingRadians, double power, long currentTime) {
    this.origin = origin;
    this.currentPos = origin;
    
    this.headingRadians = headingRadians;
    
    this.power = power;
    this.velocity = Utils.powerToVelocity(power);
    
    this.timeStamp = currentTime;
    
    this.color = Color.WHITE;
  }
  
  public void update(long currentTime) {
    long tickDiff = currentTime - timeStamp;
    currentPos = Utils.translate(origin, headingRadians, velocity * tickDiff);
  }
  
  public boolean isTargetHit(Target target) {
    return target.getCurrentState().getRectangle().contains(currentPos);
  }
  
  public boolean isWithinBattleField(Rectangle2D.Double battleField) {
    return battleField.contains(currentPos);
  }
  
  public Point2D.Double getCurrentPosition() {
    return currentPos;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void onPaint(Graphics2D g) {
//    int size = Math.max(2, (int)(2 * power));
//    Ellipse2D.Double bullet = new Ellipse2D.Double(currentPos.x - size / 2, currentPos.y - size / 2, size, size);
//    g.setColor(color);
//    g.fill(bullet);
  }
}

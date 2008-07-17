package lexx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class Target {

  private static final int MAX_HISTORY_SIZE = 20;
  private final AdvancedRobot robot;
  private final String targetName;
  
  private double distance;
  private double energy;
  private double myHeadingRadians;

  private Rectangle2D.Double rectangle;
  private Point2D.Double myPos;

  private EnemyState currentEnemyState;
  private final List<EnemyState> history;

  public Target(AdvancedRobot robot, ScannedRobotEvent event) {
    this.robot = robot;
    this.targetName = event.getName();
    this.history = new LinkedList<EnemyState>();

    this.rectangle = new Rectangle2D.Double();
    rectangle.height = DasBot.ROBOT_SIZE;
    rectangle.width = DasBot.ROBOT_SIZE;

    update(event);
  }

  public void update(ScannedRobotEvent event) {
    if (currentEnemyState != null)
      history.add(0, currentEnemyState);

    if (history.size() > MAX_HISTORY_SIZE)
      history.remove(history.size() - 1);

    myHeadingRadians = robot.getHeadingRadians();

    this.distance = event.getDistance();
    double absoluteBearingRadians = normalizeEnemyBearingRadians(event.getBearingRadians(), myHeadingRadians);

    myPos = new Point2D.Double(robot.getX(), robot.getY());
    Point2D.Double enemyPos = Utils.translate(myPos, absoluteBearingRadians, distance);
    currentEnemyState = new EnemyState(enemyPos, event.getHeadingRadians(), event.getVelocity(),
        absoluteBearingRadians, event.getTime());

    rectangle.x = enemyPos.x - DasBot.ROBOT_SIZE / 2;
    rectangle.y = enemyPos.y - DasBot.ROBOT_SIZE / 2;

    double newEnergy = event.getEnergy();
    this.energy = newEnergy;
  }

  public String getTargetName() {
    return targetName;
  }

  public double getMaxFirePower() {
    // if the enemy has less than 0.1 energy left, it is most likely disabled
    // and cant move
    // shoot with the least amount of energy possible.
    if (energy < 0.1)
      return 0.1;

    if (robot.getEnergy() < 10)
      return 0;

    double ticks = 50; // TODO hardcoded
    double requiredVelocity = distance / ticks;
    double power = Math.min(3, (20 - requiredVelocity) / 3);

    if (robot.getEnergy() < 40) {
      power = Math.min(0.1, power);
    }

    return power;
  }
  
  public void logHit() {
    
  }

  public EnemyState getHistoricState(int ticks) {
    return history.get(ticks);
  }

  public Rectangle2D.Double getRectangle() {
    return rectangle;
  }

  public void onPaint(Graphics2D g) {
    g.setColor(Color.RED);
    g.draw(rectangle);
    
    int transparency = 10;
    int age = 0;
    g.setPaint(new Color(age, 255 - age, 0, transparency));
    for (EnemyState state : history) {
      Ellipse2D.Double circle = new Ellipse2D.Double(
          state.getEnemyPosition().x - DasBot.ROBOT_SIZE / 2, 
          state.getEnemyPosition().y - DasBot.ROBOT_SIZE / 2, 
          DasBot.ROBOT_SIZE, 
          DasBot.ROBOT_SIZE);
      g.fill(circle);
      age += 255 / MAX_HISTORY_SIZE;
      transparency = 255 / age;
      g.setPaint(new Color(age, 255 - age, 0, transparency));
    }
  }

  public double getHeadingRadians() {
    return currentEnemyState.getHeadingRadians();
  }

  public double getBearingRadians() {
    return currentEnemyState.getBearingRadians();
  }

  public long getTimeStamp() {
    return currentEnemyState.getTimeStamp();
  }

  public double getDistance() {
    return distance;
  }

  public double getVelocity() {
    return currentEnemyState.getVelocity();
  }

  public Point2D.Double getEnemyPos() {
    return currentEnemyState.getEnemyPosition();
  }

  public double getEnergy() {
    return energy;
  }

  private double normalizeEnemyBearingRadians(double originalBearingRadians, double myHeadingRadians) {
    return robocode.util.Utils.normalAbsoluteAngle(myHeadingRadians + originalBearingRadians);
  }
}

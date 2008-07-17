package lexx.aim;

import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import lexx.DasBot;
import lexx.target.Target;

public class GunManager {
  private List<VirtualGun> guns;
  
  private Comparator<VirtualGun> gunComparator = new SimpleGunComparator();
  
  public GunManager(DasBot robot, Target target) {
    guns = new LinkedList<VirtualGun>();
    guns.add(new LinearGun(robot, target));
//    guns.add(new DirectGun(robot, target, -1.0));
    guns.add(new DirectGun(robot, target, -0.8));
//    guns.add(new DirectGun(robot, target, -0.6));
//    guns.add(new DirectGun(robot, target, -0.4));
    guns.add(new DirectGun(robot, target, -0.2));
    guns.add(new DirectGun(robot, target, 0));
    guns.add(new DirectGun(robot, target, 0.2));
//    guns.add(new DirectGun(robot, target, 0.4));
    guns.add(new DirectGun(robot, target, 0.6));
//    guns.add(new DirectGun(robot, target, 0.8));
  }
  
  public void update() {
    for (VirtualGun gun : guns) {
      gun.update();
    }
  }
  
  public void updateTarget(Target target) {
    for (VirtualGun gun : guns) {
      gun.updateTarget(target);
    }
  }
  
  public void trackFire() {
    for (VirtualGun gun : guns) {
      gun.doFire();
    }
  }

  public VirtualGun getBestGun() {
    Collections.sort(guns, gunComparator);
    return guns.get(0);
  }
  
  
  private static class SimpleGunComparator implements Comparator<VirtualGun> {

    public int compare(VirtualGun o1, VirtualGun o2) {
      return o2.getBulletsHit() - o1.getBulletsHit();
    }
    
  }


  public void onPaint(Graphics2D g) {
    for (VirtualGun gun : guns) {
      gun.onPaint(g);
    }
  }
}

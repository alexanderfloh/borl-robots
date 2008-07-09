package us;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stefan Untereichner
 */
public class AimStorage {

	private final Map<String, Target> _aims = new HashMap<String, Target>();
	private final Map<String, IAimingStrategy> _strategies = new HashMap<String, IAimingStrategy>();
	
	public void addTarget(Target aim) {
		_aims.put(aim.getName(), aim);
		_strategies.put(aim.getName(), new AimCenter());
	}
	
	public Target getWeakest() {
		Target weakest = null;
		for (Target current : _aims.values()) {
			if (weakest == null) {
				weakest = current;
			}
			else {
				if (current.getEnergy() < weakest.getEnergy()) {
					weakest = current;
				}
			}
		}
		if (weakest != null)
			_aims.remove(weakest.getName());
		return weakest;
	}
	
	public IAimingStrategy getAimingStrategy(Target aim) {
		return _strategies.get(aim.getName());
	}

	public int getCount() {
		return _aims.size();
	}
}

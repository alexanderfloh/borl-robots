package g;

import robocode.CustomEvent;

/**
 * Defines a movement strategy.
 * 
 * @author michi
 * @since 18.07.2008
 */
public interface Movement {
	public void move(Target target);

	/**
	 * Callback for custom events.
	 * 
	 * @param event
	 */
	void onCustomEvent(CustomEvent event);
}

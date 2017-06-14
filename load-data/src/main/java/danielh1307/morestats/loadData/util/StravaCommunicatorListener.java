package danielh1307.morestats.loadData.util;

import java.util.Set;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Segment;

/**
 * 
 * This is a simple listener which can be used together with
 * {@link StravaCommunicator}. Whenever a specific event happens there, the
 * callback methods of this listener are called.
 *
 */
public interface StravaCommunicatorListener {

	/**
	 * This method is called whenever some {@link Activity} were successfully loaded.
	 * 
	 * @param loadedActivity the loaded set of {@link Activity}.
	 */
	public void activitiesLoaded(Set<Activity> loadedActivities);
	
	/**
	 * This method is called whenever segments of an {@link Activity} where successfully loaded.
	 * 
	 * @param activity the {@link Activity}.
	 * @param segments the list of loaded segments.
	 */
	public void segmentsLoaded(Activity activity, Set<Segment> segments);

}

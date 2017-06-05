package danielh1307.morestats.loadData;

import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Athlete;
import danielh1307.morestats.loadData.util.StravaCommunicator;

/**
 * Main application.
 *
 */
public class App {

	private static boolean EASY_ACCESS = false;
	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	private static StravaCommunicator comm;

	/**
	 * 
	 * @param args Expected: [clientSecret, accessToken]
	 */
	public static void main(String[] args) {
		comm = new StravaCommunicator();

		if (EASY_ACCESS) {
			comm.authorizeWithAccessToken(args[1]);
		} else {
			authorize(args[0]);
		}

		Athlete athlete = comm.getCurrentAthlete();
		LOGGER.info(String.valueOf(athlete));

		Set<Activity> activities = comm.getActivitiesForCurrentAthlete(false);
		LOGGER.info("There is a total of " + activities.size() + " activities");
		for (Activity activity : activities) {
			LOGGER.info(String.valueOf(activity));
		}

	}

	/**
	 * 
	 * @param clientSecret client secret for OAuth2
	 */
	private static void authorize(String clientSecret) {
		Scanner scanner = new Scanner(System.in);
		LOGGER.info("Copy this to your browser and type in the resulting code");
		LOGGER.info(
				"http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=http://localhost/morestats/auth&response_type=code&scope=view_private");
		String code = scanner.next();
		scanner.close();

		comm.authorize(clientSecret, code);
	}

}

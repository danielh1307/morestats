package danielh1307.morestats.loadData;

import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Athlete;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import danielh1307.morestats.repository.RepositoryController;

/**
 * Main application.
 *
 */
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class App {

	private static boolean EASY_ACCESS = true;
	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	private static StravaCommunicator comm;
	

	/**
	 * 
	 * @param args Expected: [clientSecret, accessToken]
	 */
	public static void main(String[] args) {
//		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
//		App main = new App();
//		main.start(args, ctx);
		SpringApplication.run(App.class, args);
	}
	
	private void start(String args[], ApplicationContext ctx) {
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
		RepositoryController repoController = ctx.getBean(RepositoryController.class);
		for (Activity activity : activities) {
			repoController.save(activity);
		}
		
		LOGGER.info("Getting an activity by name ...");
		Activity activityByName = repoController.findByName("Mal wieder eine kleine Ausfahrt").iterator().next();
		LOGGER.info(String.valueOf(activityByName));
	}

	/**
	 * 
	 * @param clientSecret client secret for OAuth2
	 */
	private void authorize(String clientSecret) {
		Scanner scanner = new Scanner(System.in);
		LOGGER.info("Copy this to your browser and type in the resulting code");
		LOGGER.info(
				"http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=http://localhost/morestats/auth&response_type=code&scope=view_private");
		String code = scanner.next();
		scanner.close();

		comm.authorize(clientSecret, code);
	}

}

package danielh1307.morestats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import danielh1307.morestats.loadData.util.StravaCommunicator;

/**
 * This is the start application class of the load-data component.
 *
 */
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class App {

	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	/**
	 * 
	 * @param args
	 *            Expected: clientSecret
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			String errMsg = "Pass exactly one argument: the client secret. Currently number of arguments: ["
					+ args.length + "]";
			LOGGER.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		String clientSecret = args[0];
		LOGGER.info("Client secret is: " + clientSecret);

		// we set the client secret as static attribute to StravaCommunicator,
		// so it is accessible from the controller without making the class
		// stateful
		// TODO: Do we find a better solution here?
		StravaCommunicator.setClientSecret(clientSecret);
		
		// start the application
		SpringApplication.run(App.class, args);
	}
}

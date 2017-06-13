package danielh1307.morestats.loadData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import danielh1307.morestats.loadData.util.StravaCommunicator;

/**
 * Main application.
 *
 */
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class App {

	private static Logger LOGGER = LoggerFactory.getLogger(App.class);	

	/**
	 * 
	 * @param args Expected: [clientSecret, accessToken]
	 */
	public static void main(String[] args) {
		String clientSecret = args[0];
		LOGGER.info("Client secret is: " + clientSecret);
		StravaCommunicator.setClientSecret(clientSecret);
		SpringApplication.run(App.class, args);
	}
}

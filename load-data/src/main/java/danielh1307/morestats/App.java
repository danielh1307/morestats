package danielh1307.morestats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This is the start application class of the load-data SCS.
 * 
 * The purpose of this SCS is to load the data from Strava, put it into a
 * database ("read-model") and publish according domain events.
 *
 */
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class App {

	/**
	 * 
	 * @param args
	 *            no arguments expected
	 */
	public static void main(String[] args) {
		// start the application
		SpringApplication.run(App.class, args);
	}
}

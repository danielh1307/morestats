package danielh1307.morestats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This is the start application class of the load-data component.
 *
 */
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class App {

	/**
	 * 
	 * @param args no arguments expected
	 */
	public static void main(String[] args) {		
		// start the application
		SpringApplication.run(App.class, args);
	}
}

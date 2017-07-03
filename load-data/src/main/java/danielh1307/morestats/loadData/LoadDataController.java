package danielh1307.morestats.loadData;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Athlete;
import danielh1307.morestats.entity.Segment;
import danielh1307.morestats.loadData.util.JwtTokenFactory;
import danielh1307.morestats.loadData.util.ResponseString;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import danielh1307.morestats.loadData.util.StravaCommunicatorListener;
import danielh1307.morestats.loadData.util.TokenContainer;
import danielh1307.morestats.repository.ActivityRepository;

@Controller
@RequestMapping("/")
public class LoadDataController implements StravaCommunicatorListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataController.class);
	private static final String LOAD_DATA_URL = "http://localhost:8080/morestats/loaddata";

	@Value("${auth.url}")
	private String authScs;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private ActivityRepository activityRepository;
	
	@Autowired
	private JwtTokenFactory tokenFactory;

	private final StravaCommunicator stravaComm;

	@Autowired
	public LoadDataController(StravaCommunicator stravaComm) {
		this.stravaComm = stravaComm;
		this.stravaComm.setListener(this);
	}

	@RequestMapping("/morestats")
	@ResponseBody
	public String home() {
		// this is just a very simple welcome page, which has a link to the auth SCS for the user to "log in"
		// the origin-url is the URL to which the browser is redirected after successful authentication (see loadData())
		return "Welcome to morestats: <a href=\"" + authScs + "/morestats/authorize?origin-url=" + LOAD_DATA_URL
				+ "\">Anmelden</a>";
	}

	/**
	 * After successful authentication, browser is redirected to /morestats/loaddata, the JWT is part of the URL.
	 * 
	 * @param jwt the generated JSON Web Token.
	 * @return ModelAndView for loadata page.
	 */
	@RequestMapping("/morestats/loaddata")
	public ModelAndView loadData(@RequestParam String jwt) {
		return new ModelAndView("loadData", "jwtToken", jwt);
	}

	@MessageMapping("/getdata")
	@SendTo("/topic/data")
	public ResponseString getData(TokenContainer tokenContainer) {
		// we have to get the Strava access token from the JWT
		String rawJwtToken = tokenContainer.getJwt();
		LOGGER.info("JWT token is " + rawJwtToken);

		String stravaAccessToken = tokenFactory.getStravaAccessToken(rawJwtToken);
		LOGGER.info("Strava access token is: " + stravaAccessToken);
		LOGGER.info("Name of the athlete is: " + tokenFactory.getAthleteName(rawJwtToken));

		Athlete athlete = stravaComm.getCurrentAthlete(stravaAccessToken);
		Set<Activity> activitiesForCurrentAthlete = stravaComm.getActivitiesForCurrentAthlete(stravaAccessToken, false);
		// TODO: fire domain event
		activityRepository.save(activitiesForCurrentAthlete);
		return new ResponseString(
				"Loaded data for athlete " + athlete + " and " + activitiesForCurrentAthlete.size() + " activities");
	}

	@Override
	public void activitiesLoaded(Set<Activity> activity) {
		String msg = "Loaded [" + activity.size() + "] new activities";
		messagingTemplate.convertAndSend("/topic/data", new ResponseString(msg));
	}

	@Override
	public void segmentsLoaded(Activity activity, Set<Segment> segments) {
		String msg = "Loaded [" + segments.size() + "] segments for activity [" + activity + "]";
		messagingTemplate.convertAndSend("/topic/data", new ResponseString(msg));

	}

}

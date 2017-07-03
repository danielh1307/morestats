package danielh1307.morestats.loadData;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Athlete;
import danielh1307.morestats.entity.Segment;
import danielh1307.morestats.loadData.util.ResponseString;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import danielh1307.morestats.loadData.util.StravaCommunicatorListener;
import danielh1307.morestats.loadData.util.TokenContainer;
import danielh1307.morestats.repository.ActivityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Controller
@RequestMapping("/")
public class LoadDataController implements StravaCommunicatorListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataController.class);
	private static final String ORIGIN = "http://localhost:8080/morestats/loaddata";
	private static final String AUTH_SCS = "http://localhost:8079";
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private ActivityRepository activityRepository;

	private final StravaCommunicator stravaComm;

	@Autowired
	public LoadDataController(StravaCommunicator stravaComm) {
		this.stravaComm = stravaComm;
		this.stravaComm.setListener(this);
	}

	@RequestMapping("/morestats")
	@ResponseBody
	public String home() {
		return "Welcome to morestats: <a href=\"" + AUTH_SCS + "/morestats/authorize?origin-url=" + ORIGIN + "\">Anmelden</a>";
	}

	@RequestMapping("/morestats/loaddata")
	public ModelAndView loadData() {
		return new ModelAndView("loadData");
	}

	@MessageMapping("/getdata")
	@SendTo("/topic/data")
	public ResponseString getData(TokenContainer tokenContainer) {
		// we have to get the Strava access token from the JWT
		String rawJwtToken = tokenContainer.getAccessToken();
		LOGGER.info("JWT token is " + rawJwtToken);
		
		byte[] signingKey = "myKey".getBytes(StandardCharsets.UTF_8);
		Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(rawJwtToken);
		
		String stravaAccessToken = jwsClaims.getBody().get("stravaAccessToken", String.class);
		LOGGER.info("Strava access token is: " + stravaAccessToken);
		
		
		Athlete athlete = stravaComm.getCurrentAthlete(stravaAccessToken);
		Set<Activity> activitiesForCurrentAthlete = stravaComm
				.getActivitiesForCurrentAthlete(stravaAccessToken, false);
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

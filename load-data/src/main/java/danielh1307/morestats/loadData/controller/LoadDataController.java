package danielh1307.morestats.loadData.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import danielh1307.morestats.loadData.danielh1307.morestats.loadData.dto.AthleteDto;
import danielh1307.morestats.loadData.danielh1307.morestats.loadData.dto.AuthScs;
import danielh1307.morestats.loadData.entity.Activity;
import danielh1307.morestats.loadData.entity.Athlete;
import danielh1307.morestats.loadData.entity.Segment;
import danielh1307.morestats.loadData.repository.ActivityRepository;
import danielh1307.morestats.loadData.util.JwtHandler;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import danielh1307.morestats.loadData.util.StravaCommunicatorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Set;

@Controller
@RequestMapping("/")
public class LoadDataController implements StravaCommunicatorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataController.class);
    private static final String LOAD_DATA_HOST = "http://localhost:8080";
    private static final String SUCC_AUTH_PATH = "/morestats/loadStravaData";
    private final StravaCommunicator stravaComm;
    private final ObjectMapper mapper;

    @Value("${auth.url}")
    private String authScs;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private JwtHandler tokenFactory;

    @Autowired
    public LoadDataController(StravaCommunicator stravaComm) {
        this.stravaComm = stravaComm;
        this.stravaComm.setListener(this);
        this.mapper = new ObjectMapper();
    }

    /**
     * Directs the user to the welcome page. To log in, the host of the auth SCS and the origin-url is needed, so
     * this is part of the returned model.
     *
     * @return ModelAndView for the welcome page.
     */
    @RequestMapping("/morestats")
    @ResponseBody
    public ModelAndView home() {
        AuthScs a = new AuthScs(authScs, LOAD_DATA_HOST + SUCC_AUTH_PATH);
        return new ModelAndView("welcome", "authScs", a);
    }

    /**
     * After successful authentication, browser is redirected to /morestats/loaddata, the JWT is part of the URL.
     * This method returns the JWT and the name of the athlete.
     *
     * @param jwt the generated JSON Web Token.
     * @return ModelAndView for loadata page.
     */
    @RequestMapping(SUCC_AUTH_PATH)
    public ModelAndView loadStravaData(@RequestParam String jwt) {
        // get access token
        String stravaAccessToken = tokenFactory.getStravaAccessToken(jwt);

        // load athlete from Strava
        Athlete stravaAthlete = stravaComm.getCurrentAthlete(stravaAccessToken);

        // create the DTO for the view
        AthleteDto dto = new AthleteDto(jwt, stravaAthlete.toString());

        return new ModelAndView("loadStravaData", "athlete", dto);
    }

    /**
     * This method loads data for activities and segments from Strava and persists them to the local database.
     *
     * @param jwt the JSON web token.
     * @return a result string.
     * @throws IOException if an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getData")
    @ResponseBody
    public String getDataFromStrava(@RequestBody String jwt) throws IOException {
        JsonNode jsonNode = mapper.readTree(jwt);
        String token = jsonNode.get("jwt").textValue();
        LOGGER.info("JWT token is " + token);

        String stravaAccessToken = tokenFactory.getStravaAccessToken(token);
        LOGGER.info("Strava access token is: " + stravaAccessToken);
        LOGGER.info("Name of the athlete is: " + tokenFactory.getAthleteName(token));

        Athlete athlete = stravaComm.getCurrentAthlete(stravaAccessToken);
        Set<Activity> activitiesForCurrentAthlete = stravaComm.getActivitiesForCurrentAthlete(stravaAccessToken, false);
        // TODO: fire domain event
        activityRepository.save(activitiesForCurrentAthlete);
        return "Es wurden " + activitiesForCurrentAthlete.size() + " Aktivitäten geladen";
    }

    @RequestMapping("/current")
    public ModelAndView getCurrentData(@RequestBody String jwt) {
        // we just return the current number of activities
        return new ModelAndView("current", "numOfActivies", activityRepository.count());
    }


    @Override
    public void activitiesLoaded(Set<Activity> activity) {
        String msg = "Loaded [" + activity.size() + "] new activities";
        LOGGER.info(msg);
    }

    @Override
    public void segmentsLoaded(Activity activity, Set<Segment> segments) {
        String msg = "Loaded [" + segments.size() + "] segments for activity [" + activity + "]";
        LOGGER.info(msg);
    }
}

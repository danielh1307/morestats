package danielh1307.morestats.loadData.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import danielh1307.morestats.loadData.util.SessionStore;
import danielh1307.morestats.loadData.business.StravaLoader;
import danielh1307.morestats.loadData.repository.ActivityRepository;
import danielh1307.morestats.loadData.util.JwtHandler;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.Future;

@Controller
@RequestMapping("/")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    private final StravaCommunicator stravaComm;
    private final ObjectMapper mapper;

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private JwtHandler tokenFactory;

    @Autowired
    private StravaLoader stravaLoader;

    @Autowired
    private SessionStore s;

    @Autowired
    public ApiController(StravaCommunicator stravaComm) {
        this.stravaComm = stravaComm;
        this.mapper = new ObjectMapper();
    }


    /**
     * Load data from Strava and put it into the local write store.
     * The data is loaded asynchronously.
     * The JWT is passed as HTTP header attribute.
     *
     * @param authHeader the Authorization header.
     * @return a message for the client.
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "/morestats/getStravaDataH")
    @ResponseBody
    public String getDataFromStravaH(@RequestHeader(value = "Authorization") String authHeader) throws Exception {
        String token = authHeader.substring("Bearer ".length());
        LOGGER.info("Bearer token is " + token);

        // first we check if there is already an async load running
        Future<String> stringFuture = s.getAsyncLoad();
        if (stringFuture != null) {
            // check whether it is done
            if (stringFuture.isDone()) {
                s.clearAsyncLoad();
                return stringFuture.get();
            } else {
                return "Async load is currently running ...";
            }
        } else {
            // no future set, so we start a new one
            s.setAsyncLoad(stravaLoader.loadStravaDataAsync(token));
            return "Async load has been started ...";
        }
    }

    /**
     * Load data from Strava and put it into the local write store.
     * The data is loaded synchronously.
     * The JWT is passed as JSON data.
     *
     * @param jwt the JSON web token.
     * @return a message for the client.
     * @throws IOException if an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/morestats/getStravaDataJ")
    @ResponseBody
    public String getDataFromStrava(@RequestBody String jwt) throws IOException {
        JsonNode jsonNode = mapper.readTree(jwt);
        String jwtToken = jsonNode.get("jwt").textValue();
        LOGGER.info("JWT token (as JSON in HTTP body) is " + jwtToken);

        return stravaLoader.loadStravaDataSync(jwtToken);
    }
}

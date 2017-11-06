package danielh1307.morestats.loadData.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // if you just call cacheControl() here (without disable()), you always get this header in the HTTP response:
        // Cache-Control: no-cache, no-store, max-age=0, must-revalidate
        // if you do not want this line in the header (and implement own caching mechanism), you have to call disable()
        http.authorizeRequests().anyRequest().permitAll().and().headers().cacheControl().disable();

        // we have to disable this because otherwise we get the following error when calling a REST service:
        // "message": "Expected CSRF token not found. Has your session expired?",
        // TODO: this only happens if we send the JWT token as JSON - if we send it via HTTP header it works --> find out why
        http.csrf().disable();
    }
}

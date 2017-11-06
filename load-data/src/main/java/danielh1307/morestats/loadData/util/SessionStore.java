package danielh1307.morestats.loadData.util;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * Stores some values in the HTTP session.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionStore {

    private Future<String> asyncLoad;

    public void setAsyncLoad(Future<String> asyncLoad) {
        this.asyncLoad = asyncLoad;
    }

    public Future<String> getAsyncLoad() {
        return asyncLoad;
    }

    public void clearAsyncLoad() {
        asyncLoad = null;
    }
}

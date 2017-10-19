package danielh1307.morestats.loadData.danielh1307.morestats.loadData.dto;

public class AuthScs {

    private String host;
    private String originUrl;

    public AuthScs(String host, String originUrl) {
        this.host = host;
        this.originUrl = originUrl;
    }

    public String getHost() {
        return host;
    }

    public String getOriginUrl() {
        return originUrl;
    }
}

package danielh1307.morestats.loadData.dto;

public class WriteStoreTotal {
    private long numOfActivities;
    private int numOfSegments;
    private String jwt;

    public WriteStoreTotal(long numOfActivities, int numOfSegments, String jwt) {
        this.numOfActivities = numOfActivities;
        this.numOfSegments = numOfSegments;
        this.jwt = jwt;
    }

    public long getNumOfActivities() {
        return numOfActivities;
    }

    public int getNumOfSegments() {
        return numOfSegments;
    }

    public String getJwt() {
        return jwt;
    }
}

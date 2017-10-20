package danielh1307.morestats.loadData.dto;

public class AthleteDto {

    private String jwtToken;
    private String name;

    public AthleteDto(String jwtToken, String name) {
        this.jwtToken = jwtToken;
        this.name = name;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getName() {
        return name;
    }
}

package jawas.tripmarker.pojos;

public class Location {

    private String userId;
    private double latitude;
    private double longtitude;
    private String title;
    private String description;


    public Location(){}

    public Location(String description, double latitude, double longtitude, String title, String userId) {
        this.description = description;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.title = title;
        this.userId = userId;
    }


    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }


    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

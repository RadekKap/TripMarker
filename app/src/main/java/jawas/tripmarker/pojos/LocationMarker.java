package jawas.tripmarker.pojos;

public class LocationMarker {

    private String userId;
    private double latitude;
    private double longitude;
    private String title;
    private String description;


    public LocationMarker(){}

    public LocationMarker(String description, double latitude, double longitude, String title, String userId) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.userId = userId;
    }


    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }


    public void setLongtitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

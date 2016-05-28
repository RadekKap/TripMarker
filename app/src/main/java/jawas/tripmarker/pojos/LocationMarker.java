package jawas.tripmarker.pojos;

import java.util.Date;

public class LocationMarker {

    private String userId;
    private double latitude;
    private double longitude;
    private String title;
    private String description;
    private Date date;

    public LocationMarker(){}

    public LocationMarker(String description, double latitude, double longitude, String title, String userId) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.userId = userId;
        this.date = new Date();
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

    public Date getDate() {
        return date;
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

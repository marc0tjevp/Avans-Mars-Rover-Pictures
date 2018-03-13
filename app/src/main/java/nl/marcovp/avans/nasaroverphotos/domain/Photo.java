package nl.marcovp.avans.nasaroverphotos.domain;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by marco on 3/13/18.
 */

public class Photo implements Serializable {

    private int id;
    private int sol;
    private String cameraName;
    private String imageURL;
    private Date earthDate;

    public Photo(int id, int sol, String cameraName, String imageURL, Date earthDate) {
        this.id = id;
        this.sol = sol;
        this.cameraName = cameraName;
        this.imageURL = imageURL;
        this.earthDate = earthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSol() {
        return sol;
    }

    public void setSol(int sol) {
        this.sol = sol;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Date getEarthDate() {
        return earthDate;
    }

    public void setEarthDate(Date earthDate) {
        this.earthDate = earthDate;
    }
}

package biz.eastservices.suara.Model;

/**
 * Created by reale on 2/8/2018.
 */

public class Candidate {
    public String profileImage,name,description,phone,category,rates,whatsapp,waze;
    public double lat,lng;

    public Candidate() {
    }

    public Candidate(String profileImage, String name, String description, String phone, String category, String rates) {
        this.profileImage = profileImage;
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.category = category;
        this.rates = rates;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getWaze() {
        return waze;
    }

    public void setWaze(String waze) {
        this.waze = waze;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }
}

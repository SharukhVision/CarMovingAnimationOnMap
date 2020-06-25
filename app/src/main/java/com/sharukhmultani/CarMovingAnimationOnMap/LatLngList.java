package com.sharukhmultani.CarMovingAnimationOnMap;

import java.util.ArrayList;
import java.util.List;

public class LatLngList {
    private double lat;
    private double lng;
    private String place;

    public LatLngList(double lat, double lng, String place) {
        this.lat = lat;
        this.lng = lng;
        this.place = place;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public static List<LatLngList> setLatLng() {
        List<LatLngList> latLngListAddresses = new ArrayList<>();
        latLngListAddresses.add(new LatLngList(21.1675516, 73.5615667,"Songadh")); //Songadh
        latLngListAddresses.add(new LatLngList(21.120001, 73.400002,"Vyara")); //Vyara
        latLngListAddresses.add(new LatLngList(21.124857, 73.112610,"Bardoli")); //Bardoli
        latLngListAddresses.add(new LatLngList(21.170240, 72.831062,"Surat"));  //Surat
        return latLngListAddresses;

    }

}

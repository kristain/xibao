package com.drjing.xibao.module.entity;

import java.io.Serializable;

/**
 * 商家坐标
 * Created by kristain on 15/12/21.
 */
public class Coordinate implements Serializable, NoObfuscateInterface {

    private double lng;
    private double lat;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}

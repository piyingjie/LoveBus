package com.lovebus.entity;

public class Location {
    private double latitude; //纬度
    private double longitude; //经度
    private String address; //地址
    private String country; //国家
    private String province; //省
    private String city; //城市
    private String district; //城区
    private String street; //街道
    private String PoiName; //poi名称
    public Location(double latitude, double longitude, String address, String country, String province, String city, String district, String street, String poiName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
        PoiName = poiName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPoiName() {
        return PoiName;
    }

    public void setPoiName(String poiName) {
        PoiName = poiName;
    }
}

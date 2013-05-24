package dk.miracleas.findtoilet;

import com.google.android.gms.maps.model.LatLng;

public class Toilet implements Cloneable, Comparable<Toilet> {

	private int id;
	private String street;
	private int postal;
	private String city;
	private double lat;
	private double lon;
	private String type;
	private String paid;
	private String maned;
	private String canister;
	private String baby;
	private String water;
	private String room;
	private String desc;
	private String area;
	private String picture;
	private LatLng latLng;
	private int distance;

	@Override
	protected Toilet clone() throws CloneNotSupportedException {
		return (Toilet) super.clone();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getPostal() {
		return postal;
	}

	public void setPostal(int postal) {
		this.postal = postal;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public String getManed() {
		return maned;
	}

	public void setManed(String maned) {
		this.maned = maned;
	}

	public String getCanister() {
		return canister;
	}

	public void setCanister(String canister) {
		this.canister = canister;
	}

	public String getBaby() {
		return baby;
	}

	public void setBaby(String baby) {
		this.baby = baby;
	}

	public String getWater() {
		return water;
	}

	public void setWater(String water) {
		this.water = water;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public LatLng getLatLng() {
		latLng = new LatLng(getLat(), getLon());
		
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	@Override
	public int compareTo(Toilet another) {

		if (getDistance() < another.getDistance()) {
			return -1;
		}
		if (getDistance() == another.getDistance()) {
			return 0;
		} else {
			return 1;
		}

	}
}

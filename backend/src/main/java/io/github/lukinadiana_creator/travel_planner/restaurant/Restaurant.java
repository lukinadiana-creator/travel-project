package io.github.lukinadiana_creator.travel_planner.restaurant;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "search_city")
    private String searchCity;

    @Column(name = "food_type")
    private String foodType; //или через enum

    @Column
    private String name;

    @Column
    private double rating;

    @Column
    private String location;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "average_bill")
    private BigDecimal averageBill;

    @Column(name = "image_url")
    private String imageUrl;

    public Restaurant() { }

    public Restaurant(String searchCity, String foodType, String name, double rating, String location, Double latitude, Double longitude, BigDecimal averageBill, String imageUrl) {
        this.searchCity = searchCity;
        this.foodType = foodType;
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageBill = averageBill;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchCity() {
        return searchCity;
    }

    public void setSearchCity(String searchCity) {
        this.searchCity = searchCity;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAverageBill() {
        return averageBill;
    }

    public void setAverageBill(BigDecimal averageBill) {
        this.averageBill = averageBill;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

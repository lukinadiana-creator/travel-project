package io.github.lukinadiana_creator.travel_planner.attraction;

import jakarta.persistence.*;

@Entity
@Table(name = "attractions")
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "search_city")
    private String searchCity;

    @Column(name = "attraction_type")
    private String attractionType; //или через enum

    @Column
    private String name;

    @Column
    private String location;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    public Attraction() { };

    public Attraction(String searchCity, String attractionType, String name, String location, Double latitude, Double longitude, String description, String imageUrl) {
        this.searchCity = searchCity;
        this.attractionType = attractionType;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
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

    public String getAttractionType() {
        return attractionType;
    }

    public void setAttractionType(String attractionType) {
        this.attractionType = attractionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

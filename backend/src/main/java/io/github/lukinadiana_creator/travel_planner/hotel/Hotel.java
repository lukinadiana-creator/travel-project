package io.github.lukinadiana_creator.travel_planner.hotel;

import io.github.lukinadiana_creator.travel_planner.tour.Tour;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotelbeds_code", unique = true, nullable = false)
    private Long hotelbedsCode;

    @Column(name = "destination_code", nullable = false)
    private String destinationCode;

    @Column(nullable = false)
    private String name;

    @Column
    private int rating;

    @Column
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @ElementCollection
    @CollectionTable(
            name = "hotel_images",
            joinColumns = @JoinColumn(name = "hotel_id")
    )
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> imageUrls;

    public Hotel() {}

    public Hotel(Long hotelbedsCode, String destinationCode, String name, int rating, String location, String description, double latitude, double longitude, List<String> imageUrls) {
        this.hotelbedsCode = hotelbedsCode;
        this.destinationCode = destinationCode;
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrls = imageUrls;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelbedsCode() {
        return hotelbedsCode;
    }

    public void setHotelbedsCode(Long hotelbedsCode) {
        this.hotelbedsCode = hotelbedsCode;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

}

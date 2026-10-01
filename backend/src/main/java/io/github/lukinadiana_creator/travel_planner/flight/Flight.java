package io.github.lukinadiana_creator.travel_planner.flight;

import io.github.lukinadiana_creator.travel_planner.tour.Tour;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "departure_date")
    private LocalDateTime departureDate;

    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;

    @Column(name = "city_from")
    private String cityFrom;

    @Column(name = "city_to")
    private String cityTo;

    @Column(name = "return_company_name")
    private String returnCompanyName;

    @Column(name = "return_departure_date")
    private LocalDateTime returnDepartureDate;

    @Column(name = "return_arrival_date")
    private LocalDateTime returnArrivalDate;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

    public Flight() {}

    public Flight(String companyName, LocalDateTime departureDate, LocalDateTime arrivalDate, String cityFrom, String cityTo, String returnCompanyName, LocalDateTime returnDepartureDate, LocalDateTime returnArrivalDate, BigDecimal totalAmount) {
        this.companyName = companyName;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.returnCompanyName = returnCompanyName;
        this.returnDepartureDate = returnDepartureDate;
        this.returnArrivalDate = returnArrivalDate;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getCityTo() {
        return cityTo;
    }

    public void setCityTo(String cityTo) {
        this.cityTo = cityTo;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public String getCityFrom() {
        return cityFrom;
    }

    public void setCityFrom(String cityFrom) {
        this.cityFrom = cityFrom;
    }

    public String getReturnCompanyName() {
        return returnCompanyName;
    }

    public void setReturnCompanyName(String returnCompanyName) {
        this.returnCompanyName = returnCompanyName;
    }

    public LocalDateTime getReturnDepartureDate() {
        return returnDepartureDate;
    }

    public void setReturnDepartureDate(LocalDateTime returnDepartureDate) {
        this.returnDepartureDate = returnDepartureDate;
    }

    public LocalDateTime getReturnArrivalDate() {
        return returnArrivalDate;
    }

    public void setReturnArrivalDate(LocalDateTime returnArrivalDate) {
        this.returnArrivalDate = returnArrivalDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }
}

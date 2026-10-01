package io.github.lukinadiana_creator.travel_planner.tour;

import io.github.lukinadiana_creator.travel_planner.flight.Flight;
import io.github.lukinadiana_creator.travel_planner.hotel.Hotel;
import io.github.lukinadiana_creator.travel_planner.hotel.Room;
import io.github.lukinadiana_creator.travel_planner.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    public Tour() {}

    public Tour(BigDecimal totalPrice, Hotel hotel, Room room, Flight flight) {
        this.totalPrice = totalPrice;
        this.hotel = hotel;
        this.room = room;
        this.flight = flight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}

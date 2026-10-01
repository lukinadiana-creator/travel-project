package io.github.lukinadiana_creator.travel_planner.tour;

import io.github.lukinadiana_creator.travel_planner.exception.EntityNotFoundException;
import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import io.github.lukinadiana_creator.travel_planner.flight.Flight;
import io.github.lukinadiana_creator.travel_planner.flight.FlightPairDto;
import io.github.lukinadiana_creator.travel_planner.flight.FlightService;
import io.github.lukinadiana_creator.travel_planner.hotel.*;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final HotelService hotelService;
    private final FlightService flightService;
    private final HotelRepository hotelRepository;
    private final TourMapper tourMapper;

    public TourService(TourRepository tourRepository, HotelService hotelService, FlightService flightService, HotelRepository hotelRepository, TourMapper tourMapper) {
        this.tourRepository = tourRepository;
        this.hotelService = hotelService;
        this.flightService = flightService;
        this.hotelRepository = hotelRepository;
        this.tourMapper = tourMapper;
    }

    public List<TourSearchItemDto> searchTours(String cityFrom, String city, LocalDate checkIn, int nightCount, int adults) {
        LocalDate checkOut = checkIn.plusDays(nightCount);
        List<HotelDto> hotelDtos = hotelService.searchHotels(city, checkIn, checkOut, adults);

        return hotelDtos.stream()
                .map(tourMapper::toSearchItem)
                .toList();
    }

    public HotelDto getInfoAboutTour(Long hotelbedsCode, LocalDate checkIn, int nightCount, int adults) {
        LocalDate checkOut = checkIn.plusDays(nightCount);
        return hotelService.getHotelWithAvailability(hotelbedsCode, checkIn, checkOut, adults);
    }

    public List<FlightPairDto> getFlightOptions(String cityFrom, String cityTo, LocalDate departureDate, int nightCount) {
        return flightService.getFlight(cityFrom, cityTo, departureDate, nightCount);
    }

    @Transactional
    public Tour addTour(SaveTourRequest request) {
        Hotel hotel = hotelRepository.findByHotelbedsCode(request.hotelbedsCode())
                .orElseThrow(() -> new EntityNotFoundException("Отель не найден"));

        Room room = hotelService.saveRoom(request.selectedRoom());
        Flight flight = flightService.saveFlight(request.selectedFlight());

        BigDecimal totalPrice = room.getPrice().add(flight.getTotalAmount().multiply(BigDecimal.valueOf(room.getNumberOfPerson())));

        Tour tour = new Tour();
        tour.setHotel(hotel);
        tour.setRoom(room);
        tour.setFlight(flight);
        tour.setTotalPrice(totalPrice);

        try {
            return tourRepository.save(tour);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить тур", e);
        }
    }
}

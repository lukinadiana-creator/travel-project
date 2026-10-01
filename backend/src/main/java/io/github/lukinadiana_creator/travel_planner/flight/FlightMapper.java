package io.github.lukinadiana_creator.travel_planner.flight;

import io.github.lukinadiana_creator.travel_planner.currency.CurrencyService;
import io.github.lukinadiana_creator.travel_planner.flight.duffel.Offer;
import io.github.lukinadiana_creator.travel_planner.flight.duffel.Segment;
import io.github.lukinadiana_creator.travel_planner.flight.duffel.Slice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FlightMapper {
    private final CurrencyService currencyService;

    public FlightMapper(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    //    public FlightDto toDto(Flight flight) {
//        return new FlightDto(
//                flight.getCompanyName(),
//                flight.getArrivalDate(),
//                flight.getCityTo(),
//                flight.getDepartureDate(),
//                flight.getCityFrom()
//        );
//    }

    public Flight toEntity(FlightPairDto pairDto) {
        Flight flight = new Flight();

        flight.setCompanyName(pairDto.outboundFlight().companyName());
        flight.setDepartureDate(pairDto.outboundFlight().departureDate());
        flight.setArrivalDate(pairDto.outboundFlight().arrivalDate());
        flight.setCityFrom(pairDto.outboundFlight().cityFrom());
        flight.setCityTo(pairDto.outboundFlight().cityTo());
        flight.setReturnCompanyName(pairDto.returnFlight().companyName());
        flight.setReturnDepartureDate(pairDto.returnFlight().departureDate());
        flight.setReturnArrivalDate(pairDto.returnFlight().arrivalDate());
        flight.setTotalAmount(pairDto.totalPrice());

        return flight;
    }

    public FlightPairDto toFlightPairDto(String cityFrom, String cityTo, Offer offer) {
        Slice outbound = offer.slices().get(0);
        Slice inbound = offer.slices().get(1);

        Segment outboundSegment = outbound.segments().get(0);
        Segment inboundSegment = inbound.segments().get(0);

        BigDecimal totalAmountRub = currencyService.convertToRub(offer.totalAmount(), offer.totalCurrency());

        return new FlightPairDto(
                new FlightDto(
                        outboundSegment.operatingCarrier().name(),
                        outboundSegment.arrivingAt(),
                        cityTo,
                        outboundSegment.departingAt(),
                        cityFrom),
                new FlightDto(
                        inboundSegment.operatingCarrier().name(),
                        inboundSegment.arrivingAt(),
                        cityFrom,
                        inboundSegment.departingAt(),
                        cityTo),
                totalAmountRub
        );
    }
}

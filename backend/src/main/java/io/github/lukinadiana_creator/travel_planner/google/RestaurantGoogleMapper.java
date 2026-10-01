package io.github.lukinadiana_creator.travel_planner.google;

import io.github.lukinadiana_creator.travel_planner.currency.CurrencyService;
import io.github.lukinadiana_creator.travel_planner.restaurant.RestaurantDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class RestaurantGoogleMapper {
    private final CurrencyService currencyService;
    private final PhotoService photoService;

    public RestaurantGoogleMapper(PhotoService photoService, CurrencyService currencyService) {
        this.photoService = photoService;
        this.currencyService = currencyService;
    }

    public RestaurantDto toDto(RestaurantGoogleDto googleDto) {
        String imageUrl = null;

        if (googleDto.photos() != null && !googleDto.photos().isEmpty()) {
            imageUrl = photoService.createPhotoUrl(googleDto.photos().getFirst().name());
        }

        BigDecimal averageBill = Optional.ofNullable(googleDto.priceRange())
                .filter(pr -> pr.startPrice() != null && pr.startPrice().units() != null
                        && pr.endPrice() != null && pr.endPrice().units() != null)
                .map(pr -> {
                    BigDecimal avg = new BigDecimal(pr.startPrice().units())
                            .add(new BigDecimal(pr.endPrice().units()))
                            .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
                    return currencyService.convertToRub(avg, pr.startPrice().currencyCode());
                })
                .orElse(null);

        return new RestaurantDto(
                null,
                googleDto.primaryTypeDisplayName().text(),
                googleDto.displayName().text(),
                googleDto.rating(),
                googleDto.formattedAddress(),
                googleDto.location().latitude(),
                googleDto.location().longitude(),
                averageBill,
                imageUrl
        );
    }
}

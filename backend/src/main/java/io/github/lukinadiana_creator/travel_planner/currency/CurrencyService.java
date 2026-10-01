package io.github.lukinadiana_creator.travel_planner.currency;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private final WebClient webClientCb;
    private final ObjectMapper objectMapper;
    private volatile Map<String, BigDecimal> ratesToRub = new HashMap<>();

    public CurrencyService(WebClient webClientCb, ObjectMapper objectMapper) {
        this.webClientCb = webClientCb;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        refreshRates();
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void refreshRates() {
        try {
            String rawJason = webClientCb.get()
                    .uri("/daily_json.js")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            CbResponseDto response = objectMapper.readValue(rawJason, CbResponseDto.class);

            if (response != null) {
                Map<String, BigDecimal> newRates = response.valute().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> BigDecimal.valueOf(e.getValue().value()/e.getValue().nominal())
                        ));
                this.ratesToRub = newRates;
            }
        } catch (Exception e) {
            log.error("Не удалось обновить курсы валют", e);
        }
    }

    public BigDecimal convertToRub(BigDecimal amount, String currencyCode) {
        if ("RUB".equals(currencyCode)) {
            return amount;
        }

        BigDecimal rate = ratesToRub.get(currencyCode);
        if (rate == null) {
            throw new IllegalStateException("Курс для валюты не найден: " + currencyCode);
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}

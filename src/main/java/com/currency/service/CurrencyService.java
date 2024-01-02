package com.currency.service;

import com.currency.entity.Currency;
import com.currency.exception.DataNotFoundException;
import com.currency.exception.ParseException;
import com.currency.model.Coindesk;
import com.currency.model.TransformCoindesk;
import com.currency.repository.CurrencyRepository;
import com.currency.util.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class CurrencyService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DateUtil dateUtil;

    @Autowired
    CurrencyRepository currencyRepository;



    @Value("${coindesk.api.url}")
    private String coindeskApiUrl;


    public Coindesk getCoindesk() {
        try {
            String jsonResponse = restTemplate.getForObject(coindeskApiUrl, String.class);
            return objectMapper.readValue(jsonResponse, Coindesk.class);
        } catch (JsonProcessingException e) {
            log.error("getCoindesk format json error: {}", e.getMessage());
            throw new ParseException(e.getMessage());
        }
    }

    public TransformCoindesk transformCoindesk() {
        TransformCoindesk transformCoindesk = new TransformCoindesk();
        try {
            String jsonResponse = restTemplate.getForObject(coindeskApiUrl, String.class);
            transformCoindesk = objectMapper.readValue(jsonResponse, TransformCoindesk.class);
        } catch (JsonProcessingException e) {
            log.error("transformCoindesk format json error: {}", e.getMessage());
            throw new ParseException(e.getMessage());
        }

        transformCoindesk.setTime(dateUtil.format(transformCoindesk.getTime()));

        transformCoindesk.getBpi().forEach((key, value) -> {
            Optional<Currency> currencyOptional = currencyRepository.findById(key);

            if (currencyOptional.isPresent()) {
                value.setCodeName(currencyOptional.get().getCcyName());
            } else {
                log.error("Currency with code {} not found", key);
                throw new DataNotFoundException("Currency with code " + key + " not found");
            }
        });

        return transformCoindesk;

    }

    public List<Currency> getAllCurrencies() {
        return StreamSupport.stream(currencyRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Currency getCurrencyByCode(String ccyCode) {
        return currencyRepository.findById(ccyCode).orElseThrow(() -> new DataNotFoundException("Currency with code " + ccyCode + " not found"));
    }

    public boolean existsById(String ccyCode) {
        return currencyRepository.existsById(ccyCode);
    }

    public Currency addCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public void deleteCurrencyById(String code) {
        currencyRepository.deleteById(code);
    }

}

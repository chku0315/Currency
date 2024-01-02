package com.currency.controller;

import com.currency.entity.Currency;
import com.currency.exception.DataNotFoundException;
import com.currency.model.Coindesk;
import com.currency.model.TransformCoindesk;
import com.currency.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Slf4j
public class CoinController {

    @Autowired
    CurrencyService currencyService;

    @GetMapping("/getCoindesk")
    public Coindesk getCoindesk() {
        return currencyService.getCoindesk();
    }


    @GetMapping("/transformCoindesk")
    public TransformCoindesk transformCoindesk() {
        return currencyService.transformCoindesk();
    }

    @GetMapping("/getAllCurrencies")
    public List<Currency> getAllCurrency() {
        return currencyService.getAllCurrencies();
    }


    @GetMapping("/getCurrency/{ccyCode}")
    public Currency getCurrency(@PathVariable String ccyCode) {
        return currencyService.getCurrencyByCode(ccyCode);
    }

    @PostMapping("/addCurrency")
    public void addCurrency(@Valid @RequestBody Currency currency) {
        if(currencyService.existsById(currency.getCcyCode())) {
            log.error("[addCurrency] Currency with code " + currency.getCcyCode() + " already exists");
            throw new DuplicateKeyException("Currency with code " + currency.getCcyCode() + " already exists");
        }
        currencyService.addCurrency(currency);
    }

    @PutMapping("/updateCurrency")
    public Currency updateCurrency(@Valid @RequestBody Currency currency) {
        if(!currencyService.existsById(currency.getCcyCode())) {
            log.error("[updateCurrency] Currency with code " + currency.getCcyCode() + " not exists");
            throw new DataNotFoundException("Currency with code " + currency.getCcyCode() + " not exists");
        }

        return currencyService.addCurrency(currency);
    }

    @DeleteMapping("/deleteCurrency/{ccyCode}")
    public void deleteCurrency(@PathVariable String ccyCode) {
        if(!currencyService.existsById(ccyCode)) {
            log.error("[deleteCurrency] Currency with code " + ccyCode + " not exists");
            throw new DataNotFoundException("Currency with code " + ccyCode + " not exists");
        }
        currencyService.deleteCurrencyById(ccyCode);
    }


}

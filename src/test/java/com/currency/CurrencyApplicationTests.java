package com.currency;

import com.currency.entity.Currency;
import com.currency.repository.CurrencyRepository;
import com.currency.service.CurrencyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Value("${coindesk.api.url}")
    private String coindeskApiUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllCurrency() throws Exception {
        mockMvc.perform(get("/v1/getAllCurrencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(200));
    }

    @Test
    public void testGetCurrency() throws Exception {
        mockMvc.perform(get("/v1/getCurrency/{ccyCode}", "USD"))
                .andExpect(ResultMatcher.matchAll(
                        status().isOk(),
                        jsonPath("code").value(200),
                        jsonPath("message").value("SUCCESS"),
                        jsonPath("payload.ccyCode").value("USD"),
                        jsonPath("payload.ccyName").value("美金")
                ));
    }

    @Test
    public void testNotFoundGetCurrency() throws Exception {
        mockMvc.perform(get("/v1/getCurrency/{ccyCode}", "CNY"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(404));
    }

    @Test
    public void testAddCurrency() throws Exception {
        Currency mockCurrencyAdd = new Currency("TWD", "新台幣");

        mockMvc.perform(post("/v1/addCurrency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCurrencyAdd)))
                .andExpect(ResultMatcher.matchAll(
                        status().isOk(),
                        jsonPath("code").value(200),
                        jsonPath("message").value("SUCCESS")
                ));

    }

    @Test
    public void testDuplicateAddCurrency() throws Exception {
        Currency mockCurrencyAdd = new Currency("USD", "美金");
        mockMvc.perform(post("/v1/addCurrency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCurrencyAdd)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("code").value(409));
    }

    @Test
    public void testUpdateCurrency() throws Exception {
        Currency mockCurrencyUpdate = new Currency("HKD", "港幣");
        mockMvc.perform(put("/v1/updateCurrency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCurrencyUpdate)))
                .andExpect(ResultMatcher.matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("code").value(200),
                        jsonPath("message").value("SUCCESS"),
                        jsonPath("payload.ccyCode").value("HKD"),
                        jsonPath("payload.ccyName").value("港幣")
                ));
    }

    @Test
    public void testNotFoundUpdateCurrency() throws Exception {
        Currency mockCurrencyNotFound = new Currency("CNY", "人民幣");

        mockMvc.perform(put("/v1/updateCurrency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCurrencyNotFound)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(404));
    }

    @Test
    public void testDeleteCurrency() throws Exception {
        mockMvc.perform(delete("/v1/deleteCurrency/{ccyCode}", "JPY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(200))
                .andExpect(jsonPath("message").value("SUCCESS"));
    }

    @Test
    public void testNotFoundDeleteCurrency() throws Exception {
        mockMvc.perform(delete("/v1/deleteCurrency/{ccyCode}", "CNY"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(404));
    }

    @Test
    void testGetCoindesk() throws Exception {

        mockMvc.perform(get("/v1/getCoindesk"))
                .andExpect(ResultMatcher.matchAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.payload.time.updated").exists(),
                        jsonPath("$.payload.disclaimer").exists(),
                        jsonPath("$.payload.chartName").exists(),
                        jsonPath("$.payload.bpi").exists()
                ));

    }

    @Test
    public void testTransformCoindesk() throws Exception {

        MvcResult result = mockMvc.perform(get("/v1/transformCoindesk"))
                .andExpect(ResultMatcher.matchAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.payload.time.updated").exists(),
                        jsonPath("$.payload.time.updatedISO").exists(),
                        jsonPath("$.payload.time.updateduk").exists(),
                        jsonPath("$.payload.bpi.USD.code").value("USD"),
                        jsonPath("$.payload.bpi.USD.codeName").value("美金"),
                        jsonPath("$.payload.bpi.USD.rate").exists(),
                        jsonPath("$.payload.bpi.USD.rate_float").exists(),
                        jsonPath("$.payload.bpi.GBP.code").value("GBP"),
                        jsonPath("$.payload.bpi.GBP.codeName").value("英鎊"),
                        jsonPath("$.payload.bpi.GBP.rate").exists(),
                        jsonPath("$.payload.bpi.GBP.rate_float").exists(),
                        jsonPath("$.payload.bpi.EUR.code").value("EUR"),
                        jsonPath("$.payload.bpi.EUR.codeName").value("歐元"),
                        jsonPath("$.payload.bpi.EUR.rate").exists(),
                        jsonPath("$.payload.bpi.EUR.rate_float").exists()
                )).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        DateTimeFormatter expectedFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        String updated = jsonNode.at("/payload/time/updated").asText();
        assertTrue(validateDateTimeFormat(updated, expectedFormat));

        String updatedISO = jsonNode.at("/payload/time/updatedISO").asText();
        assertTrue(validateDateTimeFormat(updatedISO, expectedFormat));

        String updatedUK = jsonNode.at("/payload/time/updateduk").asText();
        assertTrue(validateDateTimeFormat(updatedUK, expectedFormat));
    }

    private boolean validateDateTimeFormat(String dateTimeString, DateTimeFormatter formatter) {
        try {
            LocalDateTime.parse(dateTimeString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


}

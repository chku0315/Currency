package com.currency.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferCurrencyInfo {
    @JsonProperty("code")
    private String code;

    private String codeName;

    @JsonProperty("rate")
    private String rate;

    @JsonProperty("rate_float")
    private float rate_float;


}

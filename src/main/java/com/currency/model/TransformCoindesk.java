package com.currency.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TransformCoindesk {
    @JsonProperty("time")
    private UpdateTime time;
    @JsonProperty("bpi")
    private Map<String, TransferCurrencyInfo> bpi;
}

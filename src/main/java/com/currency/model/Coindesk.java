package com.currency.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Coindesk {

        private UpdateTime time;
        private String disclaimer;
        private String chartName;
        private Map<String, CurrencyInfo> bpi;

}

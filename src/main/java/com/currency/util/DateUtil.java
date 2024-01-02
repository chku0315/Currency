package com.currency.util;

import com.currency.model.UpdateTime;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DateUtil {
    DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public UpdateTime format(UpdateTime updateTime) {
        updateTime.setUpdated(this.updatedFormat(updateTime.getUpdated()));
        updateTime.setUpdatedISO(this.isoFormat(updateTime.getUpdatedISO()));
        updateTime.setUpdateduk(this.ukFormat(updateTime.getUpdateduk()));
        return updateTime;
    }

    private String updatedFormat(String updated) {
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z", Locale.ENGLISH);
        return ZonedDateTime.parse(updated, originalFormatter).format(targetFormatter);
    }

    private String isoFormat(String updatedISO) {
        DateTimeFormatter originalFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return ZonedDateTime.parse(updatedISO, originalFormatter).format(targetFormatter);
    }

    private String ukFormat(String updateduk) {
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm z", Locale.ENGLISH);
        return ZonedDateTime.parse(updateduk, originalFormatter).format(targetFormatter);
    }

}



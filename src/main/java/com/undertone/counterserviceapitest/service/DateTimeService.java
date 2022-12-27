package com.undertone.counterserviceapitest.service;

import com.undertone.counter.PeriodType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.undertone.counter.Counter.*;

@Service
public class DateTimeService {

    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    private DateTimeFormatter minuteFormatter;
    private DateTimeFormatter hourFormatter;
    private DateTimeFormatter dayFormatter;

    @PostConstruct
    public void init() {
        final String dayFormatPattern = PeriodType.getDescriptor().getOptions().getExtension(dayFormatStr);
        final String hourFormatPattern = PeriodType.getDescriptor().getOptions().getExtension(hourFormatStr);
        final String minuteFormatPattern = PeriodType.getDescriptor().getOptions().getExtension(minuteFormatStr);

        minuteFormatter = DateTimeFormatter.ofPattern(minuteFormatPattern);
        hourFormatter = DateTimeFormatter.ofPattern(hourFormatPattern);
        dayFormatter = DateTimeFormatter.ofPattern(dayFormatPattern);
    }

    public String getTimestamp(PeriodType periodType) {
        return switch (periodType) {
            case MINUTE -> ZonedDateTime.now(UTC_ZONE_ID).format(minuteFormatter);
            case HOUR -> ZonedDateTime.now(UTC_ZONE_ID).format(hourFormatter);
            case DAY -> ZonedDateTime.now(UTC_ZONE_ID).format(dayFormatter);
            case LIFETIME -> "";
            default -> throw new IllegalArgumentException("periodType " + periodType + " is not supported");
        };
    }

    public LocalDate getLocalDate(String dayTimestamp) {
        return LocalDate.parse(dayTimestamp, dayFormatter);
    }

    public LocalDateTime getLocalDateTimeHour(String hourTimestamp) {
        return LocalDateTime.parse(hourTimestamp, hourFormatter);
    }

    public LocalDateTime getLocalDateTimeMinute(String minuteTimestamp) {
        return LocalDateTime.parse(minuteTimestamp, minuteFormatter);
    }

}

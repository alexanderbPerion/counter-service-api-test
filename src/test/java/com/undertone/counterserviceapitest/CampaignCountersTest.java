package com.undertone.counterserviceapitest;

import com.undertone.counter.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@SpringBootTest
public class CampaignCountersTest extends CounterTest {

    @Test
    public void campaignCountersTest() throws IOException, InterruptedException {
        final String campaignCounterPath = SERVER_URL + "/counter/campaign";

        log.info("campaignCounterPath = {}", campaignCounterPath);

        Random random = new Random();
        int campaignId = random.nextInt(Integer.MAX_VALUE);

        log.info("campaignId = {}", campaignId);

        final String impressionHourTimestamp = incCounter(EventType.IMPRESSION, campaignCounterPath, PeriodType.HOUR, campaignId);
        final String impressionDayTimestamp = incCounter(EventType.IMPRESSION, campaignCounterPath, PeriodType.DAY, campaignId);
        final String clickHourTimestamp = incCounter(EventType.CLICK, campaignCounterPath, PeriodType.HOUR, campaignId);
        final String responseMinuteTimestamp = incCounter(EventType.RESPONSE, campaignCounterPath, PeriodType.MINUTE, campaignId);
        incCounter(EventType.IMPRESSION, campaignCounterPath, PeriodType.HOUR, campaignId);

        final CampaignCounterResponse campaignCounterResponse = getCampaignCounterResponse(campaignId);

        CampaignCounter expectedClickCampaignCounter = getExpectedCampaignCounter(EventType.CLICK, PeriodType.HOUR, clickHourTimestamp, 1);
        CampaignCounter expectedImprHourCampaignCounter = getExpectedCampaignCounter(EventType.IMPRESSION, PeriodType.HOUR, impressionHourTimestamp, 2);
        CampaignCounter expectedImprDayCampaignCounter = getExpectedCampaignCounter(EventType.IMPRESSION, PeriodType.DAY, impressionDayTimestamp, 1);
        CampaignCounter expectedResponseCampaignCounter = getExpectedCampaignCounter(EventType.RESPONSE, PeriodType.MINUTE, responseMinuteTimestamp, 1);

        Assertions.assertEquals(4, campaignCounterResponse.getCampaignCountersList().size());
        Assertions.assertEquals(expectedClickCampaignCounter, campaignCounterResponse.getCampaignCountersList().get(0));
        Assertions.assertEquals(expectedImprDayCampaignCounter, campaignCounterResponse.getCampaignCountersList().get(1));
        Assertions.assertEquals(expectedImprHourCampaignCounter, campaignCounterResponse.getCampaignCountersList().get(2));
        Assertions.assertEquals(expectedResponseCampaignCounter, campaignCounterResponse.getCampaignCountersList().get(3));
    }

    private static CampaignCounter getExpectedCampaignCounter(EventType eventType, PeriodType periodType, String timestamp, int expectedCounter) {
        return CampaignCounter.newBuilder()
            .setEventType(eventType)
            .setEventCounter(expectedCounter)
            .setPeriodType(periodType)
            .setTimestamp(timestamp)
            .build();
    }

    private String incCounter(EventType eventType, String campaignCounterPath, PeriodType periodType, int campaignId) throws IOException, InterruptedException {
        final String timestamp = dateTimeService.getTimestamp(periodType);

        CampaignCounterRequest campaignImpressionCounterRequest = CampaignCounterRequest.newBuilder()
            .setCampaignId(campaignId)
            .setEventType(eventType)
            .setPeriodType(periodType)
            .setTimestamp(timestamp)
            .build();

        HttpRequest impressionHttpRequest = HttpRequest.newBuilder()
            .uri(URI.create(campaignCounterPath))
            .POST(HttpRequest.BodyPublishers.ofByteArray(campaignImpressionCounterRequest.toByteArray()))
            .timeout(Duration.of(REQUEST_TIMEOUT_SEC, SECONDS))
            .build();

        HttpResponse<String> response = client.send(impressionHttpRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode());

        return timestamp;
    }

}

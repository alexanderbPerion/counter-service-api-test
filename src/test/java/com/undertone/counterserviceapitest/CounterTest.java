package com.undertone.counterserviceapitest;

import com.undertone.counter.CampaignCounterResponse;
import com.undertone.counterserviceapitest.service.DateTimeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public abstract class CounterTest {

    protected static final int REQUEST_TIMEOUT_SEC = 3;

    protected static final String SERVER_URL = (System.getenv("SERVER_URL") != null) ? System.getenv("SERVER_URL") : "http://127.0.0.1:8823";
//    protected static final String SERVER_URL = "http://ramplift-v2-i-counter-service-us-east-1-k8s-internal.ramp-ut.io";

    @Autowired
    protected DateTimeService dateTimeService;

    protected static final HttpClient client = HttpClient.newHttpClient();


    protected CampaignCounterResponse getCampaignCounterResponse(int campaignId) throws IOException, InterruptedException {


        final String getCampaignCounterPath = SERVER_URL + "/counter/campaign/find/" + campaignId;

        log.info("getCampaignCounterPath = {}", getCampaignCounterPath);

        HttpRequest getCampaignCountersRequest = HttpRequest.newBuilder()
            .uri(URI.create(getCampaignCounterPath))
            .GET()
            .timeout(Duration.of(REQUEST_TIMEOUT_SEC, SECONDS))
            .build();

        HttpResponse<byte[]> campaignCountersResponse = client.send(getCampaignCountersRequest, HttpResponse.BodyHandlers.ofByteArray());
        Assertions.assertEquals(HttpStatus.OK.value(), campaignCountersResponse.statusCode());

        return CampaignCounterResponse.parseFrom(campaignCountersResponse.body());
    }


}

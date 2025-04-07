package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Service
public class StatsClient extends BaseClient {
    private static final String API_PREFIX = "/stats";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getStatsUri(String start, String end,
                                           Boolean unique, ArrayList<String> uris) throws Exception {
        String path = pathEncoderUri(start, end, unique, uris);

        return get(path);
    }

    public ResponseEntity<Object> getStats(String start, String end,
                                              Boolean unique) throws Exception {
        String path = pathEncoder(start, end, unique);

        return get(path);
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public String pathEncoderUri(String start, String end, Boolean unique, ArrayList<String> uris) throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", "start");
        requestParams.put("end", "end");
        requestParams.put("unique", "unique");

        for (String u: uris) {
            requestParams.put("uris", "u");
        }

        String encodedURL = requestParams.keySet().stream()
                .map(key -> {
                    try {
                        return key + "=" + encodeValue(requestParams.get(key));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(joining("&", "http://www.baeldung.com?", ""));

        return encodedURL;
    }

    public String pathEncoder(String start, String end, Boolean unique) throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", "start");
        requestParams.put("end", "end");
        requestParams.put("unique", "unique");

        String encodedURL = requestParams.keySet().stream()
                .map(key -> {
                    try {
                        return key + "=" + encodeValue(requestParams.get(key));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(joining("&", "http://www.baeldung.com?", ""));

        return encodedURL;
    }
}


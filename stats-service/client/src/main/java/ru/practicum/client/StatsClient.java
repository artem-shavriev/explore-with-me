package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ViewStats;

import java.util.HashMap;
import java.util.List;
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

    public ResponseEntity<List<ViewStats>> getStatsUri(String start, String end,
                                                       Boolean unique, List<String> uris) {
        String path = pathEncoderUri(start, end, unique, uris);

        return getTyped(path, new ParameterizedTypeReference<List<ViewStats>>() {});
    }

    public String pathEncoderUri(String start, String end, Boolean unique, List<String> uris) {
        //String serverUrl = "http://localhost:9090/stats?";
        String serverUrl = "http://stats-server:9090/stats?";
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", start);
        requestParams.put("end", end);
        requestParams.put("unique", unique.toString());

        for (String u: uris) {
            requestParams.put("uris", u);
        }

        String encodedURL = requestParams.keySet().stream()
                .map(key -> {
                        return key + "=" + requestParams.get(key);
                })
                .collect(joining("&", serverUrl, ""));

        return encodedURL;
    }
}


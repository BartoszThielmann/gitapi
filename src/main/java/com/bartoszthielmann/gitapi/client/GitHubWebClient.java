package com.bartoszthielmann.gitapi.client;

import com.bartoszthielmann.gitapi.entity.Repository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GitHubWebClient {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://api.github.com";

    public GitHubWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Repository> getUserRepos(String username) {
        String url = BASE_URL + "/users/" + username + "/repos?per_page=100";
        ResponseEntity<List<Repository>> rateResponse =
                restTemplate.exchange(url,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Repository>>() {
                        });
        List<Repository> repositories = rateResponse.getBody();
        return repositories;
    }
}

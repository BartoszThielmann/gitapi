package com.bartoszthielmann.gitapi.client;

import com.bartoszthielmann.gitapi.entity.Branch;
import com.bartoszthielmann.gitapi.entity.Repository;
import com.bartoszthielmann.gitapi.exception.UserNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
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
        ResponseEntity<List<Repository>> response;
        try {
        response = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Repository>>() {});
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException();
            }
            throw e;
        }
        return response.getBody();
    }

    public List<Branch> getRepoBranches(String owner, String repo) {
        String url = BASE_URL + "/repos/" + owner + "/" + repo + "/branches?per_page=100";
        ResponseEntity<List<Branch>> response =
                restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Branch>>() {
                });
        List<Branch> branches = response.getBody();
        return branches;
    }
}

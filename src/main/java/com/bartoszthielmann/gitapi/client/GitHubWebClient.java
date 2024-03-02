package com.bartoszthielmann.gitapi.client;

import com.bartoszthielmann.gitapi.entity.Branch;
import com.bartoszthielmann.gitapi.entity.Repository;
import com.bartoszthielmann.gitapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitHubWebClient {

    private final RestTemplate restTemplate;
    @Value("${github.baseUrl}")
    private String BASE_URL;
    private static final Pattern NEXT_PAGE_URL_PATTERN = Pattern.compile("<([^>]+)>; rel=\"next\"");

    public GitHubWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Repository> getUserRepos(String username) {
        ResponseEntity<List<Repository>> currPageResponse;
        List<Repository> allRepositories = new ArrayList<>();
        String url = BASE_URL + "/users/" + username + "/repos?per_page=100";

        while (url != null) {
            try {
                currPageResponse = restTemplate.exchange(url,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Repository>>() {
                        });
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new UserNotFoundException();
                }
                throw e;
            }

            List<Repository> currPageRepositories = currPageResponse.getBody();
            if (currPageRepositories != null) {
                allRepositories.addAll(currPageRepositories);
            }

            List<String> linkHeader = currPageResponse.getHeaders().get(HttpHeaders.LINK);
            url = getNextPageUrl(linkHeader);
        }
        return allRepositories;
    }

    public List<Branch> getRepoBranches(String owner, String repo) {
        ResponseEntity<List<Branch>> currPageResponse;
        List<Branch> allBranches = new ArrayList<>();
        String url = BASE_URL + "/repos/" + owner + "/" + repo + "/branches?per_page=100";

        while (url != null) {
            currPageResponse =
                restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Branch>>() {
                });

            List<Branch> currPageRepositories = currPageResponse.getBody();
            if (currPageRepositories != null) {
                allBranches.addAll(currPageRepositories);
            }

            List<String> linkHeader = currPageResponse.getHeaders().get(HttpHeaders.LINK);
            url = getNextPageUrl(linkHeader);
        }
        return allBranches;
    }

    private String getNextPageUrl(List<String> linkHeader) {
        if (linkHeader == null) {
            return null;
        }
        return linkHeader.stream()
                .filter(link -> link.contains("rel=\"next\""))
                .map(link -> {
                    Matcher matcher = NEXT_PAGE_URL_PATTERN.matcher(link);
                    return matcher.find() ? matcher.group(1) : null;
                })
                .findFirst()
                .orElse(null);
    }
}

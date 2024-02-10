package com.bartoszthielmann.gitapi.client;

import com.bartoszthielmann.gitapi.entity.Branch;
import com.bartoszthielmann.gitapi.entity.Repository;
import com.bartoszthielmann.gitapi.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class GitHubWebClient {
    private final WebClient webClient;

    public GitHubWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }

    public Flux<Repository> getUserRepos(String username) {
        return getUserRepos(username, 1);
    }

    private Flux<Repository> getUserRepos(String username, int page) {
        return webClient.get()
                .uri("/users/{username}/repos?per_page=100&page={page}", username, page)
                .exchangeToFlux(response -> {
                    if (response.statusCode().isError()) {
                        if (response.statusCode() == HttpStatus.NOT_FOUND) {
                            return Flux.error(new UserNotFoundException());
                        } else {
                            return Flux.error(new RuntimeException(String.format(
                                    "Error received in response from" +
                                            " https://api.github.com/users/%s/repos?per_page=100&page=%d: ",
                                    username, page) + String.valueOf(response.statusCode())));
                        }
                    }
                    if (hasNextPage(response)) {
                        return response.toEntityList(Repository.class)
                                .flatMapMany(entity -> {
                                    Flux<Repository> currentPageRepos = Flux.fromIterable(entity.getBody());
                                    return Flux.concat(currentPageRepos, getUserRepos(username, page + 1));
                                });
                    } else {
                        return response.bodyToFlux(Repository.class);
                    }
                })
                .onErrorResume(e -> Flux.error(e));
    }

    public Flux<Branch> getRepoBranches(String owner, String repo) {
        return getRepoBranches(owner, repo, 1);
    }

    private Flux<Branch> getRepoBranches(String owner, String repo, int page) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/branches?per_page=100&page={page}", owner, repo, page)
                .exchangeToFlux(response -> {
                    if (response.statusCode().isError()) {
                        return Flux.error(new RuntimeException(String.format("Error received in response from " +
                                "https://api.github.com/repos/%s/%s/branches?per_page=100&page=%d: ", owner, repo, page)
                                + String.valueOf(response.statusCode())));
                    }
                    if (hasNextPage(response)) {
                        return response.toEntityList(Branch.class)
                                .flatMapMany(entity -> {
                                    Flux<Branch> currentPageBranches = Flux.fromIterable(entity.getBody());
                                    return Flux.concat(currentPageBranches, getRepoBranches(owner, repo, page + 1));
                                });
                    } else {
                        return response.bodyToFlux(Branch.class);
                    }
                }).onErrorResume(e -> Flux.error(e));
    }

    private boolean hasNextPage(ClientResponse response) {
        HttpHeaders headers = response.headers().asHttpHeaders();
        if (headers.containsKey(HttpHeaders.LINK)) {
            List<String> linkHeader = headers.get(HttpHeaders.LINK);
            for (String item : linkHeader) {
                if (item.contains("rel=\"next\"")) {
                    return true;
                }
            }
        }
        return false;
    }
}

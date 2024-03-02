package com.bartoszthielmann.gitapi.integration;


import com.bartoszthielmann.gitapi.GitapiApplication;
import com.bartoszthielmann.gitapi.util.FileUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;



@SpringBootTest(classes = GitapiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class GitApiIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static WireMockServer wireMockServer;


    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void tearDown() {
        WireMock.shutdownServer();
    }

    @Test
    void test_should_return_repos() throws IOException {
        // given
        String githubReposResponse = FileUtil.readFromFileToString("files/github_repositories_for_a_user_response.json");
        String githubBranchesResponse = FileUtil.readFromFileToString("files/github_list_branches_response.json");
        String gitapiReposResponse = FileUtil.readFromFileToString("files/gitapi_repos_response.json");

        stubFor(get(urlEqualTo("/users/octocat/repos?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubReposResponse)));

        stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubBranchesResponse)));

        // when  // then
        webTestClient.get()
                .uri("v1/repos/octocat")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json(gitapiReposResponse, true);
    }

    @Test
    void test_user_not_found() throws IOException {
        // given
        String gitapiUserNotFoundResponse = FileUtil.readFromFileToString("files/gitapi_user_not_found_response.json");

        stubFor(get(urlEqualTo("/users/UserDoesntExist/repos?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND_404)));

        // when // then
        webTestClient.get()
                .uri("v1/repos/UserDoesntExist")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json(gitapiUserNotFoundResponse, true);
    }
}

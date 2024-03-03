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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
    private static int wireMockPort;
    private static String githubReposResponse;
    private static String githubBranchesResponse;

    @BeforeAll
    static void setup() throws IOException {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        wireMockPort = wireMockServer.port();
        WireMock.configureFor("localhost", wireMockPort);

        githubReposResponse = FileUtil.readFromFileToString("files/github_repositories_for_a_user_response.json");
        githubBranchesResponse = FileUtil.readFromFileToString("files/github_list_branches_response.json");
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.port", () -> wireMockPort);
        registry.add("github.baseUrl", () -> "http://localhost:" + wireMockPort);
    }

    @AfterAll
    static void tearDown() {
        WireMock.shutdownServer();
    }

    @Test
    void test_should_return_repos() throws IOException {
        // given
        String gitapiReposResponse = FileUtil.readFromFileToString("files/gitapi_repos_response.json");

        stubFor(get(urlEqualTo("/users/octocat/repos?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubReposResponse)));

        stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubBranchesResponse)));

        // when  // then
        webTestClient.get()
                .uri("v1/repos/octocat")
                .accept(MediaType.APPLICATION_JSON)
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
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json(gitapiUserNotFoundResponse, true);
    }

    @Test
    void test_should_return_all_repos_when_repos_response_from_github_is_paginated() throws IOException {
        // given
        String gitapiMultipleReposResponse = FileUtil.readFromFileToString("files/gitapi_multiple_repos_response.json");

        // stub for first page response from GitHub
        stubFor(get(urlEqualTo("/users/octocat/repos?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("link", "<http://localhost:" + wireMockPort + "/user/111487260/repos?per_page=100&page=2>; rel=\"next\", <http://localhost:" + wireMockPort + "/user/111487260/repos?per_page=100&page=2>; rel=\"last\"")
                        .withBody(githubReposResponse)));

        // stub for last page response from GitHub
        stubFor(get(urlEqualTo("/user/111487260/repos?per_page=100&page=2"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("link", "<http://localhost:" + wireMockPort + "/user/111487260/repos?per_page=100&page=1>; rel=\"prev\", <http://localhost:" + wireMockPort + "/user/111487260/repos?per_page=100&page=1>; rel=\"first\"")
                        .withBody(githubReposResponse)));

        // stub for branches response from GitHub - not relevant for this test case so return empty
        stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        // when // then
        webTestClient.get()
                .uri("v1/repos/octocat")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json(gitapiMultipleReposResponse, true);
    }

    @Test
    void test_should_return_all_branches_when_branches_response_from_github_is_paginated() throws IOException {
        // given
        String gitapiMultipleBranchesResponse = FileUtil.readFromFileToString("files/gitapi_multiple_branches_response.json");

        stubFor(get(urlEqualTo("/users/octocat/repos?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubReposResponse)));

        // stub for first page response from GitHub
        stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("link", "<http://localhost:" + wireMockPort + "/repositories/111487260/branches?per_page=100&page=2>; rel=\"next\", <http://localhost:" + wireMockPort + "/repositories/111487260/branches?per_page=100&page=2>; rel=\"last\"")
                        .withBody(githubBranchesResponse)));

        // stub for last page response from GitHub
        stubFor(get(urlEqualTo("/repositories/111487260/branches?per_page=100&page=2"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("link", "<http://localhost:" + wireMockPort + "/repositories/111487260/branches?per_page=100&page=1>; rel=\"prev\", <http://localhost:" + wireMockPort + "/repositories/111487260/branches?per_page=100&page=1>; rel=\"first\"")
                        .withBody(githubBranchesResponse)));

        // when // then
        webTestClient.get()
                .uri("v1/repos/octocat")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json(gitapiMultipleBranchesResponse, true);
    }
}

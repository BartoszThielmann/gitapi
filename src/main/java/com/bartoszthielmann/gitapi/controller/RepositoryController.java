package com.bartoszthielmann.gitapi.controller;

import com.bartoszthielmann.gitapi.dto.RepositoryDto;
import com.bartoszthielmann.gitapi.service.RepositoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class RepositoryController {

    private RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping(value = "/repos/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RepositoryDto> findUserReposByUsername(@PathVariable String username) {
        List<RepositoryDto> repos = repositoryService.getUserRepos(username);
        return repos;
    }
}

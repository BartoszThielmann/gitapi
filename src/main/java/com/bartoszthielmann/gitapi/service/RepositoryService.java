package com.bartoszthielmann.gitapi.service;

import com.bartoszthielmann.gitapi.client.GitHubWebClient;
import com.bartoszthielmann.gitapi.dto.RepositoryDto;
import com.bartoszthielmann.gitapi.entity.Repository;
import com.bartoszthielmann.gitapi.mapper.BranchMapper;
import com.bartoszthielmann.gitapi.mapper.RepositoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositoryService {

    private final GitHubWebClient gitHubWebClient;
    private final RepositoryMapper repositoryMapper;
    private final BranchMapper branchMapper;

    public RepositoryService(GitHubWebClient gitHubWebClient, RepositoryMapper repositoryMapper, BranchMapper branchMapper) {
        this.gitHubWebClient = gitHubWebClient;
        this.repositoryMapper = repositoryMapper;
        this.branchMapper = branchMapper;
    }

    public List<RepositoryDto> getUserRepos(String username) {
        List<Repository> repositories = gitHubWebClient.getUserRepos(username);
        return repositories.stream()
                .filter(repository -> !repository.isFork())
                .map(repositoryMapper::repositoryToRepositoryDto)
                .map(repositoryDto -> {
                    repositoryDto.setBranches(gitHubWebClient.getRepoBranches(repositoryDto.getOwnerLogin(), repositoryDto.getRepoName())
                            .stream()
                            .map(branchMapper::branchToBranchDto)
                            .collect(Collectors.toList()));
                    return repositoryDto;
                })
                .collect(Collectors.toList());
    }
}

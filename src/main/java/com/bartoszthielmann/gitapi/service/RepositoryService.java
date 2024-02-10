package com.bartoszthielmann.gitapi.service;

import com.bartoszthielmann.gitapi.client.GitHubWebClient;
import com.bartoszthielmann.gitapi.dto.RepositoryDto;
import com.bartoszthielmann.gitapi.entity.Branch;
import com.bartoszthielmann.gitapi.entity.Repository;
import com.bartoszthielmann.gitapi.mapper.BranchMapper;
import com.bartoszthielmann.gitapi.mapper.RepositoryMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<RepositoryDto> getUserRepos(String username) {
        Flux<Repository> repositoryFlux = gitHubWebClient.getUserRepos(username);

        return repositoryFlux
                .filter(repository -> !repository.isFork()) // business requirement to filter out forked repos
                .flatMap(repository -> Mono.just(repositoryMapper.repositoryToRepositoryDto(repository)))
                .flatMap(repositoryDto -> getBranchesForRepositoryDto(repositoryDto)
                        .map(branch -> branchMapper.branchToBranchDto(branch))
                        .collectList()
                        .map(branchDtos -> {
                            repositoryDto.setBranches(branchDtos);
                            return repositoryDto;
                        }));
    }

    private Flux<Branch> getBranchesForRepositoryDto(RepositoryDto repositoryDto) {
        String owner = repositoryDto.getOwnerLogin();
        String repo = repositoryDto.getRepoName();

        return gitHubWebClient.getRepoBranches(owner, repo);
    }
}

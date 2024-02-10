package com.bartoszthielmann.gitapi.unit;

import com.bartoszthielmann.gitapi.client.GitHubWebClient;
import com.bartoszthielmann.gitapi.dto.RepositoryDto;
import com.bartoszthielmann.gitapi.entity.Repository;
import com.bartoszthielmann.gitapi.mapper.RepositoryMapper;
import com.bartoszthielmann.gitapi.service.RepositoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RepositoryServiceUnitTest {

    @Mock private GitHubWebClient gitHubWebClient;
    @Mock private RepositoryMapper repositoryMapper;
    @InjectMocks private RepositoryService repositoryService;

    @Test
    public void test_getUserRepos() {
        // given
        Repository repository1 = new Repository("repo1", new Repository.Owner("owner1"), false);
        Repository repository2 = new Repository("repo2", new Repository.Owner("owner1"), true);

        RepositoryDto repository1Dto = new RepositoryDto("repo1", "owner1", Collections.emptyList());

        given(gitHubWebClient.getUserRepos("testUsername"))
                .willReturn(Flux.fromIterable(Arrays.asList(repository1, repository2)));
        given(gitHubWebClient.getRepoBranches("owner1", "repo1")).willReturn(Flux.empty());
        given(repositoryMapper.repositoryToRepositoryDto(repository1)).willReturn(repository1Dto);

        // when
        Flux<RepositoryDto> result = repositoryService.getUserRepos("testUsername");

        // then
        List<RepositoryDto> resultList = result.collectList().block();
        assertThat(resultList.size() == 1);
        assertThat(resultList.getFirst().getRepoName().equals("repo1"));
        assertThat(resultList.getFirst().getRepoName().equals("owner1"));
        assertThat(resultList.getFirst().getBranches().isEmpty());
    }
}

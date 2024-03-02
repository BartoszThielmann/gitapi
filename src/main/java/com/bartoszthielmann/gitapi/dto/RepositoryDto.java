package com.bartoszthielmann.gitapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDto {
    private String repoName;
    private String ownerLogin;
    private List<BranchDto> branches;
}

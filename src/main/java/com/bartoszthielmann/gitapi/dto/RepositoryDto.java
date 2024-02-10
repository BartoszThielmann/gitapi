package com.bartoszthielmann.gitapi.dto;

import java.util.List;

public class RepositoryDto {

    private String repoName;
    private String ownerLogin;
    private List<BranchDto> branches;

    public RepositoryDto() {}

    public RepositoryDto(String repoName, String ownerLogin, List<BranchDto> branches) {
        this.repoName = repoName;
        this.ownerLogin = ownerLogin;
        this.branches = branches;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public List<BranchDto> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchDto> branches) {
        this.branches = branches;
    }
}

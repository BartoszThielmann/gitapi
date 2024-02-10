package com.bartoszthielmann.gitapi.dto;

public class BranchDto {

    private String name;
    private String sha;

    public BranchDto() {}

    public BranchDto(String name, String sha) {
        this.name = name;
        this.sha = sha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }
}



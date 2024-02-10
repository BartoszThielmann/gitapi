package com.bartoszthielmann.gitapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository {
    @JsonProperty("name")
    private String repoName;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("fork")
    private boolean fork;

    public Repository() {}

    public Repository(String repoName, Owner owner, boolean fork) {
        this.repoName = repoName;
        this.owner = owner;
        this.fork = fork;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public static class Owner {
        @JsonProperty("login")
        private String ownerLogin;

        public Owner() {}

        public Owner(String ownerLogin) {
            this.ownerLogin = ownerLogin;
        }

        public String getOwnerLogin() {
            return ownerLogin;
        }

        public void setOwnerLogin(String ownerLogin) {
            this.ownerLogin = ownerLogin;
        }
    }
}

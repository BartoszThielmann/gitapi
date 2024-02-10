package com.bartoszthielmann.gitapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Branch {

    @JsonProperty("name")
    private String name;

    @JsonProperty("commit")
    private Commit commit;

    public Branch() {}

    public Branch(String name, Commit commit) {
        this.name = name;
        this.commit = commit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public String getSha() {
        return commit != null ? commit.getSha() : null;
    }

    public void setSha(String sha) {
        if (commit == null) {
            commit = new Commit();
        }
        commit.setSha(sha);
    }

    public static class Commit {

        @JsonProperty("sha")
        private String sha;

        public Commit() {}

        public Commit(String sha) {
            this.sha = sha;
        }

        public String getSha() {
            return sha;
        }

        public void setSha(String sha) {
            this.sha = sha;
        }
    }
}
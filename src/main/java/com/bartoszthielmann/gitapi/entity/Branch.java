package com.bartoszthielmann.gitapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    @JsonProperty("name")
    private String name;

    @JsonProperty("commit")
    private Commit commit;

    public String getSha() {
        return commit != null ? commit.getSha() : null;
    }

    public void setSha(String sha) {
        if (commit == null) {
            commit = new Commit();
        }
        commit.setSha(sha);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Commit {

        @JsonProperty("sha")
        private String sha;
    }
}
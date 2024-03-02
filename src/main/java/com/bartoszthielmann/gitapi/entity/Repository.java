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
public class Repository {
    @JsonProperty("name")
    private String repoName;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("fork")
    private boolean fork;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Owner {
        @JsonProperty("login")
        private String ownerLogin;
    }
}

package com.example.ems.response;

import lombok.Data;
import reactor.core.publisher.Flux;

@Data
public class RandomUserResponse {
    private Flux<Result> results;

    @Data
    public static class Result {
        private Name name;
        private String email;
        private Picture picture;

        @Data
        public static class Name {
            private String title;
            private String first;
            private String last;
        }

        @Data
        public static class Picture {
            private String large;
            private String medium;
            private String thumbnail;
        }
    }
}

package com.example.ems.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private Instant timestamp;
    private int status;
    private String message;
    private Object data;

    public static Response success(String message, Object data) {
        return new Response(Instant.now(), 200, message, data);
    }

    public static Response error(String message, int status, Object data) {
        return new Response(Instant.now(), status, message, data);
    }

}

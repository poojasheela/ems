package com.example.ems.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private Object data;

    public static Response success(String message, Object data) {
        return new Response(LocalDateTime.now(), 200, message, data);
    }

    public static Response error(String message, int status, Object data) {
        return new Response(LocalDateTime.now(), status, message, data);
    }

}

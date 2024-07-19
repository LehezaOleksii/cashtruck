package com.projects.oleksii.leheza.cashtruck.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ErrorResponse {

    private Integer code;
    private String error;
    private String message;
    private String path;
}
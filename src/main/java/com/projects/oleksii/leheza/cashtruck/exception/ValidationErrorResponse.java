package com.projects.oleksii.leheza.cashtruck.exception;

import java.util.List;

public record ValidationErrorResponse(String field, List<String> errors) {
}

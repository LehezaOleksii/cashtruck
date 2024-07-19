//package com.projects.oleksii.leheza.cashtruck.exception;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.ConstraintViolationException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@ControllerAdvice
//@Slf4j
//public class GeneralResourceExceptionHandler {
//
//    @ExceptionHandler(ImageException.class)
//    public ResponseEntity<ErrorResponse> handleException(ImageException ex, HttpServletRequest request) {
//        log.error("handle error ImageException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .error("Resource Not Found")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }
//
//    @ExceptionHandler(PaymentException.class)
//    public ResponseEntity<ErrorResponse> handleException(PaymentException ex, HttpServletRequest request) {
//        log.error("handle error PaymentException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
//                .error("Payment error")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
//    }
//
//    @ExceptionHandler(ResourceAlreadyExistException.class)
//    public ResponseEntity<ErrorResponse> handleException(ResourceAlreadyExistException ex, HttpServletRequest request) {
//        log.error("handle error ResourceAlreadyExistException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.CONFLICT.value())
//                .error("Resource Already Exists ")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
//    }
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleException(ResourceNotFoundException ex, HttpServletRequest request) {
//        log.error("handle error ResourceNotFoundException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.NOT_FOUND.value())
//                .error("Resource Not Found")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//    }
//
//    @ExceptionHandler(SecurityException.class)
//    public ResponseEntity<ErrorResponse> handleException(SecurityException ex, HttpServletRequest request) {
//        log.error("handle error SecurityException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.UNAUTHORIZED.value())
//                .error("Security exception")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//    }
//
//    @ExceptionHandler(SmtpException.class)
//    public ResponseEntity<ErrorResponse> handleException(SmtpException ex, HttpServletRequest request) {
//        log.error("handle error SmtpException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
//                .error("SMTP system exception")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
//    }
//
//    @ExceptionHandler(UserPlanException.class)
//    public ResponseEntity<ErrorResponse> handleException(UserPlanException ex, HttpServletRequest request) {
//        log.error("handle error UserPlanException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
//                .error("User Plan Exception")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    public ResponseEntity<ErrorResponse> handleException(ValidationException ex, HttpServletRequest request) {
//        log.error("handle error ValidationException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.BAD_REQUEST.value())
//                .error("Validation Exception")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
//        log.error("handle error Exception; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .error("Exception")
//                .message(ex.getMessage())
//                .path(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }
//
//    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
//    public ResponseEntity<Map<String, List<ValidationErrorResponse>>> handleValidationErrors(MethodArgumentNotValidException ex) {
//        log.error("handle error ConstraintViolationException; message:{} , cause: {}", ex.getMessage(), ex.getCause().getMessage());
//        Map<String, List<String>> groupedErrorMessages = ex.getBindingResult().getFieldErrors().stream()
//                .collect(Collectors.groupingBy(
//                        FieldError::getField,
//                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
//                ));
//        List<ValidationErrorResponse> validationErrorResponses = groupedErrorMessages.entrySet().stream()
//                .map(entry -> new ValidationErrorResponse(entry.getKey(), entry.getValue()))
//                .toList();
//        return new ResponseEntity<>(getErrorsMap(validationErrorResponses), HttpStatus.BAD_REQUEST);
//    }
//
//    private Map<String, List<ValidationErrorResponse>> getErrorsMap(List<ValidationErrorResponse> errors) {
//        Map<String, List<ValidationErrorResponse>> errorResponse = new HashMap<>();
//        errorResponse.put("errors", errors);
//        return errorResponse;
//    }
//}

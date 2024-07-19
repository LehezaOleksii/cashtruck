package com.projects.oleksii.leheza.cashtruck.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GeneralResourceExceptionHandlerThymeleaf {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        log.error("handle error message:{} ; cause: ", ex.getMessage(),ex.getCause());
        ModelAndView modelAndView = new ModelAndView("error/error_page");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }
}

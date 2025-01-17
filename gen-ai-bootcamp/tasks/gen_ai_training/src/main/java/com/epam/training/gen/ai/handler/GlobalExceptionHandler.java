package com.epam.training.gen.ai.handler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleGlobalException(Exception ex) {
    logger.error("Error: {}, message: {}", ex.getStackTrace(), ex.getMessage());
    return new ResponseEntity<>("Internal server error: " + ex.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
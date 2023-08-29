package com.example.matchup.matchupbackend.error.advice;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity globalExHandler(Exception ex) {
        log.error("[exceptionHandle] ex", ex);
        ErrorResult errorResult = ErrorResult.of(ErrorCode.SEVER_GLOBAL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }
}

package com.example.matchup.matchupbackend.error.advice;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.ErrorResult;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {
    //------------Natural Error------------//

    /**
     * DTO의 request(@QueryParam)가 컨트롤러 @Valid에서 빈 검증 통과 못한 경우  (G-003)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity MethodArgumentNotValidExHandler(MethodArgumentNotValidException ex) {
        Map<String, List<String>> messageExtra = new HashMap<>(); //ex) type: [에러1, 에러2, 에러3...]
        convertBindingResultToMap(ex, messageExtra);
        ErrorResult errorResponseDto = ErrorResult.of(ErrorCode.REQUEST_FIELD_ERROR, messageExtra);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * 토큰 없이 인가가 필요한 리소스에 유저가 접근하는 경우 (G-005)
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity MissingRequestHeaderExHandler(MissingRequestHeaderException ex) {
        ErrorResult errorResponseDto = ErrorResult.of(ErrorCode.MISSING_REQUEST_HEADER, ex.getHeaderName());
        return ResponseEntity.status(BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * 그 외 서버 내부 오류인 경우 (G-001)

     @ExceptionHandler(Exception.class) public ResponseEntity globalExHandler(Exception ex) {
     log.error("서버 오류 500");
     ErrorResult errorResult = ErrorResult.of(ErrorCode.SEVER_GLOBAL_ERROR);
     return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResult);
     }*/

    //------------Custom Error------------//

    /**
     * 잘못된 토큰을 가지고 있는 유저가 인가가 필요한 리소스에 접근하는 경우 (G-004)
     */
    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity AuthorizeExHandler(AuthorizeException ex) {
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), ex.getResource());
        return ResponseEntity.status(UNAUTHORIZED).body(errorResponseDto);
    }

    //--매서드 모음--//
    private void convertBindingResultToMap(BindingResult ex, Map<String, List<String>> messageExtra) {
        if (ex.hasErrors()) {
            ex.getFieldErrors()
                    .forEach(fieldError ->
                            messageExtra.computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
                                    .add(fieldError.getDefaultMessage()));
        }
    }
}

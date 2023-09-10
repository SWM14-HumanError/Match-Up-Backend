package com.example.matchup.matchupbackend.error.advice;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.ErrorResult;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx.DuplicateAcceptTeamUserException;
import com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx.DuplicateRecruitException;
import com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx.DuplicateTeamRecruitException;
import com.example.matchup.matchupbackend.error.exception.FileEx.FileExtensionException;
import com.example.matchup.matchupbackend.error.exception.FileEx.FileUploadException;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidValueException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.ResourceNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
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
        convertBindingResultToMap(ex.getBindingResult(), messageExtra);
        ErrorResult errorResponseDto = ErrorResult.of(ErrorCode.REQUEST_FIELD_ERROR, messageExtra);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * 토큰 없이 인가가 필요한 리소스에 유저가 접근하는 경우 (G-005)
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity MissingRequestHeaderExHandler(MissingRequestHeaderException ex) {
        String messageExtra = ex.getHeaderName();
        ErrorResult errorResponseDto = ErrorResult.of(ErrorCode.MISSING_REQUEST_HEADER, messageExtra);
        return ResponseEntity.status(UNAUTHORIZED).body(errorResponseDto);
    }

    /** 에러 난 경우 에러 메세지를 보기 위해 주석처리
     * 그 외 서버 내부 오류인 경우 (G-001)

     @ExceptionHandler(Exception.class) public ResponseEntity globalExHandler(Exception ex) {
     log.error("서버 오류 500");
     ErrorResult errorResult = ErrorResult.of(ErrorCode.SEVER_GLOBAL_ERROR);
     return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResult);
     }*/

    //------------Custom Error------------//
    //ex에 어떤걸 들고 올지 모르니까 ex.getErrorCode() 사용

    /**
     * 잘못된 토큰을 가지고 있는 유저가 인가가 필요한 리소스에 접근하는 경우 (G-004)
     */
    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity AuthorizeExHandler(AuthorizeException ex) {
        String messageExtra = ex.getResource();
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), messageExtra);
        return ResponseEntity.status(UNAUTHORIZED).body(errorResponseDto);
    }

    /**
     * 존재하지 않거나 삭제된 팀 세부 사항에 접근 하는 경우 (T-S-001)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity ResourceNotFoundExHandler(ResourceNotFoundException ex) {
        String messageExtra = ex.getDetailInfo();
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), messageExtra);
        return ResponseEntity.status(NOT_FOUND).body(errorResponseDto);
    }

    /**
     * 권한이 없는 유저가 접근 하는 경우 (T-S-002)
     */
    @ExceptionHandler(ResourceNotPermitException.class)
    public ResponseEntity ResourceNotPermitExHandler(ResourceNotPermitException ex) {
        String messageExtra = ex.getResource();
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), messageExtra);
        return ResponseEntity.status(UNAUTHORIZED).body(errorResponseDto);
    }

    /**
     * 현재 모집한 팀원보다 Max 팀원을 적게 설정 할때  (T-S-003)
     */
    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity InvalidValueExHandler(InvalidValueException ex) {
        Long messageExtra = ex.getRequestValue();
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), messageExtra);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * 현재 모집한 팀원보다 Max 팀원을 적게 설정 할때  (T-S-003)
     */
    @ExceptionHandler(DuplicateRecruitException.class)
    public ResponseEntity DuplicateRecruitExHandler(DuplicateRecruitException ex) {
        String messageExtra = "";
        if(ex instanceof DuplicateTeamRecruitException){
            messageExtra = ((DuplicateTeamRecruitException) ex).getErrorDetail();
        }
        else if(ex instanceof DuplicateAcceptTeamUserException){
            messageExtra = ((DuplicateAcceptTeamUserException) ex).getErrorDetail();
        }
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), messageExtra);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * 파일 업로드 관련 에러가 발생한 경우
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity FileUploadExHandler(FileUploadException ex) {
        String messageExtra = ex.getDetailInfo();
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), messageExtra);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    /**
     * 파일 확장자 관련 에러가 발생한 경우
     */
    @ExceptionHandler(FileExtensionException.class)
    public ResponseEntity FileExtensionExHandler(FileExtensionException ex) {
        String requestExt = ex.getRequestExt();
        List<String> supportExt = ex.getSupportExt();
        log.info("지원하는 확장자: " + supportExt);
        log.info("사용자가 보낸 확장자: " + requestExt);
        Map<String, List<String>> extraInfo = new HashMap<>(); //ppt, [jpeg, jpg, png, gif]
        extraInfo.put(requestExt, supportExt);
        ErrorResult errorResponseDto = ErrorResult.of(ex.getErrorCode(), extraInfo);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponseDto);
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

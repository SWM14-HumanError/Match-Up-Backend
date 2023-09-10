package com.example.matchup.matchupbackend.error.exception.FileEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

@Getter
public class FileUploadException extends CustomException {
    private String detailInfo;
    private String resource;

    public FileUploadException() {
        super(ErrorCode.FILE_UPLOAD_ERROR);
    }

    public FileUploadException(String detailInfo, String resource) {
        super(ErrorCode.FILE_UPLOAD_ERROR);
        this.detailInfo = detailInfo;
        this.resource = resource;
    }
}

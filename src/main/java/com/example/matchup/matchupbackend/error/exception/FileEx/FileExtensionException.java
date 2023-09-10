package com.example.matchup.matchupbackend.error.exception.FileEx;

import com.example.matchup.matchupbackend.error.exception.CustomException;

import static com.example.matchup.matchupbackend.error.ErrorCode.FILE_EXTENSION_ERROR;

public class FileExtensionException extends CustomException {
    private String supportExt;
    private String requestExt;

    public FileExtensionException(String supportExt, String requestExt) {
        super(FILE_EXTENSION_ERROR);
        this.supportExt = supportExt;
        this.requestExt = requestExt;
    }
}

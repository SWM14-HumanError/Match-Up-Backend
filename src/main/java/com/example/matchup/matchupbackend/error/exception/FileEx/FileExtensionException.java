package com.example.matchup.matchupbackend.error.exception.FileEx;

import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

import java.util.List;

import static com.example.matchup.matchupbackend.error.ErrorCode.FILE_EXTENSION_ERROR;

@Getter
public class FileExtensionException extends CustomException {
    private List<String> supportExt;
    private String requestExt;

    public FileExtensionException(List<String> supportExt, String requestExt) {
        super(FILE_EXTENSION_ERROR);
        this.supportExt = supportExt;
        this.requestExt = requestExt;
    }
}

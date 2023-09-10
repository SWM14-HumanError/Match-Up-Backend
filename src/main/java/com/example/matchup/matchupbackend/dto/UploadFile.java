package com.example.matchup.matchupbackend.dto;

import lombok.Data;

import java.net.URL;

@Data
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;
    private URL S3Url;

    public UploadFile(String uploadFileName, String storeFileName, URL s3Url) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        S3Url = s3Url;
    }
}

package com.example.matchup.matchupbackend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.error.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadFile storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null; //예외 처리 고려
        }
        String originalFileName = file.getOriginalFilename(); //실제 파일 이름(중복가능)
        String storeFileName = createStoreFileName(originalFileName); //저장용 파일 이름

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream fileInputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, storeFileName, fileInputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new FileUploadException("파일 InputStream을 만들던 중 에러가 발생하였습니다.", originalFileName);
        }
        URL S3Url = amazonS3.getUrl(bucket, storeFileName);
        return new UploadFile(originalFileName, storeFileName, S3Url);
    }

    /**
     * 실제 파일 이름(new.png)를 버킷 저장용 파일 이름(UUID.png)으로 만드는 매서드
     * @param originalFileName
     * @return
     */
    private String createStoreFileName(String originalFileName) {
        String extension = extractExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + "." + extension;
        return fileName;
    }

    /**
     * 확장자를 추출 하는 매서드 ex) image.png에서 "png" 추출
     * @param originalFilename
     * @return
     */
    private String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    /**
     * 파일 삭제 하는 메서드
     * @param originalFilename
     */
    public void deleteImage(String originalFilename)  {
        amazonS3.deleteObject(bucket, originalFilename);
    }
}

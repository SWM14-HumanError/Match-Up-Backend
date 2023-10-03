package com.example.matchup.matchupbackend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.error.exception.FileEx.FileExtensionException;
import com.example.matchup.matchupbackend.error.exception.FileEx.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일 저장하는 매서드 (일단은 사진 파일만 저장합니다)
     * @param file
     * @return
     */
    public UploadFile storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("빈 파일은 업로드 할 수 없습니다.", file.getOriginalFilename());
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
     *
     * @param originalFileName
     * @return
     */
    private String createStoreFileName(String originalFileName) {
        String extension = extractExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + "." + extension;
        return fileName;
    }

    /**
     * 확장자를 추출 하는 매서드 ex) image.png에서 "png" 추출 => 사진파일만 저장한다고 가정
     *
     * @param originalFilename
     * @return
     */
    private String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(pos + 1);
        isIMGExtension(extension);
        return extension;
    }

    /**
     * 확장자가 이미지 파일인지 확인하는 매서드
     *
     * @param extension
     * @return
     */
    private void isIMGExtension(String extension) {
        List<String> imgExtList = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png", "gif"));
        if (!imgExtList.contains(extension)) {
            throw new FileExtensionException(imgExtList, extension);
        }
    }

    /**
     * 파일 삭제 하는 메서드
     *
     * @param originalFilename
     */
    public void deleteImage(String originalFilename) {
        amazonS3.deleteObject(bucket, originalFilename);
    }

    public MultipartFile convertBase64ToFile(String base64){
        File file = new File(createStoreFileName("myFile1234"));
        byte decode[] = Base64.decodeBase64(base64);
        try{
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decode);
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return (MultipartFile) file;
    }

}

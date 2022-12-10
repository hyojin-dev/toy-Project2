package com.example.banktoyproject.config.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * S3 버킷에 이미지 등록 기능을 제공하는 클래스 입니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class S3Manager {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket; // S3 버킷 이름


    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException(
                        "error: MultipartFile -> File convert fail"
                ));
        return upload(uploadFile, dirName);
    }

    /**
     * 고유한 파일 이름을 부여하여 S3 버킷에 업로드합니다.
     *
     * @param uploadFile 업로드 할 이미지 파일
     * @param dirName    저장할 버킷 폴더 경로
     * @return 저장된 버킷 이미지 url
     */
    private String upload(File uploadFile, String dirName) {
        // S3에 저장되는 파일 이름(랜덤 uuid + 파일명)
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        putS3(uploadFile, fileName);
        removeNewFile(uploadFile); // 로컬에 생성된 File 삭제 (MultipartFile -> File 변환해서 로컬에 파일 생성)
        String cloudFrontUrl = "https://dk9q1cr2zzfmc.cloudfront.net/" + fileName; // AWS 클라우드 프론트로 접근할 것임
        return cloudFrontUrl; // 업로드된 파일의 S3 URL 주소 반환
    }

    /**
     * 버킷에 이미지 파일을 업로드합니다.
     *
     * @param uploadFile 이미지 파일
     * @param fileName   고유한 파일 이름
     * @return 저장된 버킷 이미지 url
     */
    private void putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)); // PublicRead 권한으로 업로드
    }

    /**
     * 로컬에 파일을 지웁니다.
     *
     * @param targetFile 변환된 파일
     */
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
        } else {
            log.info("File delete fail");
        }
    }

    /**
     * 이미지 파일을 파일로 변환합니다.
     *
     * @param file 이미지 파일
     * @return 변환된 파일 클래스
     * @throws IOException
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    /**
     * 상품을 삭제합니다.
     *
     * @param reviewImgUrl
     */
    public void delete(String reviewImgUrl) { // S3 파일 삭제
        try {
//            String filePath = reviewImgUrl.replace("https://dk9q1cr2zzfmc.cloudfront.net/", "");
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, reviewImgUrl);
            amazonS3Client.deleteObject(deleteObjectRequest);
        } catch (
                AmazonServiceException e) {
            e.printStackTrace();
        } catch (
                SdkClientException e) {
            e.printStackTrace();
        }
    }
}
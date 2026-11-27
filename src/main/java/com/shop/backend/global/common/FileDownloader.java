package com.shop.backend.global.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Component
public class FileDownloader {

    // 파일 저장 루트 (FileUploader와 동일, 없으면 /var/shop/uploads)
    @Value("${file.upload-dir:/var/shop/uploads}")
    private String uploadDir;

    public ResponseEntity<Resource> download(String path) {
        /*
         * {uploadDir}/{path} 경로의 파일을 다운로드 응답으로 반환
         * path가 '/'로 시작하면 절대 경로로 그대로 사용 (예전 방식으로 저장된 데이터 호환)
         * 요청 경로를 그대로 이어 붙여 사용 (../ 등 경로 검증 없음 -> 다운로드 취약점 포인트)
         * 파일이 없으면 404 반환
         */
        File file = path.startsWith("/") ? new File(path) : new File(uploadDir + "/" + path);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)   // 브라우저에서 열지 않고 다운로드
                .contentLength(file.length())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.getName(), StandardCharsets.UTF_8)   // 한글 파일명 처리
                                .build()
                                .toString())
                .body(resource);
    }
}

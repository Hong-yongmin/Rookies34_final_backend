package com.shop.backend.global.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FileUploader {

    // 리눅스 WAS 기준 파일 저장 루트 (application.properties의 file.upload-dir, 없으면 /var/shop/uploads)
    @Value("${file.upload-dir:/var/shop/uploads}")
    private String uploadDir;

    public String save(MultipartFile file, String subDir) throws IOException {
        /*
         * multipart로 받은 파일을 {uploadDir}/{subDir}/{원본 파일명} 에 저장
         * 파일명은 클라이언트가 보낸 원본 파일명 그대로 사용
         * 업로드 루트 기준 상대 경로({subDir}/{원본 파일명}) 반환 -> DB에 저장, 다운로드 시 그대로 사용
         */
        String dirPath = uploadDir + "/" + subDir;
        new File(dirPath).mkdirs();

        String relativePath = subDir + "/" + file.getOriginalFilename();
        String path = uploadDir + "/" + relativePath;
        try (InputStream in = file.getInputStream();
             FileOutputStream fos = new FileOutputStream(path)) {
            in.transferTo(fos);
        }
        return relativePath;
    }

    public void delete(String path) {
        /*
         * 저장된 파일 삭제, path는 save()가 반환한 상대 경로
         */
        if (path == null) return;
        new File(uploadDir + "/" + path).delete();
    }
}

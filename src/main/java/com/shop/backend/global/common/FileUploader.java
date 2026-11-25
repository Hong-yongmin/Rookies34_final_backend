package com.shop.backend.global.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class FileUploader {

    // 리눅스 WAS 기준 파일 저장 루트 (application.properties의 file.upload-dir, 없으면 /var/shop/uploads)
    @Value("${file.upload-dir:/var/shop/uploads}")
    private String uploadDir;

    public String save(String base64, String fileName, String subDir) throws IOException {
        /*
         * base64로 인코딩된 파일을 디코딩해 {uploadDir}/{subDir}/{fileName} 에 저장
         * 파일명은 클라이언트가 보낸 값 그대로 사용 (확장자·경로 검증 없음, 같은 이름은 덮어씀)
         * 저장된 파일 경로 반환 -> DB에 저장
         */
        if (base64.contains(",")) { // data:...;base64, 형식이면 앞부분 제거
            base64 = base64.substring(base64.indexOf(",") + 1);
        }
        byte[] data = Base64.getMimeDecoder().decode(base64); // 줄바꿈 등 포함돼도 디코딩

        String dirPath = uploadDir + "/" + subDir;
        new File(dirPath).mkdirs();

        String path = dirPath + "/" + fileName;
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        }
        return path;
    }

    public void delete(String path) {
        /*
         * 저장된 파일 삭제 (DB 저장 실패 시 정리용)
         */
        if (path == null) return;
        new File(path).delete();
    }
}

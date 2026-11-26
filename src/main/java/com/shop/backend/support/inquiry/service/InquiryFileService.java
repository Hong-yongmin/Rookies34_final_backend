
package com.shop.backend.support.inquiry.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class InquiryFileService {

    // 최대 파일 크기: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // 허용할 확장자
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "pdf");

    // application.properties에서 저장 경로 가져오기
    @Value("${app.upload.inquiry-dir}")
    private String uploadDir;

    // 첨부파일 저장
    public String saveFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        // 파일 크기 검사
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "첨부파일은 5MB 이하만 가능합니다."
            );
        }

        // 원본 파일명 확인
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException(
                    "파일명이 올바르지 않습니다."
            );
        }

        // 확장자 추출
        int dotIndex = originalFilename.lastIndexOf(".");

        if (dotIndex < 0) {
            throw new IllegalArgumentException(
                    "허용되지 않는 파일 형식입니다."
            );
        }

        String extension = originalFilename
                .substring(dotIndex + 1)
                .toLowerCase();

        // 허용 확장자 검사
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "JPG, PNG, PDF 파일만 첨부할 수 있습니다."
            );
        }

        // 중복되지 않는 파일명 생성
        String savedFilename =
                UUID.randomUUID() + "." + extension;

        try {
            Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();

            Files.createDirectories(directory);

            Path target = directory.resolve(savedFilename).normalize();

            // 저장 경로가 지정된 디렉터리 내부인지 확인
            if (!target.startsWith(directory)) {
                throw new IllegalArgumentException(
                        "잘못된 파일 저장 경로입니다."
                );
            }

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // 나중에 URL로 접근할 수 있도록 저장 파일명 반환
            return savedFilename;

        } catch (IOException e) {
            throw new RuntimeException(
                    "첨부파일 저장에 실패했습니다.",
                    e
            );
        }
    }
    // 저장된 첨부파일 읽기
    public Path getFilePath(String filename) {

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        Path directory = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        Path filePath = directory.resolve(filename)
                .normalize();

        if (!filePath.startsWith(directory)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("첨부파일을 찾을 수 없습니다.");
        }

        return filePath;
    }
}

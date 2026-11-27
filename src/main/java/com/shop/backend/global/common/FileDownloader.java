package com.shop.backend.global.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@Component
public class FileDownloader {

    private static final String LEGACY_PRODUCT_PREFIX = "/backend-shop/uploads/products/";
    private static final String SAMPLE_CERT_PREFIX = "/dummy-images/cert/";

    // 파일 저장 루트 (FileUploader와 동일, 없으면 /var/shop/uploads)
    @Value("${file.upload-dir:/var/shop/uploads}")
    private String uploadDir;

    @Value("${file.product-upload-dir:/var/shop/uploads/products}")
    private String productUploadDir;

    public ResponseEntity<Resource> download(String path) {
        if (path == null || path.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path certificateDir = Path.of(uploadDir).resolve("cert");
            if (path.startsWith(LEGACY_PRODUCT_PREFIX)) {
                return downloadFile(Path.of(productUploadDir), path.substring(LEGACY_PRODUCT_PREFIX.length()));
            }
            if (path.startsWith(SAMPLE_CERT_PREFIX)) {
                String filename = path.substring(SAMPLE_CERT_PREFIX.length());
                if (!isFilename(filename)) {
                    return ResponseEntity.notFound().build();
                }
                ResponseEntity<Resource> local = downloadFile(certificateDir, filename);
                if (local.getStatusCode().is2xxSuccessful()) {
                    return local;
                }
                return ResponseEntity.status(HttpStatus.FOUND)
                        .cacheControl(CacheControl.noStore())
                        .location(URI.create(SAMPLE_CERT_PREFIX + UriUtils.encodePathSegment(filename, StandardCharsets.UTF_8)))
                        .build();
            }
            if (path.startsWith("cert/")) {
                return downloadFile(certificateDir, path.substring("cert/".length()));
            }
            Path absolutePath = Path.of(path);
            if (absolutePath.isAbsolute() && certificateDir.toAbsolutePath().normalize().equals(absolutePath.getParent())) {
                return downloadFile(certificateDir, absolutePath.getFileName().toString());
            }
        } catch (InvalidPathException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }

    private boolean isFilename(String filename) {
        return !filename.isBlank() && !filename.equals(".") && !filename.equals("..")
                && !filename.contains("/") && !filename.contains("\\");
    }

    private ResponseEntity<Resource> downloadFile(Path directory, String filename) {
        if (!isFilename(filename)) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path root = directory.toRealPath();
            Path file = root.resolve(filename).toRealPath();
            if (!file.startsWith(root) || !Files.isRegularFile(file)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(Files.size(file))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename(filename, StandardCharsets.UTF_8).build().toString())
                    .body(new FileSystemResource(file));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

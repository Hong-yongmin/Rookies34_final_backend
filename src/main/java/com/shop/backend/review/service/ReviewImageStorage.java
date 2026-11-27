package com.shop.backend.review.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@Service
public class ReviewImageStorage {

    public static final String URL_PREFIX = "/uploads/reviews/";
    private static final Logger log = LoggerFactory.getLogger(ReviewImageStorage.class);
    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024;

    private final Path directory;

    public ReviewImageStorage(@Value("${file.review-upload-dir:/var/shop/uploads/reviews}") String directory) {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
    }

    public String save(MultipartFile file, String contextPath) {
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException("후기 사진은 파일당 5MB 이하여야 합니다.");
        }
        Path target = null;
        try {
            String extension = imageExtension(file);
            Files.createDirectories(directory);
            target = Files.createFile(directory.resolve(UUID.randomUUID() + "." + extension));
            file.transferTo(target);

            Path savedFile = target;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        delete(savedFile);
                    }
                }
            });
            return contextPath + URL_PREFIX + target.getFileName();
        } catch (IOException | RuntimeException e) {
            if (target != null) {
                delete(target);
            }
            throw new IllegalStateException("후기 사진을 저장할 수 없습니다.", e);
        }
    }

    private String imageExtension(MultipartFile file) throws IOException {
        try (var input = file.getInputStream(); var stream = new MemoryCacheImageInputStream(input)) {
            var header = new byte[12];
            stream.readFully(header);
            if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                return "webp";
            }
            stream.seek(0);
            var readers = ImageIO.getImageReaders(stream);
            if (readers.hasNext()) {
                var reader = readers.next();
                try {
                    return switch (reader.getFormatName().toLowerCase(Locale.ROOT)) {
                        case "jpeg" -> "jpg";
                        case "png" -> "png";
                        case "gif" -> "gif";
                        default -> throw unsupportedImage();
                    };
                } finally {
                    reader.dispose();
                }
            }
        }
        throw unsupportedImage();
    }

    private IllegalArgumentException unsupportedImage() {
        return new IllegalArgumentException("JPG, PNG, GIF, WebP 사진만 첨부할 수 있습니다.");
    }

    private void delete(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("후기 사진 임시 파일을 삭제하지 못했습니다: {}", file, e);
        }
    }
}

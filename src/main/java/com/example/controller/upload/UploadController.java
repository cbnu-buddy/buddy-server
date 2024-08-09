package com.example.controller.upload;

import com.example.api.ApiResult;
import com.example.service.member.MemberService;
import com.example.service.upload.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Tag(name = "사진 저장 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/upload")
public class UploadController {
    private final UploadService uploadService;

    private static final String UPLOAD_DIRECTORY = "buddy/"; // 상대 경로로 설정

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드", description = "")
    public ApiResult<?> uploadFile(@RequestParam("file") List<MultipartFile> files, HttpServletRequest request) {
        return uploadService.uploadFile(files, request);
    }

    @GetMapping("/image")
    public ResponseEntity<?> getImage(@RequestParam String path) {
        try {
//            String absolutePath = new File("").getAbsolutePath() + File.separator + UPLOAD_DIRECTORY + path;
            File file = new File(path);

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
            }

            FileSystemResource fileSystemResource = new FileSystemResource(file);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Adjust based on file type (JPEG, PNG, etc.)

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileSystemResource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading image");
        }


}}

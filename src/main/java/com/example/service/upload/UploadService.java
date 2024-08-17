package com.example.service.upload;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UploadService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    private static final String UPLOAD_DIRECTORY = "buddy/"; // 상대 경로로 설정

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    public ApiResult<?> uploadFile(List<MultipartFile> files, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<String> uploadedFilePaths = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            return ApiResult.success(uploadedFilePaths+"ff");
        }

        // 절대 경로 생성
        String uploadPath = request.getServletContext().getRealPath("/") + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);

        // buddy 디렉토리가 없으면 생성
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                throw new CustomException(ErrorCode.UPLOAD_DIRECTORY_CREATION_FAILED);
            }
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            // 날짜 및 랜덤 문자열 생성
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String randomStr = UUID.randomUUID().toString().substring(0, 8);
            String uniqueFilename = dateStr + randomStr + fileExtension;

            // 절대 경로로 파일 저장
            File destinationFile = new File(uploadPath + uniqueFilename);
            try {
                file.transferTo(destinationFile);
                // 절대 경로 반환
                uploadedFilePaths.add(destinationFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to upload file: " + originalFilename, e);
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return ApiResult.success(uploadedFilePaths);
    }
}

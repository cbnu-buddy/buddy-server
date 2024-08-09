package com.example.controller.subscribe;

import com.example.api.ApiResult;
import com.example.service.subscribe.SubscribeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Tag(name = "태그 구독 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/subscribe")
public class SubscribeController {

    private final SubscribeService subscribeService;


    @Operation(summary = "태그 구독하기", description = "")
    @PostMapping("/tags/{tagId}")
    public ApiResult<?> subscribeTag(HttpServletRequest request, @PathVariable Long tagId) {
        return subscribeService.subscribeTag(request, tagId);
    }

    @Operation(summary = "태그 구독 취소하기", description = "")
    @DeleteMapping("/tags//{tagId}")
    public ApiResult<?> unsubscribeTag(HttpServletRequest request, @PathVariable Long tagId) {
        return subscribeService.unsubscribeTag(request, tagId);
    }

    @Operation(summary = "회원의 구독 태그 목록 조회", description = "")
    @GetMapping("/my/subscribed-tags")
    public ApiResult<?> getSubscribedTags(HttpServletRequest request) {
        return subscribeService.getSubscribedTags(request);
    }

    @Operation(summary = "태그 신규 게시글 알림 받기", description = "")
    @PostMapping("/tags/{tagId}/notification")
    public ApiResult<?> enableTagNotification(HttpServletRequest request, @PathVariable Long tagId) {
        return subscribeService.enableTagNotification(request, tagId);
    }

    @Operation(summary = "태그 신규 게시글 알림 끄기", description = "")
    @DeleteMapping("/tags/{tagId}/notification")
    public ApiResult<?> disableTagNotification(HttpServletRequest request, @PathVariable Long tagId) {
        return subscribeService.disableTagNotification(request, tagId);
    }
}

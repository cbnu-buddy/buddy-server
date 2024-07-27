package com.example.controller.subscribe;

import com.example.api.ApiResult;
import com.example.dto.request.SubscribeTagRequest;
import com.example.service.subscribe.SubscribeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "태그 구독 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/subscribe")
public class SubscribeController {

    private final SubscribeService subscribeService;


    @Operation(summary = "태구 구독하기", description = "")
    @PostMapping
    public ApiResult<?> subscribeTag(@RequestBody SubscribeTagRequest subscribeTagRequest, HttpServletRequest request) {
        return subscribeService.subscribeTag(subscribeTagRequest, request);
    }
}

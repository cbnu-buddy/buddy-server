package com.example.controller.point;

import com.example.api.ApiResult;
import com.example.dto.request.PointModifyRequest;
import com.example.service.point.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Tag(name = "포인트 API")
public class PointController {
    private final PointService pointService;

    @PostMapping("/point")
    @Operation(summary = "포인트 수정", description = "")
    public ApiResult<?> modifyPoint(@RequestBody PointModifyRequest pointModifyRequest){
        pointService.modifyPoint(pointModifyRequest);
        return ApiResult.success("포인트 충전 완료");
    }
}

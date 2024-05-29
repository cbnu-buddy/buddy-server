package com.example.controller.point;

import com.example.api.ApiResult;
import com.example.dto.request.AddPointRequest;
import com.example.dto.response.PointInfoResponse;
import com.example.service.point.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "멤버 포인트 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/member")
public class PointController {

    private final PointService pointService;

    /*
    포인트 추가
    */
    @PostMapping("/point")
    @Operation(summary = "포인트 추가", description = "")
    public ApiResult<?> addPoint(@RequestBody AddPointRequest addPointRequest,
                                 @AuthenticationPrincipal HttpServletRequest request){

        return pointService.addPoint(addPointRequest, request);
    }


    /*
    포인트 내역 정보 목록 조회
    */
    @GetMapping("/points")
    @Operation(summary = "포인트 내역 조회", description = "")
    public ApiResult<?> getPoints(HttpServletRequest request) {
        return pointService.getPoints(request);
    }


}

package com.example.controller.party;

import com.example.api.ApiResult;
import com.example.dto.request.ChangeEmailRequest;
import com.example.dto.request.ChangePartyAccountRequest;
import com.example.dto.request.ChangePartyRecLimitRequest;
import com.example.dto.request.CreatePartyRequest;
import com.example.service.party.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "파티 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/party")
public class PartyController {

    private final PartyService partyService;

    /*
    파티 생성하기
     */
    @PostMapping
    @Operation(summary = "파티 생성하기", description = "")
    public ApiResult<?> createParty(@RequestBody CreatePartyRequest createPartyRequest,
                                    HttpServletRequest request){

        return partyService.createParty(createPartyRequest, request);
    }

    /*
    파티 해산하기
    */
    @DeleteMapping("/{partyId}")
    @Operation(summary = "파티 해산하기", description = "")
    public ApiResult<?> deleteParty(@PathVariable Long partyId, HttpServletRequest request){

        return partyService.deleteParty(partyId, request);
    }

    /*
    파티 로그인 정보 변경
    */
    @PatchMapping("/{partyId}/account")
    @Operation(summary = "파티 로그인 정보 변경", description = "")
    public ApiResult<?> changePartyAccount(@RequestBody ChangePartyAccountRequest changePartyAccountRequest, HttpServletRequest request){

        return partyService.changePartyAccount(changePartyAccountRequest, request);
    }

    /*
    파티 모집 인원 변경
    */
    @PatchMapping("/{partyId}/rec_limit")
    @Operation(summary = "파티 모집 인원 변경", description = "")
    public ApiResult<?> changePartyRecLimit(@RequestBody ChangePartyRecLimitRequest changePartyRecLimitRequest, HttpServletRequest request){

        return partyService.changePartyRecLimit(changePartyRecLimitRequest, request);
    }

    /*
    특정 서비스 내 매칭이 완료되지 않은 파티 목록 정보 조회
    */
    @GetMapping("/service/{serviceId}")
    @Operation(summary = "특정 서비스 내 매칭이 완료되지 않은 파티 목록 정보 조회", description = "")
    public ApiResult<?> getUnmatchedParties(@PathVariable Long serviceId) {
        return partyService.getUnmatchedParties(serviceId);
    }
}

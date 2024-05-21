package com.example.controller.party;

import com.example.api.ApiResult;
import com.example.dto.request.CreatePartyRequest;
import com.example.service.party.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "파티 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/auth/party")
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
    public ApiResult<?> deleteParty(@PathVariable Long partyId){

        return partyService.deleteParty(partyId);
    }
}

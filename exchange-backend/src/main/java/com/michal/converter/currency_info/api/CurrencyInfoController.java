package com.michal.converter.currency_info.api;

import com.michal.converter.currency_info.dto.InfoRequestDto;
import com.michal.converter.currency_info.dto.InfoResponseDto;
import com.michal.converter.currency_info.service.CurrencyInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/api")
public class CurrencyInfoController {

    private final CurrencyInfoService service;

    @PostMapping("/info")
    ResponseEntity<InfoResponseDto> getCurrencyInfo(@Valid @RequestBody InfoRequestDto code){
        InfoResponseDto response = service.getCurrencyInfo(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

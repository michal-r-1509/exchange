package com.michal.converter.api;

import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import com.michal.converter.service.ConverterServiceProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class ConverterController {

    private final ConverterServiceProvider converterService;

    @PostMapping(path = "/convert")
    ResponseEntity<ResponseDto> convert(@Valid @RequestBody RequestDto data) {
        ResponseDto response = converterService.convert(data);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

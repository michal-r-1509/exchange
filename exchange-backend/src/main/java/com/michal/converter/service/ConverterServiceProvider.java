package com.michal.converter.service;

import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConverterServiceProvider {

    private final ConverterService converterService;

    public ResponseDto convert(RequestDto data) {
        return converterService.convert(data);
    }

}

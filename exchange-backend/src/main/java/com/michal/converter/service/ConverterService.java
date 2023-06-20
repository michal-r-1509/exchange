package com.michal.converter.service;

import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;

public interface ConverterService {
    ResponseDto convert(RequestDto data);
}

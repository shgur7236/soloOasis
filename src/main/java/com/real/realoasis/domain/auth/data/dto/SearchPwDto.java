package com.real.realoasis.domain.auth.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class SearchPwDto {
    private final String id;
}

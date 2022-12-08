package com.real.realoasis.domain.user.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettingResponse {
    private String questionTime;
    private String anniversaryTime;
    private String version;
    private String myCode;
}

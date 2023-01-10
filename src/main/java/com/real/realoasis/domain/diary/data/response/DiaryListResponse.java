package com.real.realoasis.domain.diary.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiaryListResponse {
    private Long diaryId;
    private String content;
    private String title;
    private String writer;
    private String createDate;
}
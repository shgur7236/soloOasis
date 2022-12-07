package com.real.realoasis.domain.diary.presentation;

import com.real.realoasis.domain.diary.presentation.dto.request.CreateDiaryRequest;
import com.real.realoasis.domain.diary.presentation.dto.request.EditDiaryRequest;
import com.real.realoasis.domain.diary.service.CreateDiaryService;
import com.real.realoasis.domain.diary.service.EditDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {
    private final CreateDiaryService createDiaryService;
    private final EditDiaryService editDiaryService;

    @PostMapping("/create")
    public ResponseEntity<Void> createDiary(
            @RequestPart(value = "file", required = false)List<MultipartFile> files,
            @RequestPart(value = "req") CreateDiaryRequest createDiaryRequest) throws Exception {
        createDiaryService.createDiary(createDiaryRequest, files);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/edit/{diaryId}")
    public ResponseEntity<Void> editDiary(
            @PathVariable Long diaryId,
            @RequestPart(value = "file", required = false)List<MultipartFile> files,
            @RequestPart(value = "req") EditDiaryRequest editDiaryRequest) throws Exception {
        editDiaryService.editDiary(diaryId, editDiaryRequest, files);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

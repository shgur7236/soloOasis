package com.real.realoasis.domain.questionAnswer.facade;

import com.real.realoasis.domain.question.entity.Question;
import com.real.realoasis.domain.question.repository.QuestionRepository;
import com.real.realoasis.domain.questionAnswer.entity.QuestionAnswer;
import com.real.realoasis.domain.questionAnswer.exception.QuestionNotFoundException;
import com.real.realoasis.domain.questionAnswer.repository.QuestionAnswerRepository;
import com.real.realoasis.global.error.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class QuestionAnswerFacade {
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionRepository questionRepository;

    public Question findQuestionByQuestionId(Long questionId){
       return questionRepository.findQuestionById(questionId).orElseThrow(() -> new QuestionNotFoundException(ErrorCode.QUESTION_NOT_FOUND_EXCEPTION));
    }
    public void saveAnswer(QuestionAnswer questionAnswer) {
        questionAnswerRepository.save(questionAnswer);
    }
}

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(String authorization, String questionId, String answer) throws AuthorizationFailedException, InvalidQuestionException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        AnswerEntity answerEntity = new AnswerEntity();
        if (userEntity != null) {
            QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
            }
            final ZonedDateTime now = ZonedDateTime.now();
            answerEntity.setAns(answer);
            answerEntity.setUuid(UUID.randomUUID().toString());
            answerEntity.setDate(now);
            answerEntity.setQuestion(questionEntity);
            answerEntity.setUser(userEntity);
            answerEntity = answerDao.createAnswer(answerEntity);

        }
        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(String authorization, String answerId, String content) throws AuthorizationFailedException, AnswerNotFoundException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        AnswerEntity answerEntity = new AnswerEntity();
        if (userEntity != null) {
            answerEntity = answerDao.getAnswerById(answerId);
            if (answerEntity == null) {
                throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
            } else if (!userEntity.getUuid().equals(answerEntity.getUser().getUuid())) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            }

            answerEntity.setAns(content);
            answerEntity = answerDao.editAnswerContent(answerEntity);

        }
        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String authorization, String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        AnswerEntity answerEntity = new AnswerEntity();
        if (userEntity != null) {
            answerEntity = answerDao.getAnswerById(answerId);
            if (answerEntity == null) {
                throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
            } else if (userEntity.getRole().equals("nonadmin") && !userEntity.getUuid().equals(answerEntity.getUser().getUuid())) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
            answerDao.deleteAnswer(answerEntity);
        }
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswersToQuestion(String authorization, String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        List<AnswerEntity> answerEntity = new ArrayList<AnswerEntity>();
        if (userEntity != null) {
            QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
            }
            answerEntity = answerDao.getAnswerByQuestionId(questionId);
        }
        return answerEntity;
    }
}

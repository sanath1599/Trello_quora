package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(String authorization, String content) throws AuthorizationFailedException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        QuestionEntity questionEntity = new QuestionEntity();
        if (userEntity != null) {
            final ZonedDateTime now = ZonedDateTime.now();
            questionEntity.setContent(content);
            questionEntity.setDate(now);
            questionEntity.setUser(userEntity);
            questionEntity.setUuid(UUID.randomUUID().toString());
            questionDao.createQuestion(questionEntity);
        }
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(String authorization) throws AuthorizationFailedException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        List<QuestionEntity> questionEntity = new ArrayList<QuestionEntity>();
        if (userEntity != null) {
            questionEntity = questionDao.getAllQuestions();
        }
        return questionEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(String authorization, String questionId, String content) throws AuthorizationFailedException, InvalidQuestionException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        QuestionEntity questionEntity = new QuestionEntity();
        if (userEntity != null) {
            questionEntity = questionDao.getQuestionById(questionId);
            if(questionEntity==null)
            {
                throw  new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
            }
            else if(!userEntity.getUuid().equals(questionEntity.getUser().getUuid()))
            {
                throw  new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
            }
            questionEntity.setContent(content);
            questionEntity = questionDao.editQuestionContent(questionEntity);
        }
        return questionEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String authorization, String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        QuestionEntity questionEntity = new QuestionEntity();
        if (userEntity != null) {
            questionEntity = questionDao.getQuestionById(questionId);
            if(questionEntity==null)
            {
                throw  new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
            }
            else if(userEntity.getRole().equals("nonadmin") && !userEntity.getUuid().equals(questionEntity.getUser().getUuid()))
            {
                throw  new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
            }
            questionDao.deleteQuestion(questionEntity);
        }
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionsByUser(String authorization, String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        List<QuestionEntity> questionEntity = new ArrayList<QuestionEntity>();
        if (userEntity != null) {
            UserEntity questionUser=userDao.getUserById(userId);
            if(questionUser==null)
            {
                throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
            }
            questionEntity = questionDao.getAllQuestionsByUser(userId);
        }
        return questionEntity;
    }


}

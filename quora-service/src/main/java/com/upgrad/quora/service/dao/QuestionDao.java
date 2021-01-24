package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
//Solves #8 (https://github.com/adarshrya/Trello_quora/issues/8)
@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions() {
        return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
    }

    public List<QuestionEntity> getAllQuestionsByUser(String userId) {
        return entityManager.createNamedQuery("getAllQuestionsByUser", QuestionEntity.class).setParameter("userid", userId).getResultList();
    }

    public QuestionEntity getQuestionById(String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    public QuestionEntity editQuestionContent(QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
}

package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerById(String answerId) {
        try {
            return entityManager.createNamedQuery("getAnswerById", AnswerEntity.class).setParameter("uuid", answerId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity editAnswerContent(AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    public List<AnswerEntity> getAnswerByQuestionId(String questionId) {
        return entityManager.createNamedQuery("getAnswerByQuestionId", AnswerEntity.class).setParameter("questionId", questionId).getResultList();
    }
}

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if (userDao.getUserByUserName(userEntity.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        if (userDao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }

    public UserEntity getUserById(String authorization, String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        UserEntity getUserEntity = new UserEntity();
        if (userEntity != null) {
            getUserEntity = userDao.getUserById(userId);
            if(getUserEntity==null)
            {
                throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
            }
        }
        return getUserEntity;
    }

    public UserEntity deleteUser(String authorization, String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = authenticationService.validateAuthToken(authorization);
        UserEntity userEntityDelete = new UserEntity();
        if (userEntity != null && authenticationService.isAdmin(userEntity)) {
            userEntityDelete = userDao.getUserById(userId);
            if (userEntityDelete == null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
            }
            userDao.deleteUser(userEntityDelete);
        }
        return userEntityDelete;

    }
}

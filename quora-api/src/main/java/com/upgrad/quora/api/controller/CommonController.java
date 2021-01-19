package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@RequestHeader("authorization") final String authorization, @PathVariable("userId") final String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        UserEntity userEntity=userBusinessService.getUserById(authorization,userId);
        userDetailsResponse.setFirstName(userEntity.getFirstName());
        userDetailsResponse.setLastName(userEntity.getLastName());
        userDetailsResponse.setUserName(userEntity.getUserName());
        userDetailsResponse.setEmailAddress(userEntity.getEmail());
        userDetailsResponse.setCountry(userEntity.getCountry());
        userDetailsResponse.setAboutMe(userEntity.getAboutMe());
        userDetailsResponse.setContactNumber(userEntity.getContactNumber());
        userDetailsResponse.setDob(userEntity.getDob());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}

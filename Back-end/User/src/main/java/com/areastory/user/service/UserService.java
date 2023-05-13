package com.areastory.user.service;

import com.areastory.user.dto.request.UserInfoReq;
import com.areastory.user.dto.request.UserReq;
import com.areastory.user.dto.response.ArticleResp;
import com.areastory.user.dto.response.UserDetailResp;
import com.areastory.user.dto.response.UserResp;
import com.areastory.user.dto.response.UserSignUpResp;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {

    void validateUser(Long userId);

    Boolean findUser(Long userId);

    UserResp login(Long providerId, String registrationToken);

    void logout(Long userId);

    UserSignUpResp signUp(UserReq userReq, MultipartFile profile) throws IOException, NoSuchAlgorithmException;

    void updateUserInfo(Long userId, UserInfoReq userInfoReq, MultipartFile profile) throws IOException;
    UserResp getMyDetail(Long userId);

    void updateUserNickName(Long userId, String nickname);

    void deleteUser(Long userId);

    void updateProfile(Long userId, MultipartFile profile) throws IOException;

    UserDetailResp getUserDetail(Long userId, Long myId);

//    List<ArticleResp> getArticleList(Long userId);
        ArticleResp getArticleList(Long userId, int page);
//    List<ArticleResp> getOtherUserArticleList(Long userId);
    ArticleResp getOtherUserArticleList(Long userId, int page);

}

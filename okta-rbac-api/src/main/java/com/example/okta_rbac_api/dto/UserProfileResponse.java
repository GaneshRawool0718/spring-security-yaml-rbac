package com.example.okta_rbac_api.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class UserProfileResponse {

    private String id;
    private String email;
    private String login;

    private String firstName;
    private String lastName;
    private String nickName;
    private String mobilePhone;

    private String idolName;

    private String status;
    private List<String> roles;
}

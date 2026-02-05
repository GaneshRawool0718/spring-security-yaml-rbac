package com.example.okta_rbac_api.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequest {

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 50)
    private String nickName;

    @Size(max = 20)
    private String mobilePhone;

    @Size(max = 50)
    private String idolName;
}

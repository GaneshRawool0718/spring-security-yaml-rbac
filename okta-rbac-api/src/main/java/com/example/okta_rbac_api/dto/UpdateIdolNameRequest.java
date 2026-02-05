package com.example.okta_rbac_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateIdolNameRequest {

    @NotBlank
    @Size(max = 50)
    private String idolName;
}

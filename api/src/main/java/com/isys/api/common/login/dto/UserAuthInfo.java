package com.isys.api.common.login.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UserAuthInfo {
    private String NAME;
    private String PASSWORD;
}

package com.isys.api.common.login.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDTO {
    private String NAME;
    private String PASSWORD;
}

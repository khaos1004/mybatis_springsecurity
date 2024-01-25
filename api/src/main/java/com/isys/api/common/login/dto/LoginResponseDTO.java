package com.isys.api.common.login.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDTO {
    private String NAME;
    private Integer USER_ID;
    private String message;
}

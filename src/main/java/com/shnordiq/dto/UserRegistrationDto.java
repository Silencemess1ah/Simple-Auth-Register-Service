package com.shnordiq.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {

    @NotBlank
    @NotEmpty
    @Size(min = 4, max = 32)
    private String nickname;

    @NotBlank
    @NotEmpty
    @Size(min = 8, max = 32)
    private String password;

    @Email
    @NotEmpty
    @Size(min = 8, max = 128)
    private String email;
    //todo email, phone -> verification code + login variation
}

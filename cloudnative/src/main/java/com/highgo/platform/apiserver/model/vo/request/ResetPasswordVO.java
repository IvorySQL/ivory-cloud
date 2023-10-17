package com.highgo.platform.apiserver.model.vo.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
public class ResetPasswordVO implements Serializable {

    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!%\\\"\\$\\+\\|\\^\\'\\{\\}\\[\\],/:;<=>?_~`]{8,32}$", message = "{param.instance_password.invalid}")
    @NotBlank
    @Pattern(regexp = "^(?=.*[\\!@#\\$%\\^&\\*\\(\\)])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\!@#\\$%\\^&\\*\\(\\)]{8,32}$", message = "{param.instance_password.invalid}")
    private String password;
}

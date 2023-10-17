package com.highgo.platform.apiserver.model.vo.request;

import com.highgo.cloud.enums.LockStatus;
import com.highgo.cloud.enums.UserOption;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseUserVO implements Serializable {

    private String name;

    @NotBlank
    @Pattern(regexp = "^(?=.*[\\!@#\\$%\\^&\\*\\(\\)])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\!@#\\$%\\^&\\*\\(\\)]{8,32}$", message = "{param.instance_password.invalid}")
    @ApiModelProperty(value = "数据库管理员密码", required = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private LockStatus status;

    @Enumerated(EnumType.STRING)
    private UserOption option;
}

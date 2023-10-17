package com.highgo.platform.apiserver.model.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseVO implements Serializable {

    @NotBlank(message = "{dbs.common.param.db_user_name.is_required}")
    @Size(min = 1, max = 63, message = "{dbs.common.param.db_database_name.invalid}")
    private String dbName;
    private String owner;
    private String enCoding;
    private String lcCollate;
    private String lcCtype;

}

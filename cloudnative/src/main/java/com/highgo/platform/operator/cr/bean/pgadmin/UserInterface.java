package com.highgo.platform.operator.cr.bean.pgadmin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lucunqiao
 * @date 2022/12/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInterface {
    /**
     * pgamdin
     */
    private PgAdmin pgAdmin;
}

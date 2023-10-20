/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highgo.cloud.auth.repository;

import com.highgo.cloud.auth.entity.EmailVerificationCode;
import org.springframework.stereotype.Repository;
import com.highgo.cloud.constant.OrderContant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/3/8 10:54
 * @Description: 邮箱验证码数据库操作类
 */
@Repository("verificationCodeRepository")
public interface VerificationCodeRepository
        extends
            JpaRepository<EmailVerificationCode, Integer>,
            JpaSpecificationExecutor<EmailVerificationCode> {

    @Query(value = "select * " +
            "from email_verification_code " +
            "where user_name = :userName " +
            "and create_time >= :beforeTime " +
            "and create_time <= :now " +
            "and is_used = " + OrderContant.RECORD_IS_NOT_DELETE, nativeQuery = true)
    List<EmailVerificationCode> findVerificationCodeByUserName(
            @Param("userName") String userName,
            @Param("now") Timestamp now,
            @Param("beforeTime") Timestamp beforeTime);
}

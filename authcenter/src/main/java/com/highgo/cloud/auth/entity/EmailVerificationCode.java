package com.highgo.cloud.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/3/8 10:10
 * @Description: 邮箱验证码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="email_verification_code")
@NamedQuery(name="EmailVerificationCode.findAll", query="SELECT e FROM EmailVerificationCode e")
public class EmailVerificationCode {

    @Id
    @Column(name="id", unique=true, nullable=false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name="verification_code")
    private String verificationCode;

    @Column(name="create_time")
    private Timestamp createTime;

    @Column(name="user_id")
    private Integer userId;

    @Column(name="user_name")
    private String userName;

    @Column(name="user_email")
    private String userEmail;

    @Column(name="is_used")
    private Integer isUsed;

}

package com.highgo.cloud.auth.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="user_role")
@Setter
@Getter
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id", unique=true, nullable=false)
	private Integer id;

	@Column(name="deleted")
	@ColumnDefault("0")
	private Integer deleted;

	@Column(name="type")
	private Integer type;
	
	@Column(name="name", length=20)
	private String name;

	@Column(name="created_time")
	private Timestamp createdTime;

	@Column(name="updated_time")
	private Timestamp updatedTime;
	
	@Column(name="deleted_time")
	private Timestamp deleted_time;

}
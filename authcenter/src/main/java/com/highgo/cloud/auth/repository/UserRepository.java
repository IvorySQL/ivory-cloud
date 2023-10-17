package com.highgo.cloud.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.highgo.cloud.auth.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User,Integer> {

    /**
     * 通过用户名查找
     */
    User findByName(String username);

}

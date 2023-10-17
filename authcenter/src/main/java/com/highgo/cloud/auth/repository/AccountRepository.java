package com.highgo.cloud.auth.repository;


import com.highgo.cloud.auth.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository("accountRepository")
public interface AccountRepository extends JpaRepository<User,Integer> {

    /**
     * 通过用户名查找
     */
    User findByName(String username);

    @Query(value = "select u from User u where u.deleted = 0 and u.id = ?1 and u.clusterId = ?2")
    List<User> listByUserIdAndClusterId(Integer userId, String clusterId);
}

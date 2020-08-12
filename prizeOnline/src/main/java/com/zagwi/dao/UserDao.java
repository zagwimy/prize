package com.zagwi.dao;


import com.zagwi.domain.User;
import org.apache.ibatis.annotations.Select;

public interface UserDao {
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);
}

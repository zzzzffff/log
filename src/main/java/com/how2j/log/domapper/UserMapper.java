package com.how2j.log.domapper;


import com.how2j.log.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user")
    List<User> findAll();
    @Insert(" insert into user (name,password) values (#{name},#{password}) ")
    public int save(User user);
    @Update("update user set password=#{editPassword} where name=#{name}")
    public int update(User user);
}

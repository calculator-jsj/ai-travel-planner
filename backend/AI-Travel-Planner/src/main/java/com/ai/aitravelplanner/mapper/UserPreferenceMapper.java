package com.ai.aitravelplanner.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserPreferenceMapper {
    // 删除用户旧偏好
    @Delete("DELETE FROM user_preference WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    // 插入新的偏好
    @Insert("INSERT INTO user_preference(user_id, preference, travelstyle) VALUES(#{userId}, #{preference}, #{travelStyle})")
    int insert(@Param("userId") Long userId, @Param("preference") String preference, @Param("travelStyle") String travelStyle);
}

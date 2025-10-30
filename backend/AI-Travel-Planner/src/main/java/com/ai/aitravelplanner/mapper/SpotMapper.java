package com.ai.aitravelplanner.mapper;

import com.ai.aitravelplanner.entity.Spot;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpotMapper {
    @Insert("INSERT INTO spot (plan_id, day, name, lng, lat, type, description, order_index, created_at, updated_at) " +
            "VALUES (#{planId}, #{day}, #{name}, #{lng}, #{lat}, #{type}, #{description}, #{orderIndex}, #{createdAt}, #{updatedAt})")
    void insert(Spot spot);

    @Select("SELECT * FROM spot WHERE plan_id = #{planId} ORDER BY day")
    List<Spot> findByPlanId(Long planId);

    @Delete("DELETE FROM spot WHERE plan_id = #{planId}")
    void deleteByPlanId(Long planId);
}
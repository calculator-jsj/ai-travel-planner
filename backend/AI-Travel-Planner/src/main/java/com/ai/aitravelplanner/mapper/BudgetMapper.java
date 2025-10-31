package com.ai.aitravelplanner.mapper;

import com.ai.aitravelplanner.entity.Budget;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BudgetMapper {
    int insert(Budget budget);

    @Select("SELECT * FROM budget WHERE plan_id = #{planId} ORDER BY expense_date ASC")
    List<Budget> getByPlanId(Long planId);

    @Delete("DELETE FROM budget WHERE id = #{id}")
    int deleteById(Long id);
}

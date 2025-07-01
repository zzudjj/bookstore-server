package com.huang.store.mapper;

import com.huang.store.entity.spike.SpikeActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动Mapper接口
 */
@Mapper
public interface SpikeActivityMapper {
    
    /**
     * 获取当前进行中的活动
     */
    List<SpikeActivity> getActiveActivities();
    
    /**
     * 获取所有活动（按时间排序）
     */
    List<SpikeActivity> getAllActivitiesOrderByTime();
    
    /**
     * 根据ID获取活动
     */
    SpikeActivity getActivityById(@Param("id") Long id);
    
    /**
     * 插入活动
     */
    int insertActivity(SpikeActivity activity);
    
    /**
     * 更新活动
     */
    int updateActivity(SpikeActivity activity);
    
    /**
     * 获取所有活动（分页）
     */
    List<SpikeActivity> getAllActivities(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    /**
     * 获取活动总数
     */
    int getActivityCount();
    
    /**
     * 删除活动
     */
    int deleteActivity(@Param("id") Long id);
    
    /**
     * 更新活动状态
     */
    int updateActivityStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 搜索活动（分页）
     */
    List<SpikeActivity> searchActivities(@Param("offset") int offset,
                                         @Param("pageSize") int pageSize,
                                         @Param("searchParams") Map<String, Object> searchParams);

    /**
     * 获取搜索结果总数
     */
    int getSearchActivityCount(@Param("searchParams") Map<String, Object> searchParams);
}

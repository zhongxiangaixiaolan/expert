package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.common.result.PageResult;
import com.qing.expert.dto.announcement.AnnouncementQueryDTO;
import com.qing.expert.dto.announcement.AnnouncementSaveDTO;
import com.qing.expert.entity.Announcement;

import java.util.List;

/**
 * 通告服务接口
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 分页查询通告列表
     */
    PageResult<Announcement> getAnnouncementPage(AnnouncementQueryDTO queryDTO);

    /**
     * 获取通告详情
     */
    Announcement getAnnouncementDetail(Long announcementId);

    /**
     * 保存通告
     */
    boolean saveAnnouncement(AnnouncementSaveDTO saveDTO);

    /**
     * 删除通告
     */
    boolean deleteAnnouncement(Long announcementId);

    /**
     * 更新通告状态
     */
    boolean updateAnnouncementStatus(Long announcementId, Integer status);

    /**
     * 获取启用的通告列表（按排序权重排序）
     */
    List<Announcement> getEnabledAnnouncements();

    /**
     * 获取滚动显示的通告列表
     */
    List<Announcement> getScrollAnnouncements();

    /**
     * 检查排序权重是否已存在
     */
    boolean checkSortOrderExists(Integer sortOrder, Long excludeId);

    /**
     * 批量更新排序权重
     */
    boolean updateSortOrders(List<Long> announcementIds, List<Integer> sortOrders);
}

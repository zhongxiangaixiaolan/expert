package com.qing.expert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qing.expert.entity.ExpertPhoto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 达人照片服务接口
 */
public interface ExpertPhotoService extends IService<ExpertPhoto> {

    /**
     * 根据达人ID查询照片列表
     * @param expertId 达人ID
     * @return 照片列表
     */
    List<ExpertPhoto> getPhotosByExpertId(Long expertId);

    /**
     * 上传达人照片
     * @param expertId 达人ID
     * @param file 照片文件
     * @param photoTitle 照片标题
     * @param photoDescription 照片描述
     * @return 照片信息
     */
    ExpertPhoto uploadPhoto(Long expertId, MultipartFile file, String photoTitle, String photoDescription);

    /**
     * 删除达人照片
     * @param photoId 照片ID
     * @return 是否成功
     */
    boolean deletePhoto(Long photoId);

    /**
     * 更新照片信息
     * @param photoId 照片ID
     * @param photoTitle 照片标题
     * @param photoDescription 照片描述
     * @return 是否成功
     */
    boolean updatePhotoInfo(Long photoId, String photoTitle, String photoDescription);

    /**
     * 更新照片排序
     * @param photoId 照片ID
     * @param sortOrder 排序顺序
     * @return 是否成功
     */
    boolean updatePhotoSort(Long photoId, Integer sortOrder);

    /**
     * 批量更新照片排序
     * @param photoIds 照片ID列表
     * @return 是否成功
     */
    boolean batchUpdatePhotoSort(List<Long> photoIds);

    /**
     * 根据达人ID删除所有照片
     * @param expertId 达人ID
     * @return 是否成功
     */
    boolean deletePhotosByExpertId(Long expertId);

    /**
     * 获取照片访问URL
     * @param photoName 照片文件名
     * @return 访问URL
     */
    String getPhotoUrl(String photoName);
}

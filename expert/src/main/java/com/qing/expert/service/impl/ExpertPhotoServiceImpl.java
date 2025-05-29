package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.entity.ExpertPhoto;
import com.qing.expert.mapper.ExpertPhotoMapper;
import com.qing.expert.service.ExpertPhotoService;
import com.qing.expert.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * 达人照片服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpertPhotoServiceImpl extends ServiceImpl<ExpertPhotoMapper, ExpertPhoto> implements ExpertPhotoService {

    private final FileUploadUtil fileUploadUtil;

    @Override
    public List<ExpertPhoto> getPhotosByExpertId(Long expertId) {
        if (expertId == null) {
            throw new BusinessException("达人ID不能为空");
        }
        return baseMapper.selectByExpertId(expertId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpertPhoto uploadPhoto(Long expertId, MultipartFile file, String photoTitle, String photoDescription) {
        if (expertId == null) {
            throw new BusinessException("达人ID不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("照片文件不能为空");
        }

        try {
            // 上传文件
            String photoName = fileUploadUtil.uploadPhotoFile(file);

            // 获取图片尺寸
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = 0;
            int height = 0;
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            }

            // 获取下一个排序号
            Integer maxSortOrder = baseMapper.getMaxSortOrder(expertId);
            int nextSortOrder = (maxSortOrder == null ? 0 : maxSortOrder) + 1;

            // 创建照片记录
            ExpertPhoto photo = new ExpertPhoto();
            photo.setExpertId(expertId);
            photo.setPhotoName(photoName);
            photo.setPhotoTitle(StrUtil.isNotBlank(photoTitle) ? photoTitle : file.getOriginalFilename());
            photo.setPhotoDescription(photoDescription);
            photo.setSortOrder(nextSortOrder);
            photo.setFileSize(file.getSize());
            photo.setWidth(width);
            photo.setHeight(height);

            // 保存到数据库
            boolean success = save(photo);
            if (!success) {
                // 如果保存失败，删除已上传的文件
                fileUploadUtil.deletePhotoFile(photoName);
                throw new BusinessException("保存照片信息失败");
            }

            log.info("达人照片上传成功：expertId={}, photoName={}", expertId, photoName);
            return photo;

        } catch (IOException e) {
            log.error("读取图片信息失败", e);
            throw new BusinessException("读取图片信息失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("上传达人照片失败：expertId={}", expertId, e);
            throw new BusinessException("上传照片失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePhoto(Long photoId) {
        if (photoId == null) {
            throw new BusinessException("照片ID不能为空");
        }

        // 查询照片信息
        ExpertPhoto photo = getById(photoId);
        if (photo == null) {
            throw new BusinessException("照片不存在");
        }

        try {
            // 删除数据库记录
            boolean success = removeById(photoId);
            if (success) {
                // 删除文件
                fileUploadUtil.deletePhotoFile(photo.getPhotoName());
                log.info("达人照片删除成功：photoId={}, photoName={}", photoId, photo.getPhotoName());
            }
            return success;

        } catch (Exception e) {
            log.error("删除达人照片失败：photoId={}", photoId, e);
            throw new BusinessException("删除照片失败：" + e.getMessage());
        }
    }

    @Override
    public boolean updatePhotoInfo(Long photoId, String photoTitle, String photoDescription) {
        if (photoId == null) {
            throw new BusinessException("照片ID不能为空");
        }

        ExpertPhoto photo = new ExpertPhoto();
        photo.setId(photoId);
        photo.setPhotoTitle(photoTitle);
        photo.setPhotoDescription(photoDescription);

        return updateById(photo);
    }

    @Override
    public boolean updatePhotoSort(Long photoId, Integer sortOrder) {
        if (photoId == null) {
            throw new BusinessException("照片ID不能为空");
        }
        if (sortOrder == null || sortOrder < 0) {
            throw new BusinessException("排序顺序不能为空且必须大于等于0");
        }

        return baseMapper.updateSortOrder(photoId, sortOrder) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdatePhotoSort(List<Long> photoIds) {
        if (photoIds == null || photoIds.isEmpty()) {
            return true;
        }

        try {
            for (int i = 0; i < photoIds.size(); i++) {
                Long photoId = photoIds.get(i);
                int sortOrder = i + 1;
                baseMapper.updateSortOrder(photoId, sortOrder);
            }
            return true;
        } catch (Exception e) {
            log.error("批量更新照片排序失败", e);
            throw new BusinessException("批量更新照片排序失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePhotosByExpertId(Long expertId) {
        if (expertId == null) {
            throw new BusinessException("达人ID不能为空");
        }

        try {
            // 查询所有照片
            List<ExpertPhoto> photos = getPhotosByExpertId(expertId);

            // 删除数据库记录
            int deletedCount = baseMapper.deleteByExpertId(expertId);

            // 删除文件
            for (ExpertPhoto photo : photos) {
                fileUploadUtil.deletePhotoFile(photo.getPhotoName());
            }

            log.info("删除达人所有照片成功：expertId={}, deletedCount={}", expertId, deletedCount);
            return deletedCount > 0;

        } catch (Exception e) {
            log.error("删除达人所有照片失败：expertId={}", expertId, e);
            throw new BusinessException("删除达人所有照片失败：" + e.getMessage());
        }
    }

    @Override
    public String getPhotoUrl(String photoName) {
        if (StrUtil.isBlank(photoName)) {
            return null;
        }
        // 构建照片访问URL，确保路径正确
        // 注意：由于后端配置了context-path: /api，所以完整路径是 /api/photos/
        return "/api/photos/" + photoName;
    }
}

package com.qing.expert.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis诊断配置
 * 用于检查映射文件是否正确加载，避免循环依赖
 */
@Slf4j
@Configuration
public class MyBatisDiagnostic {

    /**
     * 应用启动后检查MyBatis映射文件
     */
    @Bean
    public ApplicationRunner mybatisMapperChecker(@Autowired SqlSessionFactory sqlSessionFactory) {
        return args -> {
            try {
                var configuration = sqlSessionFactory.getConfiguration();
                var mappedStatements = configuration.getMappedStatements();
                
                log.info("=== MyBatis映射文件加载检查 ===");
                log.info("总共加载的映射语句数量: {}", mappedStatements.size());
                
                // 检查ExpertPhotoMapper相关的映射
                boolean hasSelectByExpertId = mappedStatements.stream()
                    .anyMatch(ms -> ms.getId().equals("com.qing.expert.mapper.ExpertPhotoMapper.selectByExpertId"));
                
                log.info("ExpertPhotoMapper.selectByExpertId 映射是否存在: {}", hasSelectByExpertId);
                
                if (!hasSelectByExpertId) {
                    log.error("❌ ExpertPhotoMapper.selectByExpertId 映射未找到！");
                    // 列出所有ExpertPhotoMapper相关的映射
                    log.info("正在列出所有ExpertPhotoMapper相关的映射:");
                    mappedStatements.stream()
                        .filter(ms -> ms.getId().contains("ExpertPhotoMapper"))
                        .forEach(ms -> log.info("  - {}", ms.getId()));
                        
                    // 列出所有映射文件以供调试
                    log.info("所有已加载的映射语句:");
                    mappedStatements.stream()
                        .limit(20) // 只显示前20个，避免日志过多
                        .forEach(ms -> log.info("  - {}", ms.getId()));
                        
                } else {
                    log.info("✅ ExpertPhotoMapper.selectByExpertId 映射加载成功");
                }
                
            } catch (Exception e) {
                log.error("检查映射文件加载时出错", e);
            }
        };
    }
}

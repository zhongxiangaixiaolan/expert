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

                // 简化MyBatis检查，只在出现问题时输出
                boolean hasSelectByExpertId = mappedStatements.stream()
                        .anyMatch(ms -> ms.getId().equals("com.qing.expert.mapper.ExpertPhotoMapper.selectByExpertId"));

                if (!hasSelectByExpertId) {
                    log.error("❌ ExpertPhotoMapper.selectByExpertId 映射未找到！");
                    log.info("总共加载的映射语句数量: {}", mappedStatements.size());
                }

            } catch (Exception e) {
                log.error("检查映射文件加载时出错", e);
            }
        };
    }
}

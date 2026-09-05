package com.example.jaspersample;

import com.example.fw.common.systemdate.SystemDate;
import com.example.fw.common.systemdate.config.SystemDateConfigPackage;
import com.example.fw.web.aspect.LogAspect;
import com.example.jaspersample.domain.message.MessageIds;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * アプリケーション層の設定クラス
 *
 */
@Configuration
// システム日時機能の追加
@ComponentScan(basePackageClasses = {SystemDateConfigPackage.class})
public class AppConfig {

    /**
     * ロギング機能
     */
    @Bean
    LogAspect logAspect(SystemDate systemDate, MessageSource messageSource) {
        return new LogAspect(systemDate, messageSource, MessageIds.W_EX_2001, MessageIds.E_EX_9001);
    }

}

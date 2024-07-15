package com.example.fw.common.reports;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 帳票作成クラスのBeanを示すアノテーション
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ReportCreator {

}

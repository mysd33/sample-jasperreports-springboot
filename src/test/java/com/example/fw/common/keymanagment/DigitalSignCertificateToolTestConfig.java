package com.example.fw.common.keymanagment;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.keymanagement.config.KeyManagementConfigPackage;
import com.example.fw.common.objectstorage.config.S3ConfigPackage;


@Configuration
@ComponentScan(basePackageClasses = { KeyManagementConfigPackage.class, S3ConfigPackage.class })
public class DigitalSignCertificateToolTestConfig {

}

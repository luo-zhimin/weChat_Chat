package com.wechat.cn.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by IntelliJ IDEA.
 * swagger 配置
 * @Author : 志敏.罗
 * @create 2022/8/17 16:25
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "swagger",name = "enable",havingValue = "true")
public class SwaggerConfig {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()).enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wechat.cn.controller"))
                .build();
    }

    public ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .version("1.0")
                .title("Wechat")
//                .contact(new Contact("admin","url","xxx"))
                .build();
    }
}

package com.social.photo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfigs {
        @Bean
        public Docket swagger () {
            return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.social.photo.controllers"))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(apiEndPointsInfo())
                    .useDefaultResponseMessages(false);
        }

        private ApiInfo apiEndPointsInfo () {
            return new ApiInfoBuilder().title("Photo Service REST API")
                    .description("Photo Management REST API")
                    .contact(new Contact("Laila Nasser", "", "lailanelkoussy@aucegypt.edu"))
                    .build();
        }
    }

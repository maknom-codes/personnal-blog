package com.maknom.blog.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maknom.blog.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BlogAppConfiguration implements WebMvcConfigurer {

   @Bean
   public ServerStartupLogger serverStartupLogger() {
      return new ServerStartupLogger();
   }


   @Bean
   public ViewInterceptor viewInterceptor() {
      return new ViewInterceptor();
   }


   @Bean
   public ObjectMapper objectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      return objectMapper;
   }


   @Bean
   public FileServiceImpl fileServiceImpl(@Value("${spring.application.storage.path}") String storageFilePath,
                                          @Value("${spring.application.storage.files.pattern:*.json}") String filePattern,
                                          ObjectMapper objectMapper) {
      return new FileServiceImpl(storageFilePath, filePattern, objectMapper);
   }

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      ViewInterceptor viewInterceptor = viewInterceptor();
      registry.addInterceptor(viewInterceptor)
              .addPathPatterns("/**")
              .excludePathPatterns( "/css/**");
   }
}

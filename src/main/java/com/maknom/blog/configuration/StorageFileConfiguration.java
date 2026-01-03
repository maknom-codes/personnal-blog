package com.maknom.blog.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class StorageFileConfiguration implements ApplicationRunner {

   private static final Logger log = LoggerFactory.getLogger(StorageFileConfiguration.class);


   private String storageFilePath;

   public StorageFileConfiguration(@Value("${spring.application.storage.path}") String storageFilePath) {
      this.storageFilePath = storageFilePath;
   }



   @Override
   public void run(ApplicationArguments args) {

      Path path = Paths.get(storageFilePath);
      if (!Files.exists(path)) {
         try {
            Files.createDirectories(path);
            log.info("DIRECTORY {} CREATED", path.toAbsolutePath());
         } catch (IOException e) {
            log.error("CANNOT CREATE THE DIRECTORY cause: {}", e.getMessage());
         }
      }
   }
}

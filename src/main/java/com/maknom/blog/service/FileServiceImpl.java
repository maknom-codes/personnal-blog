package com.maknom.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maknom.blog.model.ArticleRequest;
import com.maknom.blog.model.Article;
import com.maknom.blog.model.FileService;
import com.maknom.blog.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileServiceImpl implements FileService {

   private static final String ADMIN_USER_NAME = "admin";
   private static final String ADMIN_USER_PASSWORD = "admin";
   private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

   private final String storageFilePath;

   private final String filePattern;

   private final ObjectMapper objectMapper;

   public FileServiceImpl(String storageFilePath,
                          String filePattern,
                          ObjectMapper objectMapper) {
      this.storageFilePath = storageFilePath;
      this.filePattern = filePattern;
      this.objectMapper = objectMapper;
   }

   private static String idGenerator (String randomString) {
      LocalDateTime now = LocalDateTime.now();
      String date = now.format(DateTimeFormatter.ofPattern("yyyMMdd"));
      return randomString + date;
   }

   @Override
   public User login(String username, String password) {
      if ((username.equals(ADMIN_USER_NAME)
            && username.equals(ADMIN_USER_PASSWORD))) {
         return new User(username,
                 password);
      }
      return null;
   }

   @Override
   public Article create(ArticleRequest articleRequest) {
      Article article = new Article();
      article.setId(idGenerator(UUID.randomUUID().toString().substring(0, 8)));
      log.info("New article ID: {}", article.getId());

      article.setTitle(articleRequest.getTitle());
      article.setPublishDate(articleRequest.getPublishingDate().toString());
      article.setContent(articleRequest.getContent());

      try {
         String fileName = article.getId() + ".json";
         Path path = Paths.get(storageFilePath);
         Path filePath = path.resolve(fileName);
         objectMapper.writeValue(filePath.toFile(), article);
         return article;
      } catch (Exception e) {
         log.error("Erreur: {}", e.getMessage());
      }
      return null;
   }

   @Override
   public Article edit(String articleId, ArticleRequest articleRequest) {
      Article article = new Article();
      article.setId(articleId);
      article.setTitle(articleRequest.getTitle());
      article.setPublishDate(articleRequest.getPublishingDate().toString());
      article.setContent(articleRequest.getContent());
      String filename = articleId + ".json";
      Path path = Paths.get(storageFilePath);
      Path filePath = path.resolve(filename);
      try {
         objectMapper.writeValue(filePath.toFile(), article);
         return article;
      } catch (Exception e) {
         log.error("Erreur: {}", e.getMessage());
      }
      return null;
   }

   @Override
   public Article getArticle(String articleId) {
      List<Article> articles = getAllArticles();
      Optional<Article> optionalArticle = articles.stream()
              .filter(art -> art.getId().equalsIgnoreCase(articleId))
              .findFirst();
      return optionalArticle.isPresent() ? optionalArticle.get() : null;
   }

   @Override
   public List<Article> getAllArticles() {
      List<Article> articles = new ArrayList<>();
      List<Path> filePaths = getFilePaths();
      if(filePaths.isEmpty()) {
         log.warn("Files not found");
      } else {
         for(Path path: filePaths) {
            Resource resource = new FileSystemResource(path);
            Article article = readFile(resource, objectMapper);
            if (article != null) {
               articles.add(article);
               log.info("Nouvel article {}", article);
            }
         }
      }

      return articles;
   }

   @Override
   public void delete(String articleId) {
      String fileName = articleId + ".json";
      Path pathDirectory = Paths.get(storageFilePath);
      Path filePath = pathDirectory.resolve(fileName);
      try  {
         Files.delete(filePath);
         log.info("File {} has been deleted successfully!", fileName);
      } catch (Exception e) {
         log.error("File {} deletion failed!", fileName);
      }
   }

   private List<Path> getFilePaths() {
      Path filePaths = Paths.get(storageFilePath);
      log.info("Recherche des articles dans : {}", filePaths.getFileName());
      try (Stream<Path> stream = Files.list(filePaths)) {
         return   stream
                 .filter(Files::isRegularFile)
                 .collect(Collectors.toList());

      } catch (IOException e) {
         log.error("Erreur: {}", e.getMessage());
      }
      return new ArrayList<>();
   }

   private Article readFile(Resource resource, ObjectMapper objectMapper) {
      try {
         InputStream inputStream = resource.getInputStream();
         return objectMapper.readValue(inputStream, Article.class);
      } catch (IOException e) {
         log.error("Erreur de lecture de fichier {}: {}", resource.getFilename(), e.getMessage());
      }
      return null;
   }
}

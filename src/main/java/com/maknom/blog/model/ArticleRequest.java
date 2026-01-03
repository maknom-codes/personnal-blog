package com.maknom.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ArticleRequest {

   private String id;

   @NotBlank(message = "Title is required!")
   @Size(max = 100, message = "The title must not pass 100 characters")
   private String title;

   @NotNull(message = "Publication date is required")
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private LocalDate publishingDate;

   @NotBlank(message = "Content is required!")
   @Size(max = 3000, message = "The content must not pass 3000 characters")
   private String content;


   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public LocalDate getPublishingDate() {
      return publishingDate;
   }

   public void setPublishingDate(LocalDate publishingDate) {
      this.publishingDate = publishingDate;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }
}

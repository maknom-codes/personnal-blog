package com.maknom.blog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Article {

   @JsonProperty(value = "id")
   private String id;
   @JsonProperty(value = "title")
   private String title;
   @JsonProperty(value = "content")
   private String content;
   @JsonProperty(value = "publish_date")
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private String publishDate;


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

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getPublishDate() {
      return publishDate;
   }

   public void setPublishDate(String publishDate) {
      this.publishDate = publishDate;
   }
}

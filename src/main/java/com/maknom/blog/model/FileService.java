package com.maknom.blog.model;

import java.util.List;

public interface FileService {

   User login(String username, String password);

   Article create(ArticleRequest articleRequest);

   Article edit(String articleId, ArticleRequest articleRequest);

   Article getArticle(String articleId);

   List<Article> getAllArticles();

   void delete(String articleId);

}

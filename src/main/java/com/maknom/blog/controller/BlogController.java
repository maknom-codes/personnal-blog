package com.maknom.blog.controller;

import com.maknom.blog.model.Article;
import com.maknom.blog.model.ArticleRequest;
import com.maknom.blog.model.FileService;
import com.maknom.blog.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Controller
public class BlogController {

   private final FileService fileService;

   public BlogController(FileService fileService) {
      this.fileService = fileService;
   }


   @GetMapping("/home")
   public String getBlog(Model model){
      List<Article> articles = fileService.getAllArticles();
      model.addAttribute("articles", articles);
      model.addAttribute("titre", "Personal Blog");
      return "index";
   }

   @GetMapping("/admin")
   public String getBlogAdmin(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model){

      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Basic")) {
         response.setStatus(HttpStatus.UNAUTHORIZED.value());
         response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"protected zone\"");
         model.addAttribute("titre", "Personal Blog Admin");
         model.addAttribute("statusCode", response.getStatus());
         return "admin";
      }

      String[] credentials = decodeBasicAuth(authHeader);
      User user = fileService.login(credentials[0], credentials[1]);
      if (user == null) {
         response.setStatus(HttpStatus.UNAUTHORIZED.value());
         response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"protected zone\"");
         model.addAttribute("titre", "Personal Blog Admin");
         model.addAttribute("statusCode", response.getStatus());
         return "admin";
      }
      response.setStatus(HttpStatus.OK.value());
      List<Article> articles = fileService.getAllArticles();
      model.addAttribute("articles", articles);
      model.addAttribute("titre", "Personal Blog Admin");
      model.addAttribute("statusCode", response.getStatus());
      return "admin";
   }

   @PutMapping("/blogs/{articleId}")
   public String editBlogAdmin(@PathVariable(name = "articleId") String articleId,
                               @Valid @ModelAttribute("article") ArticleRequest articleRequest,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               HttpServletResponse response){


      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Basic")) {
         response.setStatus(HttpStatus.UNAUTHORIZED.value());
         response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"protected zone\"");
         return "redirect:/admin";
      }

      if (bindingResult.hasErrors()) {
         return "edit";
      }
      Article article = fileService.edit(articleId, articleRequest);
      if (article != null) {
         redirectAttributes.addFlashAttribute("successMessage", "Article '" + article.getTitle() + "' edited successfully!");
      }
      return "redirect:/admin";
   }

   @GetMapping("/blogs/{articleId}")
   public String getBlog(@PathVariable(name = "articleId") String articleId, Model model){
      Article article = fileService.getArticle(articleId);
      model.addAttribute("article", article);
      return "article";
   }


   @GetMapping("/edit/{articleId}")
   public String editForm(@PathVariable(name = "articleId") String articleId,
                          Model model, HttpServletRequest request){

      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Basic")) {
         return "redirect:/admin";
      }
      Article article = fileService.getArticle(articleId);
      ArticleRequest articleRequest = new ArticleRequest();
      articleRequest.setContent(article.getContent());
      articleRequest.setPublishingDate(LocalDate.parse(article.getPublishDate()));
      articleRequest.setTitle(article.getTitle());
      articleRequest.setId(articleId);
      model.addAttribute("article", articleRequest);
      model.addAttribute("title", "Edit Article");
      return "edit";
   }

   @GetMapping("/new")
   public String getForm(HttpServletRequest request, Model model){
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Basic")) {
         return "redirect:/admin";
      }
      ArticleRequest articleRequest = new ArticleRequest();
      articleRequest.setId(null);
      articleRequest.setPublishingDate(LocalDate.now());
      model.addAttribute("article", articleRequest);
      model.addAttribute("title", "Add Article");
      return "add";
   }

   @PostMapping("/blogs/add")
   public String create(@Valid @ModelAttribute("article") ArticleRequest articleRequest,
                        BindingResult bindingResult, RedirectAttributes redirectAttributes,
                        HttpServletRequest request){

      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Basic")) {
         return "redirect:/admin";
      }

      if (bindingResult.hasErrors()) {
         return "add";
      }
      Article article = fileService.create(articleRequest);
      if (article != null) {
         redirectAttributes.addFlashAttribute("successMessage", "Article '" + article.getTitle() + "' created successfully!");
      }
      return "redirect:/admin";
   }

   @DeleteMapping("/blogs/{articleId}")
   public String deleteForm(@PathVariable(name = "articleId") String articleId, HttpServletRequest request){
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Basic")) {
         return "redirect:/admin";
      }
      fileService.delete(articleId);
      return "redirect:/admin";
   }

   private String[] decodeBasicAuth(String authHeader) {
      String base64 = authHeader.substring(6);
      String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
      return decoded.split(":", 2);
   }

}

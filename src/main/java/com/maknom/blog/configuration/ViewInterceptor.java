package com.maknom.blog.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;


public class ViewInterceptor implements HandlerInterceptor {

   private static final Logger log = LoggerFactory.getLogger(ViewInterceptor.class);

   @Override
   public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) throws Exception {
//      HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
      int status = response.getStatus();
      String method = request.getMethod();
      if (ex != null) {
         log.error("ERREUR LORS DU RENDU {} - STATUT: {} - NATURE: {}", method, status, ex.getMessage());
      } else {
         log.info("RENDU COMPLET {} - STATUT: {}", method, status);
      }
   }
}

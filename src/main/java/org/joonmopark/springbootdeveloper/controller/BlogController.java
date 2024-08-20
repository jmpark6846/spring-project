package org.joonmopark.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.joonmopark.springbootdeveloper.domain.Article;
import org.joonmopark.springbootdeveloper.dto.AddArticleRequest;
import org.joonmopark.springbootdeveloper.dto.AddArticleResponse;
import org.joonmopark.springbootdeveloper.dto.ArticleResponse;
import org.joonmopark.springbootdeveloper.dto.UpdateArticleRequest;
import org.joonmopark.springbootdeveloper.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/api/articles")
    public ResponseEntity<ArticleResponse> addArticle(@RequestBody AddArticleRequest addArticleRequest, Principal principal){
        Article article = blogService.save(addArticleRequest, principal.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ArticleResponse(article));
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> getArticles(){
        List<ArticleResponse> articleResponses = blogService.getArticles().stream().map(ArticleResponse::new).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(articleResponses);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable long id){
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<String> delete(@PathVariable long id){
        blogService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> update(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        Article article = blogService.update(id,request);
        return ResponseEntity.ok().body(new ArticleResponse(article));
    }
}

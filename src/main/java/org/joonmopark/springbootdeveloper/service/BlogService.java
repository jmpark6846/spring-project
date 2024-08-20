package org.joonmopark.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.joonmopark.springbootdeveloper.domain.Article;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.dto.AddArticleRequest;
import org.joonmopark.springbootdeveloper.dto.UpdateArticleRequest;
import org.joonmopark.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String username){
        return blogRepository.save(request.toEntity(username));
    }
    public List<Article> getArticles() {
        return blogRepository.findAll();
    }

    public Article findById(Long id){
        return blogRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("not found: "+ id));
    }

    public void deleteById(long id){
        blogRepository.deleteById(id);
    }

    public void delete(long id){
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: "+ id));

        authorizeAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request){
        Article article = findById(id);
        article.update(request.getTitle(), request.getContent());

        authorizeAuthor(article);
        return article;
    }

    public void authorizeAuthor(Article article){
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if(!article.getAuthor().equals(username)){
            throw new IllegalArgumentException("not authorized");
        }
    }
}

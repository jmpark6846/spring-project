package org.joonmopark.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.joonmopark.springbootdeveloper.domain.Article;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.dto.AddArticleRequest;
import org.joonmopark.springbootdeveloper.dto.UpdateArticleRequest;
import org.joonmopark.springbootdeveloper.repository.BlogRepository;
import org.joonmopark.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void mockMvcSetup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    public void setSecurityContext(){
        userRepository.deleteAll();
        user = userRepository.save(User.builder().email("test@test.com").password("password").build());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @Test
    void addArticle() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "내용";
        final String author = "user1";
        final AddArticleRequest addArticleRequest = new AddArticleRequest(title, content, author);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addArticleRequest))
                .principal(principal));

        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();
        Assertions.assertThat(articles.get(0).getTitle()).isEqualTo(title);
    }

    @DisplayName("getArticles: 블로그 글들 모두 조회한다.")
//    @Sql("/test-insert-data.sql")
    @Test
    void getArticles() throws Exception {
        //given
        final String url = "/api/articles";
        Article article = createDefaultArticle();

        //when
        ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(article.getTitle()))
                .andExpect(jsonPath("$[0].content").value(article.getContent()));
    }

    @DisplayName("getArticle: Id로 특정 게시물을 조회한다.")
    @Test
    void getArticle() throws Exception {
        final String url = "/api/articles/{id}";
        Article article = createDefaultArticle();

        ResultActions resultActions = mockMvc.perform(get(url, article.getId()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(article.getContent()));
    }

    @DisplayName("delete: Id로 게시물을 삭제한다.")
    @Test
    void delete() throws Exception {
        final String url = "/api/articles/{id}";
        Article article = createDefaultArticle();
        blogRepository.save(article);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url, article.getId()));

        resultActions.andExpect(status().isOk());
        Assertions.assertThat(blogRepository.findAll()).isEmpty();
    }

    @Test
    void update() throws Exception {
        final String url = "/api/articles/{id}";
        Article article = createDefaultArticle();
        blogRepository.save(article);
        UpdateArticleRequest request = new UpdateArticleRequest("updated title", "updated content");

        ResultActions resultActions = mockMvc.perform(put(url, article.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("updated title"));

        Article updatedArticle = blogRepository.findById(article.getId()).get();
        Assertions.assertThat(updatedArticle.getTitle()).isEqualTo("updated title");
    }

    private Article createDefaultArticle(){
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());

    }
}
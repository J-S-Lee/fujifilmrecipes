package com.fujifilmrecipes.fujifilmrecipes.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujifilmrecipes.fujifilmrecipes.domain.Post;
import com.fujifilmrecipes.fujifilmrecipes.repository.PostRepository;
import com.fujifilmrecipes.fujifilmrecipes.request.PostCreate;
import com.fujifilmrecipes.fujifilmrecipes.request.PostEdit;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.IntStream;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Transactional
class PostControllerTest {

    private static final String identifier = "{class-name}/{method-name}";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("/posts 요청시 {}를 출력한다")
    void test() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("글 내용입니다")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title 값은 필수다")
    void test2() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .content("글 내용입니다")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(print());
        //TODO jsonPath에 대해서 공부해보기
    }

    @Test
    @DisplayName("/posts 요청시 db에 값이 저장된다")
    void test3() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("글 내용입니다")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions perform = mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json));

        //then
        perform.andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, postRepository.count());

        Post post = postRepository.findAll().getFirst();
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("글 내용입니다", post.getContent());

        //docs
        perform.andDo(
                MockMvcRestDocumentationWrapper.document(
                        identifier,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Post API")
                                        .summary("포스트 등록 API")
                                        .requestFields(
                                                fieldWithPath("title").description("제목"),
                                                fieldWithPath("content").description("내용")
                                        )
                                        .build()
                        )
                )
        );

    }

    @Test
    @DisplayName("글 1개 조회")
    void getPostById() throws Exception {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        //when
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("글 여러개 조회")
    void getList() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        postRepository.saveAll(requestPosts);

        //when
        mockMvc.perform(get("/posts?page=1&sort=id,desc&size=5")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$[0].id").value(requestPosts.getLast().getId()))
                .andExpect(jsonPath("$[0].title").value("제목 30"))
                .andExpect(jsonPath("$[0].content").value("내용 30"))
                .andDo(print());

    }

    @Test
    @DisplayName("글 수정")
    void postUpdate() throws Exception {
        Post post = Post.builder().title("제목").content("내용").build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("타이틀")
                .content("내용")
                .build();

        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 삭제")
    void postDelete() throws Exception {
        Post post = Post.builder().title("제목").content("내용").build();
        postRepository.save(post);

        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void postGetNotFound() throws Exception {
        //expected
        mockMvc.perform(get("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void postEditNotFound() throws Exception {
        PostEdit postEdit = PostEdit.builder()
                .title("title")
                .content("content")
                .build();

        //expected
        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 등록 잘못된 요청")
    void postBadRequest() throws Exception {
        PostCreate post = PostCreate.builder()
                .title("나는 바보")
                .content("내용")
                .build();

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

}
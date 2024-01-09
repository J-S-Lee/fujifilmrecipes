package com.fujifilmrecipes.fujifilmrecipes.service;

import com.fujifilmrecipes.fujifilmrecipes.domain.Post;
import com.fujifilmrecipes.fujifilmrecipes.exception.PostNotFound;
import com.fujifilmrecipes.fujifilmrecipes.repository.PostRepository;
import com.fujifilmrecipes.fujifilmrecipes.request.PostCreate;
import com.fujifilmrecipes.fujifilmrecipes.request.PostEdit;
import com.fujifilmrecipes.fujifilmrecipes.request.PostSearch;
import com.fujifilmrecipes.fujifilmrecipes.response.PostResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("글 작성")
    void testWrite() {
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        postService.write(postCreate);

        Assertions.assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().getFirst();
        assertEquals("제목입니다", post.getTitle());
        assertEquals("내용입니다", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void getPostById() {
        //given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        //when
        PostResponse post = postService.get(requestPost.getId());

        //then
        assertNotNull(post);
        Post first = postRepository.findAll().getFirst();
        assertEquals(requestPost.getTitle(), post.getTitle());
        assertEquals(post.getTitle(), first.getTitle());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void getPostList() {
        //given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                .title("제목 " + i)
                .content("내용 " + i)
                .build())
                .toList();

        postRepository.saveAll(requestPosts);

//        PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        PostSearch postSearch = PostSearch.builder().build();


        //when
        List<PostResponse> posts = postService.getList(postSearch);
        for (PostResponse post : posts) {
            System.out.println("post.getTitle() = " + post.getTitle());
        }

        //then
        assertEquals(10L, posts.size());
        assertEquals("제목 30", posts.get(0).getTitle());
        assertEquals("제목 26", posts.get(4).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void editTitle() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("타이틀")
                .build();

        postService.edit(post.getId(), postEdit);

        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertEquals("타이틀", changedPost.getTitle());
    }

    @Test
    @DisplayName("글 삭제")
    void deletePost() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();

        postRepository.save(post);

        postService.delete(post.getId());

        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 실패 - 존재하지 않는 글")
    void getPostByIdFail() {
        //given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        //expected
        assertThrows(PostNotFound.class, () -> {
            postService.get(requestPost.getId() + 1L);
        });

    }

    @Test
    @DisplayName("글 제목 수정 실패 - 존재하지 않는 글")
    void editTitleFail() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("타이틀")
                .build();

        assertThrows(PostNotFound.class, () -> {
            postService.edit(post.getId() + 1L, postEdit);
        });

    }

    @Test
    @DisplayName("글 삭제 실패 - 존재하지 않는 글")
    void deletePostFail() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();

        postRepository.save(post);

        assertThrows(PostNotFound.class, () -> {
            postService.delete(post.getId() + 1L);
        });
    }
}
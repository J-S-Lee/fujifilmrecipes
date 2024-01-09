package com.fujifilmrecipes.fujifilmrecipes.service;

import com.fujifilmrecipes.fujifilmrecipes.domain.Post;
import com.fujifilmrecipes.fujifilmrecipes.domain.PostEditor;
import com.fujifilmrecipes.fujifilmrecipes.exception.PostNotFound;
import com.fujifilmrecipes.fujifilmrecipes.repository.PostRepository;
import com.fujifilmrecipes.fujifilmrecipes.request.PostCreate;
import com.fujifilmrecipes.fujifilmrecipes.request.PostEdit;
import com.fujifilmrecipes.fujifilmrecipes.request.PostSearch;
import com.fujifilmrecipes.fujifilmrecipes.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void write(PostCreate postCreate) {
        // postCreate -> Entity
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();
        postRepository.save(post);
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    public PostResponse edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);


        PostEditor postEditor = post.toEditor()
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);

        return new PostResponse(post);

    }

    public void delete(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        postRepository.deleteById(postId);
    }
}

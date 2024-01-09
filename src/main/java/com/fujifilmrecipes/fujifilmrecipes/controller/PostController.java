package com.fujifilmrecipes.fujifilmrecipes.controller;

import com.fujifilmrecipes.fujifilmrecipes.request.PostCreate;
import com.fujifilmrecipes.fujifilmrecipes.request.PostEdit;
import com.fujifilmrecipes.fujifilmrecipes.request.PostSearch;
import com.fujifilmrecipes.fujifilmrecipes.response.PostResponse;
import com.fujifilmrecipes.fujifilmrecipes.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public Map<String, String> post(@RequestBody @Valid PostCreate post) {
        post.validate();
        postService.write(post);
        return Map.of();
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public PostResponse edit(@PathVariable Long postId, @RequestBody @Valid PostEdit postEdit) {
        return postService.edit(postId, postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }
}

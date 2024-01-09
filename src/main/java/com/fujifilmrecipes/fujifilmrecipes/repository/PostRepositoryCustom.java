package com.fujifilmrecipes.fujifilmrecipes.repository;

import com.fujifilmrecipes.fujifilmrecipes.domain.Post;
import com.fujifilmrecipes.fujifilmrecipes.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}

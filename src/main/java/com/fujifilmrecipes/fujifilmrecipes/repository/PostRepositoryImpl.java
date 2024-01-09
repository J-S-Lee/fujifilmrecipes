package com.fujifilmrecipes.fujifilmrecipes.repository;

import com.fujifilmrecipes.fujifilmrecipes.domain.Post;
import com.fujifilmrecipes.fujifilmrecipes.domain.QPost;
import com.fujifilmrecipes.fujifilmrecipes.request.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.fujifilmrecipes.fujifilmrecipes.domain.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset())
                .orderBy(post.id.desc())
                .fetch();
    }
}

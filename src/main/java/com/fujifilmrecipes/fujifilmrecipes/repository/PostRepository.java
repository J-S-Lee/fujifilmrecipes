package com.fujifilmrecipes.fujifilmrecipes.repository;

import com.fujifilmrecipes.fujifilmrecipes.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}

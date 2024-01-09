package com.fujifilmrecipes.fujifilmrecipes.request;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.*;

@Builder
@Getter
@Setter
public class PostSearch {

    private static final int MAX_SIZE = 2000;

    @Default
    private Integer page = 1;

    @Default
    private Integer size = 10;

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}

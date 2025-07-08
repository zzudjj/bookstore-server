package com.huang.store.entity.dto;

import com.huang.store.entity.book.BookSort;
import java.util.List;

/**
 * @description: 分类集合
 */
public class SortResponse {
    private BookSort upperSort;//父级的分类名
    private List<BookSort> children;//分类的子集


    public BookSort getUpperSort() {
        return upperSort;
    }

    public void setUpperSort(BookSort upperSort) {
        this.upperSort = upperSort;
    }

    public List<BookSort> getChildren() {
        return children;
    }

    public void setChildren(List<BookSort> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "SortResponse{" +
                "upperSort=" + upperSort +
                ", children=" + children +
                '}';
    }
}

package com.huang.store.entity.book;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * 图书实体类
 *
 * @author: 黄龙
 * @date: 2020/7/22 21:44
 * @description: 图书实体类
 */
@Data
public class Book {
    private int id;
    private String bookName;
    private String author;
    private String isbn;
    private String publish;//出版社
    private Timestamp birthday;//出版时间
    private double marketPrice;//书的原价
    private double price;//书的售价
    private Integer stock;//库存
    private String description;//书的概述，在详情页展示
    private List<String> ImgSrc;//书的图片地址
    private boolean put;//是否上架
    private String coverImg;//书的封面图
    private int rank;//权重值
    private boolean newProduct;
    private boolean recommend;
    private int bookSort[];

    /**
     * 保持向后兼容性的方法
     * 原代码中使用的是getisbn()而不是标准的getIsbn()
     */
    public String getisbn() {
        return this.isbn;
    }

    /**
     * 保持向后兼容性的方法
     */
    public void setisbn(String isbn) {
        this.isbn = isbn;
    }
}



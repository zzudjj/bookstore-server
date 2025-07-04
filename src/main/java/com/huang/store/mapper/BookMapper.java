package com.huang.store.mapper;

import com.huang.store.entity.book.*;
import com.huang.store.entity.dto.OrderBookDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookMapper {
    //对于book表的操作
    int addBook(Book book);
    int modifyBook(Book book);
    int modifyBookPut(int id,boolean put);//修改图书是否为上下架
    int modifyBookRec(int id,boolean recommend);//修改图书是否为推荐图书
    int modifyBookNewPro(int id,boolean newProduct);//修改图书是否为新品
    int modifyBookStock(int id,int stockNum);//减库存stockNum
    int restoreBookStock(@Param("id") int id, @Param("stockNum") int stockNum);//恢复库存stockNum
    int deleteBook(int id);//删除图书
    List<Book> getBooks();//得到所有图书
    List<Book> getBooksByPage(int page,int pageSize);//按页得到图书集合
    List<Book> getNewPutBookList(int page,int pageSize);//按页得到新上架的图书集合

    List<OrderBookDto> getBatchBookList(@Param("ids") int[] ids, @Param("account") String account);//根据ids数组和用户账号，得到对应的图书集合
    List<OrderBookDto> getOneBookList(int[] ids);//根据ids数组，得到对应的图书集合

    int getBookId(String isbn);
    String getBookIsbn(int id);
    Book getBook(int id);//获取图书的所有
    Book getBookByIsbn(String isbn);//根据ISBN获取图书信息
    Book getBookDetail(String isbn);//获得图书用于后台页表展示的信息(除去图书详情页和部分图书相册)
    int getPublishBookNum(String publishName);//得到某一出版社的图书的数量
    int getBookCount();//得到图书的数量
    List<Book> getPublishBooks(String publishName);//得到某一个出版社的所有图书

    // 图书搜索相关方法
    List<Book> searchBooks(String keyword, int page, int pageSize);//根据关键字搜索图书（书名、作者、ISBN、出版社）
    int getSearchBookCount(String keyword);//获取搜索结果总数

    // 统计相关方法
    int count();//获取图书总数
    int getTotalStock();//获取总库存
    int getLowStockCount(@Param("threshold") int threshold);//获取低库存商品数量
    List<Book> getLowStockBooks(@Param("threshold") int threshold, @Param("page") int page, @Param("pageSize") int pageSize);//获取低库存商品列表



    //对于bookimg表的操作
    int addBookImg(BookImg bookImg);
    int deleteBookImgOfOne(String isbn);//删除某本书的全部图片
    int deleteOneBookImg(String isbn,String imgSrc);
    String getBookCover(String isbn);//得到书的封面图
    int modifyBookImgList(String oldIsbn,String newIsbn);//当某本图书的isbn好改变了，需要修改与该图书相关联的相册isbn号
    List<String> getBookImgSrcList(String isbn);//按isbn号得到某本图书的所有图书集合

    //对于推荐图书的操作
    int addToRecommend(Recommend recommend);
    int deleteFromRecommend(int bookId);
    int modifyRecommendRank(int bookId,int rank);
    int modifyRecommend(Recommend recommend);
    int hasExistInRec(int bookId);
    List<Book> getRecommendsByPage(@Param("page") int page, @Param("pageSize") int pageSize);

    //对于新品推荐的操作
    int addToNewProduct(Recommend newProduct);
    int deleteFromNewProduct(int bookId);
    int modifyNewProductRank(int bookId,int rank);
    int modifyNewProduct(Recommend newProduct);
    int hasExistInNew(int bookId);
    List<Book> getNewProductsByPage(@Param("page") int page, @Param("pageSize") int pageSize);


    //添加图书到分类
    int addBookToSort(int bookSortId ,int bookId);
    int delBookFromSort(int booSortId,int bookId);
    int modifyBookSort(int bookSortId,int bookId);
    BookSort getBookSort(int bookId);//得到某本书的分类名
    List<Book> getBooksByFirst(List<Integer> list, int page, int pageSize);//得到级别一的所有分类书籍
    List<Book> getBookBySecond(int bookSortId,int page,int pageSize);//得到级别二的所有分类图书
    int getFirstBookCount(List<Integer> list);//得到一级分类图书的数量
    int getSecondBookCount(int bookSortId);//得到二级分类图书的数量
}


package com.huang.store.entity.dto;

import com.huang.store.entity.order.Expense;
import com.huang.store.entity.user.Address;

import java.util.List;

/**
 * @author: 黄龙
 * @date: 2020/7/22 21:09
 * @description: 初始化订单的交互类
 */
public class OrderInitDto {
    private String account;//用户账号
    private List<OrderBookDto> bookList;
    private List<Address> addressList;//返回给前端显示的某个用户的地址
    private Expense expense;
    private Address address;
    private String couponCode;//使用的优惠券码
    private boolean isSpikeOrder;//是否为秒杀订单

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<OrderBookDto> getBookList() {
        return bookList;
    }

    public void setBookList(List<OrderBookDto> bookList) {
        this.bookList = bookList;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public boolean isSpikeOrder() {
        return isSpikeOrder;
    }

    public void setSpikeOrder(boolean spikeOrder) {
        isSpikeOrder = spikeOrder;
    }

    @Override
    public String toString() {
        return "OrderInitDto{" +
                "account='" + account + '\'' +
                ", bookList=" + bookList +
                ", addressList=" + addressList +
                ", expense=" + expense +
                ", address=" + address +
                ", couponCode='" + couponCode + '\'' +
                ", isSpikeOrder=" + isSpikeOrder +
                '}';
    }
}

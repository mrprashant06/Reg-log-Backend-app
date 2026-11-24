package com.ecommerce.reglog.dto.request;

import com.ecommerce.reglog.enums.OrderStatus;
import com.ecommerce.reglog.enums.PaymentMethod;

public class OrderFilterRequest {

    private String query; // Order ID, Product Name, Brand
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private String startDate; // yyyy-MM-dd
    private String endDate;   // yyyy-MM-dd
    private String sortBy = "newest"; // "newest", "oldest", "price_high", "price_low"
    private int page = 0;
    private int size = 10;

    public OrderFilterRequest() {}

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page < 0 ? 0 : page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = (size <= 0 || size > 100) ? 10 : size;
    }
}

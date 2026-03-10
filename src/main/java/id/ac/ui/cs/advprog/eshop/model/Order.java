package id.ac.ui.cs.advprog.eshop.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Order {
    private String id;
    private List<Product> products;
    private Long orderTime;
    private String author;
    private String status;

    public Order(String id, List<Product> products, Long orderTime, String author) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products must not be empty");
        }
        this.id = id;
        this.products = products;
        this.orderTime = orderTime;
        this.author = author;
        this.status = "WAITING_PAYMENT";
    }

    public Order(String id, List<Product> products, Long orderTime, String author, String status) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products must not be empty");
        }
        this.id = id;
        this.products = products;
        this.orderTime = orderTime;
        this.author = author;
        setStatus(status);
    }

    public final void setStatus(String status) {
        if (!"WAITING_PAYMENT".equals(status) && !"SUCCESS".equals(status) && !"CANCELLED".equals(status) && !"FAILED".equals(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
        this.status = status;
    }
}

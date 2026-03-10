package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;

import java.util.List;

public interface OrderService {
    Order create(Order order);
    Order update(Order order);
    Order findById(String id);
    List<Order> findAllByAuthor(String author);
}

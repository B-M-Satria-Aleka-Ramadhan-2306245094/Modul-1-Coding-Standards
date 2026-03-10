public interface OrderService {
    Order create(Order order);
    Order update(Order order);
    Order findById(String id);
    List<Order> findAllByAuthor(String author);
}
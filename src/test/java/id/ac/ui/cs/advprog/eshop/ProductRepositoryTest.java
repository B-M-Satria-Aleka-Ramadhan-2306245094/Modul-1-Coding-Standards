package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryTest {

    @Test
    void createAndFindAll() {
        ProductRepository repo = new ProductRepository();
        Product p = new Product();
        p.setProductId("p1");
        p.setProductName("Test");
        p.setProductQuantity(5);

        Product created = repo.create(p);
        assertSame(p, created);

        Iterator<Product> it = repo.findAll();
        assertTrue(it.hasNext());
        Product fromIt = it.next();
        assertEquals("p1", fromIt.getProductId());
        assertEquals("Test", fromIt.getProductName());
        assertEquals(5, fromIt.getProductQuantity());
    }
}

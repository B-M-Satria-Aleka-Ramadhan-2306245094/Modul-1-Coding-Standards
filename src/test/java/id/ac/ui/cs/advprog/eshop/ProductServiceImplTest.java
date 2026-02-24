package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import id.ac.ui.cs.advprog.eshop.service.ProductServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceImplTest {

    @Test
    void createAndFindAll() throws Exception {
        ProductServiceImpl service = new ProductServiceImpl();
        ProductRepository repo = new ProductRepository();

        Field f = ProductServiceImpl.class.getDeclaredField("productRepository");
        f.setAccessible(true);
        f.set(service, repo);

        Product p = new Product();
        p.setProductId("p2");
        p.setProductName("Prod2");
        p.setProductQuantity(3);

        Product created = service.create(p);
        assertSame(p, created);

        List<Product> all = service.findAll();
        assertEquals(1, all.size());
        Product got = all.get(0);
        assertEquals("p2", got.getProductId());
        assertEquals("Prod2", got.getProductName());
        assertEquals(3, got.getProductQuantity());
    }
}

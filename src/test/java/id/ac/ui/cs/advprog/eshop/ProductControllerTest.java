package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.controller.ProductController;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
public class ProductControllerTest {

    static class StubService implements ProductService {
        private final List<Product> data = new ArrayList<>();

        @Override
        public Product create(Product product) {
            data.add(product);
            return product;
        }

        @Override
        public List<Product> findAll() {
            return new ArrayList<>(data);
        }

        @Override
        public Product findById(String id) {
            return data.stream().filter(p -> p.getProductId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public void update(Product product) {
            data.stream().filter(p -> p.getProductId().equals(product.getProductId()))
                    .findFirst().ifPresent(p -> {
                        p.setProductName(product.getProductName());
                        p.setProductQuantity(product.getProductQuantity());
                    });
        }

        @Override
        public void delete(String id) {
            data.removeIf(p -> p.getProductId().equals(id));
        }
    }

    @Test
    void pagesAndCreateFlow() throws Exception {
        ProductController controller = new ProductController();
        StubService stub = new StubService();

        Field f = ProductController.class.getDeclaredField("service");
        f.setAccessible(true);
        f.set(controller, stub);

        // create page
        Model model = new ConcurrentModel();
        String view = controller.createProductPage(model);
        assertEquals("createProduct", view);
        assertTrue(model.asMap().containsKey("product"));
        assertNotNull(model.asMap().get("product"));

        // submit create
        Product p = new Product();
        p.setProductId("c1");
        p.setProductName("ControllerProd");
        p.setProductQuantity(7);

        Model model2 = new ConcurrentModel();
        String redirect = controller.createProduct(p, model2);
        assertEquals("redirect:list", redirect);
        assertEquals(1, stub.findAll().size());

        // list page
        Model listModel = new ConcurrentModel();
        String listView = controller.productListPage(listModel);
        assertEquals("productList", listView);
        assertTrue(listModel.asMap().containsKey("products"));
        Object productsObj = listModel.asMap().get("products");
        assertTrue(productsObj instanceof List);
        List<?> products = (List<?>) productsObj;
        assertEquals(1, products.size());
    }
}

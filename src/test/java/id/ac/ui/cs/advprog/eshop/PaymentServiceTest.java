package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import id.ac.ui.cs.advprog.eshop.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private OrderRepository orderRepository;
    private Order baseOrder;

    private static final String METHOD_VOUCHER = "VOUCHER";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REF_CODE_KEY = "referenceCode";
    private static final String BANK_NAME = "BCA";

    @BeforeEach
    void setUp() throws Exception {
        orderRepository = new OrderRepository();
        paymentService = new PaymentServiceImpl(
                new id.ac.ui.cs.advprog.eshop.repository.PaymentRepository(),
                orderRepository
        );

        List<Product> products = new ArrayList<>();
        Product p = new Product();
        p.setProductId("p1");
        p.setProductName("Prod");
        p.setProductQuantity(1);
        products.add(p);
        baseOrder = new Order("o1", products, 1700000000L, "Author");
        orderRepository.save(baseOrder);
    }

    
    @Test
    void addPayment_VoucherValid_Success() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = paymentService.addPayment(baseOrder, METHOD_VOUCHER, data);
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals(METHOD_VOUCHER, payment.getMethod());
    }

    @Test
    void addPayment_VoucherInvalid_Rejected() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "INVALIDCODE0000");
        Payment payment = paymentService.addPayment(baseOrder, METHOD_VOUCHER, data);
        assertEquals("REJECTED", payment.getStatus());
    }

    @Test
    void addPayment_BankTransferValid_Waiting() {
        Map<String, String> data = new HashMap<>();
        data.put(BANK_NAME_KEY, BANK_NAME);
        data.put(REF_CODE_KEY, "INV123");
        Payment payment = paymentService.addPayment(baseOrder, METHOD_BANK_TRANSFER, data);
        assertEquals("WAITING_PAYMENT", payment.getStatus());
        assertEquals(METHOD_BANK_TRANSFER, payment.getMethod());
    }

    @Test
    void addPayment_BankTransferMissingField_Rejected() {
        Map<String, String> data = new HashMap<>();
        data.put(BANK_NAME_KEY, BANK_NAME);
        Payment payment = paymentService.addPayment(baseOrder, METHOD_BANK_TRANSFER, data);
        assertEquals("REJECTED", payment.getStatus());
    }

    @Test
    void setStatus_Success_UpdatesOrder() {
        Map<String, String> data = new HashMap<>();
        data.put(BANK_NAME_KEY, BANK_NAME);
        data.put(REF_CODE_KEY, "INV123");
        Payment payment = paymentService.addPayment(baseOrder, METHOD_BANK_TRANSFER, data);

        paymentService.setStatus(payment, "SUCCESS");

        Order got = orderRepository.findById(baseOrder.getId());
        assertEquals("SUCCESS", got.getStatus());
    }

    @Test
    void setStatus_Rejected_UpdatesOrderFailed() {
        Map<String, String> data = new HashMap<>();
        data.put(BANK_NAME_KEY, BANK_NAME);
        data.put(REF_CODE_KEY, "INV123");
        Payment payment = paymentService.addPayment(baseOrder, METHOD_BANK_TRANSFER, data);

        paymentService.setStatus(payment, "REJECTED");

        Order got = orderRepository.findById(baseOrder.getId());
        assertEquals("FAILED", got.getStatus());
    }

    @Test
    void getPaymentAndGetAllPayments() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = paymentService.addPayment(baseOrder, METHOD_VOUCHER, data);

        Payment got = paymentService.getPayment(payment.getId());
        assertNotNull(got);
        assertEquals(payment.getId(), got.getId());

        List<Payment> all = paymentService.getAllPayments();
        assertTrue(all.stream().anyMatch(p -> p.getId().equals(payment.getId())));
    }
}

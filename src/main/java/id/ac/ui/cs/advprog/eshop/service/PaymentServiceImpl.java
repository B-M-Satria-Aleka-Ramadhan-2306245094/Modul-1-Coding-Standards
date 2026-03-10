package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String METHOD_VOUCHER = "VOUCHER";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_WAITING_PAYMENT = "WAITING_PAYMENT";
    private static final String STATUS_FAILED = "FAILED";

    
    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;

    private final Map<String, String> paymentOrderMap = new ConcurrentHashMap<>();

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public PaymentServiceImpl() {
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setMethod(method);
        payment.setPaymentData(paymentData);

        String status = STATUS_WAITING_PAYMENT;
        if (METHOD_VOUCHER.equals(method)) {
            String voucherCode = paymentData != null ? paymentData.get("voucherCode") : null;
            status = isValidVoucher(voucherCode) ? STATUS_SUCCESS : STATUS_REJECTED;
        } else if (METHOD_BANK_TRANSFER.equals(method)) {
            String bankName = paymentData != null ? paymentData.get("bankName") : null;
            String referenceCode = paymentData != null ? paymentData.get("referenceCode") : null;
            if (isNullOrEmpty(bankName) || isNullOrEmpty(referenceCode)) {
                status = STATUS_REJECTED;
            }
        }
        payment.setStatus(status);

        paymentRepository.save(payment);
        paymentOrderMap.put(payment.getId(), order.getId());
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        paymentRepository.save(payment);

        String orderId = paymentOrderMap.get(payment.getId());
        if (orderId != null) {
            Order order = orderRepository.findById(orderId);
            if (order != null) {
                if (STATUS_SUCCESS.equals(status)) {
                    order.setStatus(STATUS_SUCCESS);
                } else if (STATUS_REJECTED.equals(status)) {
                    order.setStatus(STATUS_FAILED);
                }
                orderRepository.save(order);
            }
        }
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private boolean isValidVoucher(String voucher) {
        if (voucher == null || voucher.length() != 16) {
            return false;
        }
        if (!voucher.startsWith("ESHOP")) {
            return false;
        }
        int digits = 0;
        for (char c : voucher.toCharArray()) {
            if (Character.isDigit(c)) {
                digits++;
            }
        }
        return digits >= 8;
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}

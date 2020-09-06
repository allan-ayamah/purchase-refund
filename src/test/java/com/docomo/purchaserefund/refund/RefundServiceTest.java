package com.docomo.purchaserefund.refund;

import com.docomo.purchaserefund.customer.CustomerService;
import com.docomo.purchaserefund.customer.DbCustomerDao;
import com.docomo.purchaserefund.dbconfig.SessionFactoryConfig;
import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import com.docomo.purchaserefund.model.Purchase;
import com.docomo.purchaserefund.model.Refund;
import com.docomo.purchaserefund.purchase.DbPurchaseDao;
import com.docomo.purchaserefund.purchase.PurchaseService;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RefundServiceTest {
    private CustomerService customerService;
    private RefundService refundService;
    private List<Integer> customerIds = new ArrayList<>();
    private List<Integer> redundIds = new ArrayList<>();

    @Before
    public void setUp() throws PurchaseRefundException {
        SessionFactory sessionFactory = SessionFactoryConfig.initSessionFactory();
        this.customerService = new CustomerService(new DbCustomerDao(sessionFactory));
        this.refundService = new RefundService(new DbRefundDao(sessionFactory), this.customerService);
    }

    @After
    public void cleanUp() throws PurchaseRefundException {
        redundIds.forEach(id -> {
            try {
                refundService.removeRefund(id);
            } catch (PurchaseRefundException e) {}
        });
        customerIds.forEach(id -> {
            try {
                customerService.removeCustomer(id);
            } catch (PurchaseRefundException e) {}
        });
    }


    @Test
    public void addRefund() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund refund = new Refund(0,"IPhone XR", 10.0, "EUR", null, null,null);
        refundService.addRefund(customer.getId(),refund);
        redundIds.add(refund.getId());

        Refund sameRefund = refundService.getRefundById(refund.getId());
        Assert.assertNotNull(sameRefund.getDescription());
        Assert.assertNotNull(sameRefund.getAmount());
        Assert.assertNotNull(sameRefund.getCurrency());
        Assert.assertNotNull(sameRefund.getCustomer());
        Assert.assertNotNull(sameRefund.getCreatedAt());
        Assert.assertNotNull(sameRefund.getUpdatedAt());
    }

    @Test
    public void testAddRefundRequiresAmount() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Refund refund = new Refund(0, "IPhone XR", null, "EUR", null, null, null);
            refundService.addRefund(customer.getId(), refund);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testAddRefundRequiresCurrency() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Refund refund = new Refund(0, "IPhone XR", 10.0, null, null, null, null);
            refundService.addRefund(customer.getId(), refund);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testCustomerPhoneCreditGetsIncreasedOnRefund() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund refund = new Refund(0,"IPhone XR", 10.0, "EUR", null, null,null);
        refundService.addRefund(customer.getId(),refund);
        redundIds.add(refund.getId());

        Customer updatedCustomer = customerService.getCustomerById(customer.getId());
        Assert.assertTrue(Double.valueOf(110.0).equals(updatedCustomer.getPhoneCredit()));
    }

    @Test
    public void testGetAllRefunds() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund r1 = new Refund(0,"IPhone XR", 10.0, "EUR", null, null,null);
        Refund r2 = new Refund(0,"Code Complete", 4.0, "EUR", null, null,null);
        refundService.addRefund(customer.getId(),r1);
        refundService.addRefund(customer.getId(),r2);
        redundIds.add(r1.getId());
        redundIds.add(r2.getId());
        List<Refund> allRefunds = refundService.getAllRefunds();
        Assert.assertTrue(allRefunds.size() >= 2);
        Optional<Refund> r1Record = allRefunds.stream().filter(r -> r.getId() == r1.getId()).findFirst();
        Assert.assertTrue(r1Record.isPresent());
        Optional<Refund> r2Record = allRefunds.stream().filter(r -> r.getId() == r1.getId()).findFirst();
        Assert.assertTrue(r2Record.isPresent());

    }

    @Test
    public void testGetRefundById() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund refund = new Refund(0,"IPhone XR", 10.0, "EUR", null, null,null);
        refundService.addRefund(customer.getId(),refund);
        redundIds.add(refund.getId());

        Refund sameRefund = refundService.getRefundById(refund.getId());
        Assert.assertNotNull(sameRefund);
        Assert.assertTrue(refund.getId() == sameRefund.getId());
    }

    @Test
    public void testRemoveRefund() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund refund = new Refund(0,"IPhone XR", 10.0, "EUR", null, null,null);
        refundService.addRefund(customer.getId(),refund);

        refundService.removeRefund(refund.getId());
        Assert.assertNull(refundService.getRefundById(refund.getId()));
    }
}

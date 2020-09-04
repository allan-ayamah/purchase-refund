package com.docomo.purchaserefund.refund;

import com.docomo.purchaserefund.customer.CustomerService;
import com.docomo.purchaserefund.customer.DbCustomerDao;
import com.docomo.purchaserefund.dbconfig.SessionFactoryConfig;
import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import com.docomo.purchaserefund.model.Refund;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DbRefundDaoTest {
    private CustomerService customerService;
    private RefundDao refundDao;
    private List<Integer> customerIds = new ArrayList<>();
    private List<Integer> redundIds = new ArrayList<>();

    @Before
    public void setUp() throws PurchaseRefundException {
        SessionFactory sessionFactory = SessionFactoryConfig.initSessionFactory();
        this.customerService = new CustomerService(new DbCustomerDao(sessionFactory));
        this.refundDao = new DbRefundDao(sessionFactory);
    }

    @After
    public void cleanUp() throws PurchaseRefundException {
        redundIds.forEach(id -> {
            try {
                refundDao.removeRefund(id);
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
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund refund = new Refund(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        refundDao.addRefund(refund);
        redundIds.add(refund.getId());

        Assert.assertNotNull(refund.getDescription());
        Assert.assertNotNull(refund.getAmount());
        Assert.assertNotNull(refund.getCurrency());
        Assert.assertNotNull(refund.getCustomer());
        Assert.assertNotNull(refund.getCreatedAt());
        Assert.assertNotNull(refund.getUpdatedAt());
    }

    @Test
    public void testAddRefundRequiresAmount() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Refund refund = new Refund(0, "IPhone XR", null, "EUR", customer, new Date(), new Date());
            refundDao.addRefund(refund);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testAddRefundRequiresCurrency() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Refund refund = new Refund(0, "IPhone XR", 10.0, null, customer, new Date(), new Date());
            refundDao.addRefund(refund);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }


    @Test
    public void testGetAllRefunds() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund r1 = new Refund(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        Refund r2 = new Refund(0,"Code Complete", 4.0, "EUR", customer, new Date(),new Date());
        refundDao.addRefund(r1);
        refundDao.addRefund(r2);
        redundIds.add(r1.getId());
        redundIds.add(r2.getId());
        List<Refund> allRefunds = refundDao.getAllRefunds();
        Assert.assertTrue(allRefunds.size() >= 2);
        Optional<Refund> r1Record = allRefunds.stream().filter(r -> r.getId() == r1.getId()).findFirst();
        Assert.assertTrue(r1Record.isPresent());
        Optional<Refund> r2Record = allRefunds.stream().filter(r -> r.getId() == r1.getId()).findFirst();
        Assert.assertTrue(r2Record.isPresent());

    }

    @Test
    public void testGetRefundById() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund refund = new Refund(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        refundDao.addRefund(refund);
        redundIds.add(refund.getId());

        Refund sameRefund = refundDao.getRefundById(refund.getId());
        Assert.assertNotNull(sameRefund);
        Assert.assertTrue(refund.getId() == sameRefund.getId());
    }

    @Test
    public void testRemoveRefund() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Refund refund = new Refund(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        refundDao.addRefund(refund);

        refundDao.removeRefund(refund.getId());
        Assert.assertNull(refundDao.getRefundById(refund.getId()));
    }
}

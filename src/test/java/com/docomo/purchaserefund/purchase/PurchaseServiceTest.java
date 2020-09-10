package com.docomo.purchaserefund.purchase;

import com.docomo.purchaserefund.customer.CustomerService;
import com.docomo.purchaserefund.customer.DbCustomerDao;
import com.docomo.purchaserefund.dbconfig.SessionFactoryConfig;
import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import com.docomo.purchaserefund.model.Purchase;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PurchaseServiceTest {
    private CustomerService customerService;
    private PurchaseService purchaseService;
    private List<Integer> customerIds = new ArrayList<>();
    private List<Integer> purchaseIds = new ArrayList<>();

    @Before
    public void setUp() throws PurchaseRefundException {
        SessionFactory sessionFactory = SessionFactoryConfig.initSessionFactory();
        this.customerService = new CustomerService(new DbCustomerDao(sessionFactory));
        this.purchaseService = new PurchaseService(new DbPurchaseDao(sessionFactory), this.customerService);
    }

    @After
    public void cleanUp() throws PurchaseRefundException {
        purchaseIds.forEach(id -> {
            try {
                purchaseService.removePurchase(id);
            } catch (PurchaseRefundException e) {}
        });
        customerIds.forEach(id -> {
            try {
                customerService.removeCustomer(id);
            } catch (PurchaseRefundException e) {}
        });
    }


    @Test
    public void addPurchase() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase purchase = new Purchase(0,"IPhone XR", 10.0, "EUR", null, null,null);
        purchaseService.addPurchase(customer.getId(),purchase);
        purchaseIds.add(purchase.getId());

        Purchase samePurchase = purchaseService.getPurchaseById(purchase.getId());
        Assert.assertNotNull(samePurchase.getItemDescription());
        Assert.assertNotNull(samePurchase.getAmount());
        Assert.assertNotNull(samePurchase.getCurrency());
        Assert.assertNotNull(samePurchase.getCustomer());
        Assert.assertNotNull(samePurchase.getCreatedAt());
        Assert.assertNotNull(samePurchase.getUpdatedAt());
    }

    @Test
    public void testAddPurchaseRequiresAmount() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Purchase purchase = new Purchase(0, "IPhone XR", null, "EUR", null, null, null);
            purchaseService.addPurchase(customer.getId(), purchase);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testAddPurchaseRequiresCurrency() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Purchase purchase = new Purchase(0, "IPhone XR", 10.0, null, null, null, null);
            purchaseService.addPurchase(customer.getId(), purchase);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testAddPurchaseInsufficientPhoneCredit() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Purchase purchase = new Purchase(0, "IPhone XR", 120.0, null, null, null, null);
            purchaseService.addPurchase(customer.getId(), purchase);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testCustomerPhoneCreditGetsDeductedOnPurchase() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase purchase = new Purchase(0,"IPhone XR", 10.0, "EUR", null, null,null);
        purchaseService.addPurchase(customer.getId(),purchase);
        purchaseIds.add(purchase.getId());

        Customer updatedCustomer = customerService.getCustomerById(customer.getId());
        Assert.assertTrue(Double.valueOf(90.0).equals(updatedCustomer.getPhoneCredit()));
    }

    @Test
    public void testGetAllPurchases() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase p1 = new Purchase(0,"IPhone XR", 10.0, "EUR", null, null,null);
        Purchase p2 = new Purchase(0,"Code Complete", 4.0, "EUR", null, null,null);
        purchaseService.addPurchase(customer.getId(),p1);
        purchaseService.addPurchase(customer.getId(),p2);
        purchaseIds.add(p1.getId());
        purchaseIds.add(p2.getId());
        List<Purchase> allPurchases = purchaseService.getAllPurchases();
        Assert.assertTrue(allPurchases.size() >= 2);
        Optional<Purchase> p1Record = allPurchases.stream().filter(p -> p.getId() == p1.getId()).findFirst();
        Assert.assertTrue(p1Record.isPresent());
        Optional<Purchase> p2Record = allPurchases.stream().filter(p -> p.getId() == p2.getId()).findFirst();
        Assert.assertTrue(p2Record.isPresent());
    }

    @Test
    public void testGetPurchaseById() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase purchase = new Purchase(0,"IPhone XR", 10.0, "EUR", null, null,null);
        purchaseService.addPurchase(customer.getId(),purchase);
        purchaseIds.add(purchase.getId());

        Purchase samePurchase = purchaseService.getPurchaseById(purchase.getId());
        Assert.assertNotNull(samePurchase);
        Assert.assertTrue(purchase.getId() == samePurchase.getId());
    }

    @Test
    public void testRemovePurchase() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase p1 = new Purchase(0,"IPhone XR", 10.0, "EUR", null, null,null);
        purchaseService.addPurchase(customer.getId(),p1);

        purchaseService.removePurchase(p1.getId());
        Assert.assertNull(purchaseService.getPurchaseById(p1.getId()));
    }
}

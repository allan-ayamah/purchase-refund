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
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DbPurchaseDaoTest {
    private CustomerService customerService;
    private PurchaseDao purchaseDao;
    private List<Integer> customerIds = new ArrayList<>();
    private List<Integer> purchaseIds = new ArrayList<>();

    @Before
    public void setUp() throws PurchaseRefundException {
        SessionFactory sessionFactory = SessionFactoryConfig.initSessionFactory();
        this.customerService = new CustomerService(new DbCustomerDao(sessionFactory));
        this.purchaseDao = new DbPurchaseDao(sessionFactory);
    }

    @After
    public void cleanUp() throws PurchaseRefundException {
        purchaseIds.forEach(id -> {
            try {
                purchaseDao.removePurchase(id);
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
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase purchase = new Purchase(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        purchaseDao.addPurchase(purchase);
        purchaseIds.add(purchase.getId());

        Assert.assertNotNull(purchase.getItemDescription());
        Assert.assertNotNull(purchase.getAmount());
        Assert.assertNotNull(purchase.getCurrency());
        Assert.assertNotNull(purchase.getCustomer());
        Assert.assertNotNull(purchase.getCreatedAt());
        Assert.assertNotNull(purchase.getUpdatedAt());
    }

    @Test
    public void testAddPurchaseRequiresAmount() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Purchase purchase = new Purchase(0, "IPhone XR", null, "EUR", customer, new Date(), new Date());
            purchaseDao.addPurchase(purchase);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testAddPurchaseRequiresCurrency() throws PurchaseRefundException{
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        try {
            Purchase purchase = new Purchase(0, "IPhone XR", 10.0, null, customer, new Date(), new Date());
            purchaseDao.addPurchase(purchase);
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected) {

        }
    }

    @Test
    public void testGetAllPurchases() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase p1 = new Purchase(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        Purchase p2 = new Purchase(0,"Code Complete", 4.0, "EUR", customer, new Date(),new Date());
        purchaseDao.addPurchase(p1);
        purchaseDao.addPurchase(p2);
        purchaseIds.add(p1.getId());
        purchaseIds.add(p2.getId());
        List<Purchase> allPurchases = purchaseDao.getAllPurchases();
        Assert.assertTrue(allPurchases.size() >= 2);
        Optional<Purchase> p1Record = allPurchases.stream().filter(p -> p.getId() == p1.getId()).findFirst();
        Assert.assertTrue(p1Record.isPresent());
        Optional<Purchase> p2Record = allPurchases.stream().filter(p -> p.getId() == p2.getId()).findFirst();
        Assert.assertTrue(p2Record.isPresent());
    }

    @Test
    public void testGetPurchaseById() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase purchase = new Purchase(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        purchaseDao.addPurchase(purchase);
        purchaseIds.add(purchase.getId());

        Purchase samePurchase = purchaseDao.getPurchaseById(purchase.getId());
        Assert.assertNotNull(samePurchase);
        Assert.assertTrue(purchase.getId() == samePurchase.getId());
    }

    @Test
    public void testRemovePurchase() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Purchase p1 = new Purchase(0,"IPhone XR", 10.0, "EUR", customer, new Date(),new Date());
        purchaseDao.addPurchase(p1);

        purchaseDao.removePurchase(p1.getId());
        Assert.assertNull(purchaseDao.getPurchaseById(p1.getId()));
    }
}

package com.docomo.purchaserefund.customer;

import com.docomo.purchaserefund.dbconfig.SessionFactoryConfig;
import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DbCustomerDaoTest {
    private CustomerDao customerDao;
    private List<Integer> customerIds = new ArrayList<>();

    @Before
    public void setUp() throws PurchaseRefundException {
        SessionFactory sessionFactory = SessionFactoryConfig.initSessionFactory();
        this.customerDao = new DbCustomerDao(sessionFactory);
    }

    @After
    public void cleanUp() throws PurchaseRefundException {
        customerIds.forEach( customerId -> {
            try {
                customerDao.removeCustomer(customerId);
            } catch (PurchaseRefundException e) {}
        });
    }

    @Test
    public void testGetAllCustomers() throws PurchaseRefundException {
        Customer c1 = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        Customer c2 = new Customer(0,"Harry Potter","+393275415265",100.0,new Date(),new Date());
        customerDao.addCustomer(c1);
        customerDao.addCustomer(c2);
        customerIds.add(c1.getId());
        customerIds.add(c2.getId());
        List<Customer> customers = customerDao.getAllCustomers();
        Assert.assertTrue(customers.size() >= 2);
        Optional<Customer> c1Record = customers.stream().filter(c -> c.getId() == c1.getId()).findFirst();
        Assert.assertTrue(c1Record.isPresent());
        Optional<Customer> c2Record = customers.stream().filter(c -> c.getId() == c2.getId()).findFirst();
        Assert.assertTrue(c2Record.isPresent());
    }

    @Test
    public void testAddCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        Integer customerId = customerDao.addCustomer(customer);
        Assert.assertNotNull(customerId);
        customerIds.add(customerId);
        Assert.assertNotNull(customerDao.getCustomerById(customerId));
        Assert.assertNotNull(customer.getName());
        Assert.assertNotNull(customer.getPhoneNumber());
        Assert.assertNotNull(customer.getCreatedAt());
        Assert.assertNotNull(customer.getUpdatedAt());
    }

    @Test
    public void testPhoneNumberRequired(){
        try {
            customerDao.addCustomer(new Customer(0,"Michael",null,100.0,new Date(),new Date()));
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected){

        }
    }

    @Test
    public void testPhoneCreditRequired(){
        try {
            customerDao.addCustomer(new Customer(0,"Michael","+393265412584",null,new Date(),new Date()));
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected){

        }
    }

    @Test
    public void testGetCustomerById() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerDao.addCustomer(customer);
        customerIds.add(customer.getId());

        Customer sameCustomer = customerDao.getCustomerById(customer.getId());
        Assert.assertNotNull(sameCustomer);
        Assert.assertTrue(customer.getId() == sameCustomer.getId());
    }

    @Test
    public void testGetCustomerByPhoneNumber() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerDao.addCustomer(customer);
        customerIds.add(customer.getId());

        Customer sameCustomer = customerDao.getCustomerByPhoneNumber(customer.getPhoneNumber());
        Assert.assertNotNull(sameCustomer);
        Assert.assertTrue(customer.getId() == sameCustomer.getId());
        Assert.assertTrue(customer.getPhoneNumber().equals(sameCustomer.getPhoneNumber()));
    }

    @Test
    public void testUpdateCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerDao.addCustomer(customer);
        customerIds.add(customer.getId());
        customer.setName("James Brown");
        customerDao.updateCustomer(customer);
        Customer newCustomer = customerDao.getCustomerById(customer.getId());
        Assert.assertTrue("James Brown".equals(newCustomer.getName()));
    }

    @Test
    public void testRemoveCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,new Date(),new Date());
        customerDao.addCustomer(customer);
        customerDao.removeCustomer(customer.getId());
        Assert.assertNull(customerDao.getCustomerById(customer.getId()));
    }
}

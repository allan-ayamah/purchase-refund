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

public class CustomerServiceTest {
    private CustomerService customerService;
    private List<Integer> customerIds = new ArrayList<>();
    @Before
    public void setUp() throws PurchaseRefundException {
        SessionFactory sessionFactory = SessionFactoryConfig.initSessionFactory();
        CustomerDao customerDao = new DbCustomerDao(sessionFactory);
        this.customerService = new CustomerService(customerDao);
    }

    @After
    public void cleanUp() throws PurchaseRefundException {
        customerIds.forEach( customerId -> {
            try {
                customerService.removeCustomer(customerId);
            } catch (PurchaseRefundException e) {}
        });
    }

    @Test
    public void testGetAllCustomers() throws PurchaseRefundException {
        Customer c1 = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        Customer c2 = new Customer(0,"Harry Potter","+393275415265",100.0,null,null);
        customerService.addCustomer(c1);
        customerService.addCustomer(c2);
        customerIds.add(c1.getId());
        customerIds.add(c2.getId());
        List<Customer> customers = customerService.getAllCustomers();
        Assert.assertTrue(customers.size() >= 2);
        Optional<Customer> c1Record = customers.stream().filter(c -> c.getId() == c1.getId()).findFirst();
        Assert.assertTrue(c1Record.isPresent());
        Optional<Customer> c2Record = customers.stream().filter(c -> c.getId() == c2.getId()).findFirst();
        Assert.assertTrue(c2Record.isPresent());
    }

    @Test
    public void testIsPhoneNumberValid() {
        Assert.assertTrue(customerService.isPhoneNumberValid("3272548962"));
        Assert.assertTrue(customerService.isPhoneNumberValid("327(254)8962"));
        Assert.assertTrue(customerService.isPhoneNumberValid("327 (254) 8962"));
        Assert.assertTrue(customerService.isPhoneNumberValid("+393272548962"));
    }

    @Test
    public void testPhoneNumberIsNotValid() {
        Assert.assertFalse(customerService.isPhoneNumberValid(null));
        Assert.assertFalse(customerService.isPhoneNumberValid("         "));
        Assert.assertFalse(customerService.isPhoneNumberValid("32785621"));
        Assert.assertFalse(customerService.isPhoneNumberValid("327855854afr"));
        Assert.assertFalse(customerService.isPhoneNumberValid("327548+6951"));
    }

    @Test
    public void testAddCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        Integer customerId = customerService.addCustomer(customer);
        Assert.assertNotNull(customerId);
        customerIds.add(customerId);
        Customer sameCustomer = customerService.getCustomerById(customerId);
        Assert.assertNotNull(sameCustomer);
        Assert.assertNotNull(sameCustomer.getName());
        Assert.assertNotNull(sameCustomer.getPhoneNumber());
        Assert.assertNotNull(sameCustomer.getCreatedAt());
        Assert.assertNotNull(sameCustomer.getUpdatedAt());
    }

    @Test
    public void testPhoneNumberRequired(){
        try {
            customerService.addCustomer(new Customer(0,"Michael",null,100.0,null,null));
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected){

        }
    }

    @Test
    public void testPhoneCreditRequired(){
        try {
            customerService.addCustomer(new Customer(0,"Michael","+393265412584",null,null,null));
            Assert.fail("This should fail");
        } catch (PurchaseRefundException expected){

        }
    }

    @Test
    public void testGetCustomerById() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());

        Customer sameCustomer = customerService.getCustomerById(customer.getId());
        Assert.assertNotNull(sameCustomer);
        Assert.assertTrue(customer.getId() == sameCustomer.getId());
    }

    @Test
    public void testGetCustomerByPhoneNumber() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());

        Customer sameCustomer = customerService.getCustomerByPhoneNumber(customer.getPhoneNumber());
        Assert.assertNotNull(sameCustomer);
        Assert.assertTrue(customer.getId() == sameCustomer.getId());
        Assert.assertTrue(customer.getPhoneNumber().equals(sameCustomer.getPhoneNumber()));
    }

    @Test
    public void testUpdateCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        Date expectedUpdatedAt = customer.getUpdatedAt();
        customer.setName("James Brown");
        customerService.updateCustomer(customer);
        Customer newCustomer = customerService.getCustomerById(customer.getId());
        Assert.assertTrue("James Brown".equals(newCustomer.getName()));
        Assert.assertTrue(!expectedUpdatedAt.equals(newCustomer.getUpdatedAt()));
    }

    @Test
    public void testRemoveCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerService.removeCustomer(customer.getId());
        Assert.assertNull(customerService.getCustomerById(customer.getId()));
    }
}

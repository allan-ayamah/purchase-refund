package com.docomo.purchaserefund.customer;

import com.docomo.purchaserefund.dbconfig.SessionFactoryConfig;
import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.helper.HibernateHelper;
import com.docomo.purchaserefund.model.Customer;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if(customerIds.isEmpty()) return;
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
    }

    @Test
    public void testAddCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        Integer customerId = customerService.addCustomer(customer);
        Assert.assertNotNull(customerId);
        customerIds.add(customerId);
        Assert.assertNotNull(customerService.getCustomerById(customerId));
        Assert.assertNotNull(customer.getName());
        Assert.assertNotNull(customer.getPhoneNumber());
        Assert.assertNotNull(customer.getCreatedAt());
        Assert.assertNotNull(customer.getUpdatedAt());
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
    public void testUpdateCustomer() throws PurchaseRefundException {
        Customer customer = new Customer(0,"Maria Jones","+393275412502",100.0,null,null);
        customerService.addCustomer(customer);
        customerIds.add(customer.getId());
        String expectedName = customer.getName();
        Date expectedUpdatedAt = customer.getUpdatedAt();
        customer.setName("James Brown");
        customerService.updateCustomer(customer);
        Customer newCustomer = customerService.getCustomerById(customer.getId());
        Assert.assertTrue("James Brown".equals(newCustomer.getName()));
        Assert.assertTrue(!expectedUpdatedAt.equals(newCustomer.getUpdatedAt()));
    }

}

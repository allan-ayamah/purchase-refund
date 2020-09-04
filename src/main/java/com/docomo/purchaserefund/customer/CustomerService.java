package com.docomo.purchaserefund.customer;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerDao customerDao;

    @Autowired
    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    /**
     * Return a list of all customers in the system
     * @return return a list of customers
     * @throws PurchaseRefundException
     */
    public List<Customer> getAllCustomers() throws PurchaseRefundException {
        try {
            return customerDao.getAllCustomers();
        } catch (PurchaseRefundException e ){
            PurchaseRefundException ex = new PurchaseRefundException("Could not get all customers", e);
            log.error("", ex);
            throw  ex;
        }
    }

    /**
     * Returns customer who has the id of the input customerId
     * @param customerId
     * @return returns customer
     * @throws PurchaseRefundException
     */
    public Customer getCustomerById(Integer customerId) throws PurchaseRefundException{
        try {
            if(log.isDebugEnabled())
                log.debug(String.format("Get customer by id = %s",customerId));
            return customerDao.getCustomerById(customerId);
        } catch (PurchaseRefundException e ){
            PurchaseRefundException ex = new PurchaseRefundException(String.format("Could not customer by id = %s",customerId), e);
            log.error("", ex);
            throw  ex;
        }
    }

    /**
     * Return customer who has a phone number equals to the input phoneNumber
     * @param phoneNumber the phone number to search
     * @return returns customer
     * @throws PurchaseRefundException
     */
    public Customer getCustomerByPhoneNumber(String phoneNumber) throws PurchaseRefundException{
        try {
            if(log.isDebugEnabled()) {
                log.debug(String.format("Get customer by phone number = %s", phoneNumber));
            }
            return customerDao.getCustomerByPhoneNumber(phoneNumber);
        } catch (PurchaseRefundException e ){
            PurchaseRefundException ex = new PurchaseRefundException(String.format("Could not customer by phone number = %s", phoneNumber), e);
            log.error("", ex);
            throw  ex;
        }
    }

    /**
     * Adds new customer, if the phone number already exists it throws an exception
     * and doesn't save new customer
     * @param newCustomer
     * @return returns customer id
     * @throws PurchaseRefundException
     */
    public Integer addCustomer(Customer newCustomer) throws PurchaseRefundException {
        try {
            if(newCustomer == null)
                return null;
            Customer customer = customerDao.getCustomerByPhoneNumber(newCustomer.getPhoneNumber());
            if(customer != null){
                String message = String.format("Customer with phone number = %s already exist", newCustomer.getPhoneNumber());
                PurchaseRefundException ex = new PurchaseRefundException(message);
                if(log.isErrorEnabled()) {
                    log.error("",ex);
                }
                throw ex;
            }
            newCustomer.setCreatedAt(new Date());
            newCustomer.setUpdatedAt(new Date());
            return customerDao.addCustomer(newCustomer);
        } catch (PurchaseRefundException e ){
            PurchaseRefundException ex = new PurchaseRefundException("Could not add new customer", e);
            log.error("", ex);
            throw  ex;
        }
    }

    /**
     * Updates a customer
     * @param customer
     * @throws PurchaseRefundException
     */
    public void updateCustomer(Customer customer) throws PurchaseRefundException {
        try {
            if (customer == null)
                return;
            customer.setUpdatedAt(new Date());
            customerDao.updateCustomer(customer);
        } catch (PurchaseRefundException e ){
            PurchaseRefundException ex = new PurchaseRefundException(String.format("Could not update customer, id = %s",customer.getId()), e);
            log.error("", ex);
            throw  ex;
        }
    }

    public void removeCustomer(Integer customerId) throws PurchaseRefundException {
        if(customerId == null) return;
        try{
            customerDao.removeCustomer(customerId);
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException(String.format("Could not remove customer, id = %s",customerId), e);
            log.error("", ex);
            throw  ex;
        }
    }
}

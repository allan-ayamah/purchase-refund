package com.docomo.purchaserefund.customer;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;

import java.util.List;

public interface CustomerDao {
    public List<Customer> getAllCustomers() throws PurchaseRefundException;
    public Customer getCustomerById(Integer customerId) throws PurchaseRefundException;
    public Customer getCustomerByPhoneNumber(String phoneNumber) throws PurchaseRefundException;
    public Integer addCustomer(Customer newCustomer) throws PurchaseRefundException;
    public void updateCustomer(Customer customer) throws PurchaseRefundException;
    public void removeCustomer(Integer customerId) throws PurchaseRefundException;
}

package com.docomo.purchaserefund.purchase;

import com.docomo.purchaserefund.customer.CustomerService;
import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import com.docomo.purchaserefund.model.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PurchaseService {
    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);
    private final PurchaseDao purchaseDao;
    private final CustomerService customerService;

    @Autowired
    public PurchaseService(PurchaseDao purchaseDao, CustomerService customerService) {
        this.purchaseDao = purchaseDao;
        this.customerService = customerService;
    }

    /**
     * Returns a list of all purchases in the system
     * @return list of purchases
     * @throws PurchaseRefundException
     */
    public List<Purchase> getAllPurchases() throws PurchaseRefundException{
        try{
            return purchaseDao.getAllPurchases();
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException("Could not get all purchases", e);
            log.error("",ex);
            throw ex;
        }
    }

    /**
     * Returns a purchase
     * @param purchaseId the id of the purchase
     * @return the purchase object
     * @throws PurchaseRefundException
     */
    public Purchase getPurchaseById(Integer purchaseId) throws PurchaseRefundException {
        if(purchaseId == null) return null;
        try{
            return purchaseDao.getPurchaseById(purchaseId);
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException("Could not get purchase by id", e);
            log.error("",ex);
            throw ex;
        }
    }

    /**
     * Makes a new purchase
     *
     * @param customerId the customer id
     * @param purchase  the details of the purchase
     * @return id of the purchase
     * @throws PurchaseRefundException
     */
    public Integer addPurchase(Integer customerId, Purchase purchase) throws PurchaseRefundException {
        if(purchase == null || customerId == null)
            return null;
        Customer customer = customerService.getCustomerById(customerId);
        if(customer == null) {
            throw new PurchaseRefundException(String.format("Customer with id = %s does not exist", customerId));
        }
        purchase.setCustomer(customer);
        if(purchase.getAmount()  == null) {
            throw new PurchaseRefundException("Amount cannot be null");
        }
        if(purchase.getAmount() > customer.getPhoneCredit()){
            throw new PurchaseRefundException("Insufficient phone credit");
        }
        if(log.isTraceEnabled()){
            log.trace(String.format("Customer %d makes a purchase of amount %s",customer.getId(), purchase.getAmount()));
        }
        Double remainingPhoneCredit = customer.getPhoneCredit() - purchase.getAmount();
        customer.setPhoneCredit(remainingPhoneCredit);
        if(log.isTraceEnabled()){
            log.trace(String.format("Customer remaining phone credit: %s",remainingPhoneCredit));
        }
        purchase.setCreatedAt(new Date());
        purchase.setUpdatedAt(new Date());
        try {
            Integer purchaseId = purchaseDao.addPurchase(purchase);
            customerService.updateCustomer(customer);
            return purchaseId;
        } catch(PurchaseRefundException e) {
            PurchaseRefundException ex = new PurchaseRefundException("Could not make purchase", e);
            log.error("",ex);
            throw ex;
        }
    }

    /**
     * Removes a particular purchase from the system
     * @param purchaseId the id of the purchase to remove
     * @throws PurchaseRefundException
     */
    public void removePurchase(Integer purchaseId) throws PurchaseRefundException {
        if (purchaseId == null) return;
        try{
            purchaseDao.removePurchase(purchaseId);
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException(String.format("Could not remove purchase, id = %s",purchaseId), e);
            log.error("", ex);
            throw  ex;
        }
    }

}

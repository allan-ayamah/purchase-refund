package com.docomo.purchaserefund.refund;

import com.docomo.purchaserefund.customer.CustomerService;
import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import com.docomo.purchaserefund.model.Purchase;
import com.docomo.purchaserefund.model.Refund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RefundService {
    private static final Logger log = LoggerFactory.getLogger(RefundService.class);
    private final RefundDao refundDao;
    private final CustomerService customerService;

    @Autowired
    public RefundService(RefundDao refundDao, CustomerService customerService) {
        this.refundDao = refundDao;
        this.customerService = customerService;
    }

    /**
     * Returns a list of all refunds in the system
     * @return the list of refunds
     * @throws PurchaseRefundException
     */
    public List<Refund> getAllRefunds() throws PurchaseRefundException {
        try {
            return refundDao.getAllRefunds();
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException("Could not get all refunds", e);
            log.error("",ex);
            throw ex;
        }
    }

    public Refund getRefundById(int id) throws PurchaseRefundException{
        try{
            return refundDao.getRefundById(id);
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException("Could not get Refund by id", e);
            log.error("",ex);
            throw ex;
        }
    }

    /**
     * Returns all refunds made to to a customer
     * @param customerId the customer id
     * @return  the list of refunds
     * @throws PurchaseRefundException
     */
    public List<Refund> getRefundsForCustomer(Integer customerId) throws PurchaseRefundException {
        if(customerId == null) return null;
        try {
          return refundDao.getRefundByCustomerId(customerId);
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException(String.format("Could not get refunds for customer: ", customerId), e);
            log.error("",ex);
            throw ex;
        }
    }

    /**
     * Makes new refund to a customer
     * @param customerId    the customer id
     * @param refund    teh details of the refund
     * @return id of the refund
     * @throws PurchaseRefundException
     */
    public Integer addRefund(Integer customerId, Refund refund) throws PurchaseRefundException {
        if(refund == null || customerId == null)
            return null;
        Customer customer = customerService.getCustomerById(customerId);
        if(customer == null) {
            throw new PurchaseRefundException(String.format("Customer with id = %s does not exist", customerId));
        }
        refund.setCustomer(customer);
        if(refund.getAmount() == null) {
            throw new PurchaseRefundException("Amount of refund is required");
        }
        Double newPhoneCredit = customer.getPhoneCredit() + refund.getAmount();
        customer.setPhoneCredit(newPhoneCredit);
        if(log.isTraceEnabled()){
            log.trace(String.format("Customer phone credit: %s", newPhoneCredit));
        }
        refund.setCreatedAt(new Date());
        refund.setUpdatedAt(new Date());
        try {
            Integer refundId = refundDao.addRefund(refund);
            customerService.updateCustomer(customer);
            return refundId;
        } catch (PurchaseRefundException e) {
            PurchaseRefundException ex = new PurchaseRefundException("Could not make refund", e);
            log.error("",ex);
            throw ex;
        }
    }

    /**
     * Removes a particular refund from the system
     * @param refundId the if of the refund
     * @throws PurchaseRefundException
     */
    public void removeRefund(Integer refundId) throws PurchaseRefundException {
        if(refundId == null) return;
        try{
            refundDao.removeRefund(refundId);
        } catch (PurchaseRefundException e){
            PurchaseRefundException ex = new PurchaseRefundException(String.format("Could not remove refund, id = %s",refundId), e);
            log.error("", ex);
            throw  ex;
        }
    }


}

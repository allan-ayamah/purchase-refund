package com.docomo.purchaserefund.refund;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Refund;

import java.util.List;

public interface RefundDao {
    public List<Refund> getAllRefunds() throws PurchaseRefundException;
    public Refund getRefundById(int refundId) throws PurchaseRefundException;
    public List<Refund> getRefundByCustomerId(int customerId) throws PurchaseRefundException;
    public Integer addRefund(Refund refund) throws PurchaseRefundException;
    public void removeRefund(int refundId) throws PurchaseRefundException;
}

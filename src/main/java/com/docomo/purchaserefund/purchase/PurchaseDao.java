package com.docomo.purchaserefund.purchase;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Purchase;

import java.util.List;

public interface PurchaseDao {
    public List<Purchase> getAllPurchases() throws PurchaseRefundException;
    public Purchase getPurchaseById(int purchaseId) throws PurchaseRefundException;
    public Integer addPurchase(Purchase purchase) throws PurchaseRefundException;
    public void removePurchase(int purchaseId) throws PurchaseRefundException;
}

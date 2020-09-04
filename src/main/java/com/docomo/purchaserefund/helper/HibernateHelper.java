package com.docomo.purchaserefund.helper;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateHelper {

    public static void rollbackAndClose(Session session) throws PurchaseRefundException {
        try {
            if (session != null && session.isOpen()) {
                Transaction tx = session.getTransaction();
                if ((tx != null) && (tx.isActive())) {
                    tx.rollback();
                }
                if(session.isOpen()){
                    session.close();
                }
            }
        } catch (Throwable e) {
            throw new PurchaseRefundException(e);
        }
    }

    public static void commitAndClose(Session session) throws PurchaseRefundException {
        try {
            if (session != null && session.isOpen()) {
                Transaction tx = session.getTransaction();
                if ((tx != null) && (tx.isActive())) {
                    tx.commit();
                }
                session.close();
            }
        } catch (Throwable ex) {
            throw new PurchaseRefundException(ex);
        }
    }
}

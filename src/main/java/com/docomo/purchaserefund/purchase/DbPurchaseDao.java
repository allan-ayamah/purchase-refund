package com.docomo.purchaserefund.purchase;


import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.helper.HibernateHelper;
import com.docomo.purchaserefund.model.Purchase;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DbPurchaseDao implements PurchaseDao{
    private final SessionFactory sessionFactory;
    @Autowired
    public DbPurchaseDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Purchase> getAllPurchases() throws PurchaseRefundException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Purchase");
            List<Purchase> result = (List<Purchase>) query.list();
            HibernateHelper.commitAndClose(session);
            return result;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not get all purchases from db", e);
        }
    }

    @Override
    public Purchase getPurchaseById(int purchaseId) throws PurchaseRefundException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Purchase p WHERE p.id = :id").setInteger("id",purchaseId);
            Purchase purchase = (Purchase) query.uniqueResult();
            HibernateHelper.commitAndClose(session);
            return purchase;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException(String.format("Could not get purchase by id = %s",purchaseId), e);
        }
    }

    @Override
    public Integer addPurchase(Purchase purchase) throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(purchase);
            HibernateHelper.commitAndClose(session);
            return purchase.getId();
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not add purchases to db", e);
        }
    }

    @Override
    public void removePurchase(int purchaseId) throws PurchaseRefundException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM Purchase p WHERE p.id = :id");
            query.setInteger("id", purchaseId).executeUpdate();
            HibernateHelper.commitAndClose(session);
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException(String.format("Could not remove purchase = %s",purchaseId), e);
        }
    }
}

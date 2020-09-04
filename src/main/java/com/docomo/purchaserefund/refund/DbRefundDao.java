package com.docomo.purchaserefund.refund;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.helper.HibernateHelper;
import com.docomo.purchaserefund.model.Purchase;
import com.docomo.purchaserefund.model.Refund;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Ref;
import java.util.List;

@Repository
public class DbRefundDao implements RefundDao {
    private SessionFactory sessionFactory;

    @Autowired
    public DbRefundDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Refund> getAllRefunds() throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Refund r LEFT JOIN FETCH r.customer");
            List<Refund> result = (List<Refund>) query.list();
            HibernateHelper.commitAndClose(session);
            return result;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not get refunds from db",e);
        }
    }

    @Override
    public Refund getRefundById(int refundId) throws PurchaseRefundException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Refund r LEFT JOIN FETCH r.customer WHERE r.id = :id")
                    .setInteger("id",refundId);
            Refund refund = (Refund) query.uniqueResult();
            HibernateHelper.commitAndClose(session);
            return refund;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException(String.format("Could not get refund by id = %s",refundId), e);
        }
    }

    @Override
    public List<Refund> getRefundByCustomerId(int customerId) throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Refund r LEFT JOIN FETCH r.customer c WHERE c.id = :customerId");
            query.setInteger("customerId",customerId);
            List<Refund> result = (List<Refund>) query.list();
            HibernateHelper.commitAndClose(session);
            return result;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not get refunds for customer",e);
        }
    }

    @Override
    public Integer addRefund(Refund refund) throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(refund);
            HibernateHelper.commitAndClose(session);
            return refund.getId();
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not add refund to db", e);
        }
    }

    @Override
    public void removeRefund(int refundId) throws PurchaseRefundException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM Refund r WHERE r.id = :id");
            query.setInteger("id", refundId).executeUpdate();
            HibernateHelper.commitAndClose(session);
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException(String.format("Could not remove refund = %s",refundId), e);
        }
    }
}

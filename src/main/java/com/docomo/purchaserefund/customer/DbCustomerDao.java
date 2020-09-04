package com.docomo.purchaserefund.customer;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.helper.HibernateHelper;
import com.docomo.purchaserefund.model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("dbCustomerDao")
public class DbCustomerDao  implements CustomerDao{
    private final SessionFactory sessionFactory;

    @Autowired
    public DbCustomerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Customer> getAllCustomers() throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Customer");
            List<Customer> result = (List<Customer>) query.list();
            HibernateHelper.commitAndClose(session);
            return result;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not get customers from db",e);
        }
    }

    @Override
    public Customer getCustomerById(int customerId) throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Customer c WHERE c.id = :id");
            query.setInteger("id", customerId);
            Customer customer = (Customer) query.uniqueResult();
            HibernateHelper.commitAndClose(session);
            return customer;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not get customer by id ="+customerId, e);
        }
    }

    @Override
    public Customer getCustomerByPhoneNumber(String phoneNumber) throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery("FROM Customer c WHERE c.phoneNumber = :phoneNumber");
            query.setString("phoneNumber", phoneNumber);
            Customer customer = (Customer) query.uniqueResult();
            HibernateHelper.commitAndClose(session);
            return customer;
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not get customer by phone number ="+phoneNumber, e);
        }
    }

    @Override
    public Integer addCustomer(Customer newCustomer) throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(newCustomer);
            HibernateHelper.commitAndClose(session);
            return newCustomer.getId();
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not add customer to db", e);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws PurchaseRefundException {
        Session session = null;
        try{
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(customer);
            HibernateHelper.commitAndClose(session);
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException("Could not update customer", e);
        }
    }

    @Override
    public void removeCustomer(int customerId) throws PurchaseRefundException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.createQuery("DELETE FROM Purchase p WHERE p.customer.id = :customerId")
                    .setInteger("customerId",customerId)
                    .executeUpdate();
            session.createQuery("DELETE FROM Refund r WHERE r.customer.id = :customerId")
                    .setInteger("customerId",customerId)
                    .executeUpdate();
            Query query = session.createQuery("DELETE FROM Customer c WHERE c.id = :id");
            query.setInteger("id", customerId).executeUpdate();
            HibernateHelper.commitAndClose(session);
        } catch (Exception e) {
            HibernateHelper.rollbackAndClose(session);
            throw new PurchaseRefundException(String.format("Could not remove customer = %s",customerId), e);
        }
    }
}

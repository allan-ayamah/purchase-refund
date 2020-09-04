package com.docomo.purchaserefund.dbconfig;

import com.docomo.purchaserefund.model.Customer;
import com.docomo.purchaserefund.model.Purchase;
import com.docomo.purchaserefund.model.Refund;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionFactoryConfig {

    @Bean
    public static SessionFactory initSessionFactory(){
        try {
            org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration();
            config.addAnnotatedClass(Customer.class);
            config.addAnnotatedClass(Purchase.class);
            config.addAnnotatedClass(Refund.class);
            SessionFactory sessionFactory = config.configure("hibernate.cfg.xml").buildSessionFactory();
            return sessionFactory;
        } catch(Throwable e){
            throw new ExceptionInInitializerError(e);
        }
    }
}

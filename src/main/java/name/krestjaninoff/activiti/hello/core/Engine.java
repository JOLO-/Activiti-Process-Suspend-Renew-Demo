package name.krestjaninoff.activiti.hello.core;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SuppressWarnings("unused")
public class Engine {

    private static Engine INSTANCE;
    private static ClassPathXmlApplicationContext applicationContext;
    private static SessionFactory hibernateSessionFactory;

    static {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        hibernateSessionFactory = (SessionFactory) applicationContext.getBean("sessionFactory");
        INSTANCE = new Engine();
    }

    private Engine() {}

    public static Engine getInstance() {
        return INSTANCE;
    }

    public synchronized ClassPathXmlApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public synchronized SessionFactory getHibernateSessionFactory() {
        return hibernateSessionFactory;
    }

    public synchronized Session getHibernateSession() throws HibernateException {
        return hibernateSessionFactory.getCurrentSession();
    }
}

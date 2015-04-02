package kosolap;

import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HibernateUtil {

    static ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(new String[] {"spring-config.xml"});
    private static final SessionFactory sessionFactory = (SessionFactory) ac.getBean("sessionFactory");
    private static ServiceRegistry serviceRegistry;


    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Чистит кеш и закрывает соединение с БД
        getSessionFactory().close();
    }

}
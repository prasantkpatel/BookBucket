package io.cs702.bookbucket.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/* Utitlity class for getting spring managed beans into non-spring
* managed classes. For eg: Utility Classes and POJOs
*/
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static <T extends Object> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

}

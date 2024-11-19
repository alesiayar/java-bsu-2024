package by.bsu.dependency.myExample;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(scope = BeanScope.PROTOTYPE)
public class PrototypeBean1 {

    public static int counter = 0;

    @Inject
    private SingletoneBean1 singletonBean;

    public void doSomething() {
        counter++;
        System.out.println("counter in protatype: " + counter);
    }

    @PostConstruct
    public void postConstruct() {
        counter++;
    }

}
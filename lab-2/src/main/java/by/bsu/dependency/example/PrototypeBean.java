package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(scope = BeanScope.PROTOTYPE)
public class PrototypeBean {

    public static int counter = 0;

    @Inject
    private SingletonBean singletonBean;

    public void doSomething() {
        counter++;
        System.out.println("counter in protatype: " + counter);
    }

    @PostConstruct
    public void postConstruct() {
        counter++;
    }

}
package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "singletonBean", scope = BeanScope.SINGLETON)
public class SingletonBean {
    int counter;

    @Inject
    private PrototypeBean prototypeBean;

    public void doSomething() {
        counter++;
        System.out.println("counter in singleton: " + counter);
    }

    @PostConstruct
    void print() {
        System.out.println("constructor was called, counter is " + counter);
    }
}
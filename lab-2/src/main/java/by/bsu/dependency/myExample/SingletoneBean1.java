package by.bsu.dependency.myExample;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "singletoneBean1", scope = BeanScope.SINGLETON)
public class SingletoneBean1 {
    int counter;

    @Inject
    private PrototypeBean1 prototypeBean;

    public void doSomething() {
        counter++;
        System.out.println("counter in singleton: " + counter);
    }

    @PostConstruct
    void print() {
        System.out.println("constructor was called, counter is " + counter);
    }
}
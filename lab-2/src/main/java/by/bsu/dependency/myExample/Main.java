package by.bsu.dependency.myExample;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.AutoScanApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AutoScanApplicationContext("by.bsu.dependency.myExample");

        applicationContext.start();
        SingletoneBean1 singletonBean = (SingletoneBean1) applicationContext.getBean("singletoneBean1");
        PrototypeBean1 prototypeBean = (PrototypeBean1) applicationContext.getBean("prototypeBean1");

        singletonBean.doSomething();
        prototypeBean.doSomething();
        singletonBean = (SingletoneBean1) applicationContext.getBean("singletoneBean1");
        prototypeBean = (PrototypeBean1) applicationContext.getBean("prototypeBean1");
        singletonBean.doSomething();
        prototypeBean.doSomething();
    }
}
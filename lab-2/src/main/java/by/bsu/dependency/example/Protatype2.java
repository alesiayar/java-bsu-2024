package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "protatype2", scope = BeanScope.PROTOTYPE)
public class Protatype2 {
    @Inject
    Protatype1 protatype1;
}
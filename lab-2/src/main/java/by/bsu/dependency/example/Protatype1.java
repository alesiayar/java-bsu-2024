package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "protatype1", scope = BeanScope.PROTOTYPE)
public class Protatype1 {
    @Inject
    by.bsu.dependency.example.Protatype2 protatype2;
}
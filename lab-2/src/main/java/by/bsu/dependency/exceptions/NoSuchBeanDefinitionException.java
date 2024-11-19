package by.bsu.dependency.exceptions;

public class NoSuchBeanDefinitionException extends RuntimeException {
    public NoSuchBeanDefinitionException() {
        super("No such bean definition.");
    }
}

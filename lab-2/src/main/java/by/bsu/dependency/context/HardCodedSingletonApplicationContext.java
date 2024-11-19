package by.bsu.dependency.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;


public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {
    /**
     * ! Класс существует только для базового примера !
     * <br/>
     * Создает контекст, содержащий классы, переданные в параметре. Полагается на отсутсвие зависимостей в бинах,
     * а также на наличие аннотации {@code @Bean} на переданных классах.
     * <br/>
     * ! Контекст данного типа не занимается внедрением зависимостей !
     * <br/>
     * ! Создает только бины со скоупом {@code SINGLETON} !
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) {
        beanDefinitions = Arrays.stream(beanClasses).collect(
                Collectors.toMap(
                        beanClass -> beanClass.getAnnotation(Bean.class).name(),
                        Function.identity()
                )
        );
    }

    @Override
    public void start() {
        isStarted = ContextStatus.STARTED;
        beanDefinitions.forEach((beanName, beanClass) -> beansSingletones.put(beanName, instantiateBean(beanClass)));

        beansSingletones.forEach((beanName, object) -> inject(object));
    }

    @Override
    public Object getBean(String name) {
        if (isStarted == ContextStatus.NOT_STARTED)  {
            throw new ApplicationContextNotStartedException();
        }
        var temp = beansSingletones.get(name);
        if (temp == null) {
            throw new NoSuchBeanDefinitionException();
        }
        return temp;
    }

    @Override
    public boolean isSingleton(String name) {
        if (!this.beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return true;
    }

    @Override
    public boolean isPrototype(String name) {
        if (!this.beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return false;
    }
}

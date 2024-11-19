package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected ContextStatus isStarted = ContextStatus.NOT_STARTED;
    protected Map<String, Class<?>> beanDefinitions;
    protected Map<String, Object> beansSingletones = new HashMap<>();

    protected void inject(Object object) {
        Arrays.stream(object.getClass().getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    field.set(object, getBean(field.getType()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    protected <T> void postConstructInvoke(T object) {
        Arrays.stream(object.getClass().getDeclaredMethods()).forEach(method -> {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    method.invoke(object);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        if (isStarted == ContextStatus.NOT_STARTED)  {
            throw new ApplicationContextNotStartedException();
        }
        boolean flagCastException = false;
        for (Map.Entry<String, Class<?>> entry : beanDefinitions.entrySet()) {
            if (entry.getValue().equals(clazz)) {
                try {
                    if (isPrototype(entry.getKey())) {
                        return createProtatypeObject(clazz.getName(), clazz, true);
                    }
                    var obj = beansSingletones.get(entry.getKey());
                    if (obj != null) {
                        return clazz.cast(obj);
                    }
                    flagCastException = true;
                } catch (ClassCastException e) {
                    flagCastException = true;
                }
            }
        }
        if (flagCastException) {
            return null;
        }
        throw new NoSuchBeanDefinitionException();
    }

    protected <T> T createProtatypeObject(String name, Class<T> clazz, boolean isInvoked) {
        T object;
        try {
            object = clazz.getConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        inject(object);
        beanDefinitions.put(name, object.getClass());
        if (!isInvoked) {
            postConstructInvoke(object);
        }
        return object;
    }

    @Override
    public Object getBean(String name) {
        if (isStarted == ContextStatus.NOT_STARTED)  {
            throw new ApplicationContextNotStartedException();
        }
        if (isPrototype(name)) {
            return createProtatypeObject(name, beanDefinitions.get(name), false);
        }
        else if (!beansSingletones.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return beansSingletones.get(name);
    }

    @Override
    public boolean isRunning() {
        return isStarted == ContextStatus.STARTED;
    }

    @Override
    public boolean isPrototype(String name) {
        return !isSingleton(name);
    }

    @Override
    public boolean isSingleton(String name) {
        if (!this.beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        var temp = beanDefinitions.get(name).getAnnotation(Bean.class).scope();
        if (temp == null) {
            return true;
        }
        return temp == BeanScope.SINGLETON;
    }

    @Override
    public boolean containsBean(String name) {
        if (isStarted == ContextStatus.NOT_STARTED)  {
            throw new ApplicationContextNotStartedException();
        }
        return beanDefinitions.containsKey(name);
    }

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    public <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        isStarted = ContextStatus.STARTED;
        beanDefinitions.forEach((beanName, beanClass) ->  {
            if (isSingleton(beanName)) {
                try {
                    beansSingletones.put(beanName, instantiateBean(beanClass));
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        beansSingletones.forEach((beanName, object) -> inject(object));
        beansSingletones.forEach((beanName, object) -> postConstructInvoke(object));
    }

}

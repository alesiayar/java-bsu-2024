package by.bsu.dependency.context;

import by.bsu.dependency.example.*;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SimpleApplicationContextTest {

    private SimpleApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class, PrototypeBean.class, SingletonBean.class);
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testPostConstructCalled() {
        applicationContext.start();
        assertThat(((FirstBean) applicationContext.getBean("firstBean")).isPostConstructCalled).isTrue();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    @Test
    void testPrototype() {
        applicationContext.start();

        assertNotEquals(
                applicationContext.getBean("prototypeBean"),
                applicationContext.getBean("prototypeBean")
        );
    }

    @Test
    void testPrototypePostConstruct() {
        applicationContext.start();

        assertEquals(PrototypeBean.counter, 0);

        applicationContext.getBean("prototypeBean");
        applicationContext.getBean("prototypeBean");

        assertEquals(PrototypeBean.counter, 2);
    }

    @Test
    void testBeanNameWithoutAnnotation() {
        applicationContext.start();
        OtherBean otherBean = (OtherBean) applicationContext.getBean("otherBean");
        assertNotNull(otherBean, "Bean should be created without @Bean annotation");
    }

    @Test
    void testInitiate() {
        assertNotNull(applicationContext.instantiateBean(FirstBean.class));
        assertNotNull(applicationContext.instantiateBean(PrototypeBean.class));
    }

    @Test
    void testInject() throws NoSuchFieldException, IllegalAccessException {
        applicationContext.start();
        PrototypeBean prototypeBean = (PrototypeBean) applicationContext.getBean("prototypeBean");
        var singletonBeanField = prototypeBean.getClass().getDeclaredField("singletonBean");
        singletonBeanField.setAccessible(true);
        Object injectedSingletonBean = singletonBeanField.get(prototypeBean);
        assertThat(injectedSingletonBean).isNotNull();
        assertThat(injectedSingletonBean).isEqualTo(applicationContext.getBean(SingletonBean.class));
    }

    @Test
    void TestRecursiveInject() {
        applicationContext = new SimpleApplicationContext(Protatype1.class, Protatype2.class);
        applicationContext.start();
        assertThrows(
                StackOverflowError.class,
                () -> applicationContext.getBean("protatype2")
        );
    }


}
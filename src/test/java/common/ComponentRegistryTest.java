package common;

import com.stablest.web_crawler.common.ComponentRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComponentRegistryTest {
    private ComponentRegistry componentRegistry;

    @BeforeEach
    void setUp() {
        componentRegistry = new ComponentRegistry();
    }

    @Test
    void givenIsFrozen_whenRegister_thenThrowsIllegalStateException() {
        componentRegistry.freeze();

        Assertions.assertThrows(IllegalStateException.class, () -> componentRegistry.register(Object.class, new Object()));
    }

    @Test
    void givenIsNotFrozen_whenRegister_thenStoresInMap() {
        Object object = new Object();

        componentRegistry.register(Object.class, object);
        Object response = componentRegistry.get(Object.class);

        Assertions.assertEquals(object, response);
    }

    @Test
    void givenComponentNotRegistered_whenGet_thenReturnsNull() {
        String result = componentRegistry.get(String.class);

        Assertions.assertNull(result);
    }

    @Test
    void givenRegisteredComponent_whenGet_thenReturnsInstance() {
        String expected = "test";

        componentRegistry.register(String.class, expected);
        String result = componentRegistry.get(String.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void givenMultipleComponents_whenGetEach_thenCorrectInstanceReturned() {
        String stringComponent = "";
        Integer intComponent = 0;

        componentRegistry.register(String.class, stringComponent);
        componentRegistry.register(Integer.class, intComponent);

        Assertions.assertEquals(stringComponent, componentRegistry.get(String.class));
        Assertions.assertEquals(intComponent, componentRegistry.get(Integer.class));
    }
}

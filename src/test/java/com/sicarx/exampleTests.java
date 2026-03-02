package com.sicarx;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite de Pruebas para Jenkins")
public class exampleTests {

    private String testValue;

    @BeforeEach
    void setUp() {
        testValue = "TestValue";
    }

    @Test
    @DisplayName("Prueba 1: Validar igualdad de strings")
    void exampleTest() {
        //Arrange
        String expected = "Hello World";

        //Act
        String actual = "Hello World";

        //Assert
        assertEquals(expected, actual, "Expected and actual values should be the same");
    }

    @Test
    @DisplayName("Prueba 2: Validar suma de números")
    void testSuma() {
        //Arrange
        int num1 = 10;
        int num2 = 20;
        int expected = 30;

        //Act
        int result = num1 + num2;

        //Assert
        assertEquals(expected, result, "La suma debe ser correcta");
    }

    @Test
    @DisplayName("Prueba 3: Validar que string no es null")
    void testStringNotNull() {
        //Arrange
        String value = "jenkins-test";

        //Assert
        assertNotNull(value, "El string no debe ser null");
    }

    @Test
    @DisplayName("Prueba 4: Validar que lista contiene elemento")
    void testListContains() {
        //Arrange
        java.util.List<String> items = java.util.Arrays.asList("item1", "item2", "item3");

        //Assert
        assertTrue(items.contains("item2"), "La lista debe contener item2");
    }

    @Test
    @DisplayName("Prueba 5: Validar que número es mayor a cero")
    void testPositiveNumber() {
        //Arrange
        int number = 42;

        //Assert
        assertTrue(number > 0, "El número debe ser mayor a cero");
    }

    @Test
    @DisplayName("Prueba 6: Validar operación de multiplicación")
    void testMultiplicacion() {
        //Arrange
        int factor1 = 5;
        int factor2 = 4;
        int expected = 20;

        //Act
        int result = factor1 * factor2;

        //Assert
        assertEquals(expected, result, "La multiplicación debe ser correcta");
    }

    @Test
    @DisplayName("Prueba 7: Validar valor setup")
    void testSetupValue() {
        //Assert
        assertEquals("TestValue", testValue, "El valor setup debe ser correcto");
    }

    @Test
    @DisplayName("Prueba 8: Validar que lista no está vacía")
    void testListNotEmpty() {
        //Arrange
        java.util.List<Integer> numbers = java.util.Arrays.asList(1, 2, 3, 4, 5);

        //Assert
        assertFalse(numbers.isEmpty(), "La lista no debe estar vacía");
    }

    @Test
    @DisplayName("Prueba 9: Validar boolean verdadero")
    void testBooleanTrue() {
        //Arrange
        boolean condition = true;

        //Assert
        assertTrue(condition, "La condición debe ser verdadera");
    }

    @Test
    @DisplayName("Prueba 10: Validar comparación de objetos")
    void testObjectComparison() {
        //Arrange
        String obj1 = new String("test");
        String obj2 = "test";

        //Assert
        assertEquals(obj1, obj2, "Los objetos deben ser iguales");
    }

}


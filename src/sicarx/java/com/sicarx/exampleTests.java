package com.sicarx;


import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class exampleTests {

        @Test
        void exampleTest() {
            //Arrange
            String expected = "Hello World";

            //Act
            String actual = "Hello World";

            //Assert
            assertEquals(expected, actual, "Expected and actual values should be the same");
        }
}

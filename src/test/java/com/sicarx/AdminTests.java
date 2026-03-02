package com.sicarx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.Feature;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite de Pruebas de Administración")
@Feature("Administración del Sistema")
public class AdminTests {

    private int balance;
    private String username;

    @BeforeEach
    void setUp() {
        balance = 1000;
        username = "admin_user";
    }

    @Nested
    @DisplayName("Pruebas de Balance de Usuario")
    @Story("Gestión de Balance")
    class BalanceTests {

        @Test
        @DisplayName("Test 1: Balance inicial debe ser 1000")
        @Description("Verificar que el balance inicial es correcto")
        @Severity(SeverityLevel.BLOCKER)
        void testInitialBalance() {
            assertEquals(1000, balance, "El balance inicial debe ser 1000");
        }

        @Test
        @DisplayName("Test 2: Depositar dinero aumenta balance")
        @Description("Verificar que al depositar dinero el balance aumenta correctamente")
        @Severity(SeverityLevel.CRITICAL)
        void testDepositMoney() {
            // Arrange
            int initialBalance = balance;
            int depositAmount = 500;
            int expectedBalance = initialBalance + depositAmount;

            // Act
            balance += depositAmount;

            // Assert
            assertEquals(expectedBalance, balance, "El balance después del depósito debe ser correcto");
        }

        @Test
        @DisplayName("Test 3: Retirar dinero reduce balance (FALLA ESPERADA)")
        @Description("Este test está diseñado para fallar y visualizar en Allure Reports")
        @Severity(SeverityLevel.CRITICAL)
        void testWithdrawMoney_Fail() {
            // Arrange
            int withdrawAmount = 300;
            int expectedBalance = 700;

            // Act
            balance -= withdrawAmount;

            // Assert - ESTA LÍNEA HACE QUE FALLE
            assertEquals(expectedBalance + 100, balance, "Este test falla intencionalmente para demostrar fallos en Allure");
        }

        @Test
        @DisplayName("Test 4: Balance no puede ser negativo")
        @Description("Verificar que el balance no puede ser negativo")
        @Severity(SeverityLevel.NORMAL)
        void testNegativeBalance() {
            // Arrange
            int withdrawAmount = 2000;
            int initialBalance = balance;

            // Act
            if (initialBalance - withdrawAmount < 0) {
                balance = initialBalance; // No permitir balance negativo
            } else {
                balance -= withdrawAmount;
            }

            // Assert
            assertTrue(balance >= 0, "El balance nunca debe ser negativo");
        }
    }

    @Nested
    @DisplayName("Pruebas de Validación de Usuario")
    @Story("Gestión de Usuarios")
    class UserValidationTests {

        @Test
        @DisplayName("Test 5: Username debe ser válido")
        @Description("Verificar que el username es válido")
        @Severity(SeverityLevel.CRITICAL)
        void testValidUsername() {
            assertNotNull(username, "El username no debe ser null");
            assertTrue(username.length() > 0, "El username debe tener al menos un carácter");
        }

        @Test
        @DisplayName("Test 6: Username contiene 'admin' (FALLA ESPERADA)")
        @Description("Este test falla intencionalmente para demostrar fallos")
        @Severity(SeverityLevel.NORMAL)
        void testAdminUsername_Fail() {
            // Este test fallará porque username es "admin_user" pero la aserción es incorrecta
            assertTrue(username.equals("root"), "Username debe ser 'root'");
        }

        @Test
        @DisplayName("Test 7: Username no está vacío")
        @Description("Verificar que el username no está vacío")
        @Severity(SeverityLevel.NORMAL)
        void testUsernameNotEmpty() {
            assertFalse(username.isEmpty(), "El username no debe estar vacío");
        }

        @Test
        @DisplayName("Test 8: Username tiene longitud mínima")
        @Description("Verificar que el username tiene una longitud mínima de 5 caracteres")
        @Severity(SeverityLevel.MINOR)
        void testUsernameMinLength() {
            assertTrue(username.length() >= 5, "El username debe tener al menos 5 caracteres");
        }
    }

    @Nested
    @DisplayName("Pruebas de Operaciones Especiales")
    @Story("Operaciones Críticas")
    class SpecialOperationsTests {

        @Test
        @DisplayName("Test 9: Cálculo de interés (FALLA ESPERADA)")
        @Description("Cálculo de interés que falla intencionalmente")
        @Severity(SeverityLevel.NORMAL)
        void testInterestCalculation_Fail() {
            // Arrange
            double rate = 0.05; // 5% de interés
            double expectedInterest = balance * rate;
            double actualInterest = 100; // Valor incorrecto

            // Assert - FALLA ESPERADA
            assertEquals(expectedInterest, actualInterest, "El cálculo de interés es incorrecto");
        }

        @Test
        @DisplayName("Test 10: Transferencia válida entre cuentas")
        @Description("Verificar que una transferencia válida funciona correctamente")
        @Severity(SeverityLevel.CRITICAL)
        void testValidTransfer() {
            // Arrange
            int transferAmount = 250;
            int senderBalance = balance;
            int receiverBalance = 500;

            // Act
            senderBalance -= transferAmount;
            receiverBalance += transferAmount;

            // Assert
            assertEquals(balance - transferAmount, senderBalance, "El balance del remitente debe disminuir");
            assertEquals(750, receiverBalance, "El balance del receptor debe aumentar");
        }

        @Test
        @DisplayName("Test 11: Validar múltiples transacciones")
        @Description("Ejecutar múltiples transacciones y validar el resultado final")
        @Severity(SeverityLevel.CRITICAL)
        void testMultipleTransactions() {
            // Arrange
            int[] transactions = {-100, 50, -200, 150};
            int expectedFinalBalance = balance;

            // Act
            for (int transaction : transactions) {
                expectedFinalBalance += transaction;
            }

            // Assert
            assertEquals(900, expectedFinalBalance, "El balance final después de múltiples transacciones debe ser correcto");
        }

        @Test
        @DisplayName("Test 12: Validar límite de transacción (FALLA ESPERADA)")
        @Description("Test que falla para demostrar validación de límites")
        @Severity(SeverityLevel.NORMAL)
        void testTransactionLimit_Fail() {
            // Arrange
            int maxTransactionLimit = 500;
            int transactionAmount = 600;

            // Act & Assert - FALLA ESPERADA
            assertTrue(transactionAmount <= maxTransactionLimit, "La transacción excede el límite permitido");
        }
    }

    @Nested
    @DisplayName("Pruebas de Seguridad")
    @Story("Validaciones de Seguridad")
    class SecurityTests {

        @Test
        @DisplayName("Test 13: Password debe cumplir requisitos")
        @Description("Validar que la contraseña cumple con los requisitos de seguridad")
        @Severity(SeverityLevel.BLOCKER)
        void testPasswordRequirements() {
            // Arrange
            String password = "SecurePass123!";
            boolean hasUpperCase = password.matches(".*[A-Z].*");
            boolean hasLowerCase = password.matches(".*[a-z].*");
            boolean hasNumbers = password.matches(".*[0-9].*");
            boolean hasSpecialChars = password.matches(".*[!@#$%^&*].*");
            boolean hasMinimumLength = password.length() >= 12;

            // Assert
            assertTrue(hasUpperCase, "La contraseña debe contener mayúsculas");
            assertTrue(hasLowerCase, "La contraseña debe contener minúsculas");
            assertTrue(hasNumbers, "La contraseña debe contener números");
            assertTrue(hasSpecialChars, "La contraseña debe contener caracteres especiales");
            assertTrue(hasMinimumLength, "La contraseña debe tener al menos 12 caracteres");
        }

        @Test
        @DisplayName("Test 14: Email debe ser válido (FALLA ESPERADA)")
        @Description("Validación de email con formato inválido para demostrar fallos")
        @Severity(SeverityLevel.NORMAL)
        void testEmailValidation_Fail() {
            // Arrange
            String email = "admin@company.com";

            // Assert - FALLA ESPERADA (validación incorrecta)
            assertTrue(email.matches("^[A-Z]+@[A-Z]+\\.[A-Z]+$"), "Email debe estar en formato válido (incorrecto propósito)");
        }

        @Test
        @DisplayName("Test 15: Verificar acceso de administrador")
        @Description("Verificar que el usuario tiene permisos de administrador")
        @Severity(SeverityLevel.BLOCKER)
        void testAdminAccess() {
            // Arrange
            boolean isAdmin = username.startsWith("admin");

            // Assert
            assertTrue(isAdmin, "El usuario debe ser administrador");
        }
    }
}








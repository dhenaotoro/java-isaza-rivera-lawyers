package com.isazariveralawyers.api.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de utilidades de teléfono")
class PhoneUtilsTest {

    @Test
    @DisplayName("Debe convertir número colombiano sin indicativo a formato E.164")
    void testToE164_WithoutCountryCode() {
        // Arrange
        String phone = "3001234567";

        // Act
        String result = PhoneUtils.toE164(phone);

        // Assert
        assertEquals("+573001234567", result);
    }

    @Test
    @DisplayName("Debe mantener número que ya está en formato E.164")
    void testToE164_AlreadyE164() {
        // Arrange
        String phone = "+573001234567";

        // Act
        String result = PhoneUtils.toE164(phone);

        // Assert
        assertEquals("+573001234567", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"3001234567", "3151234567", "3171234567", "3181234567"})
    @DisplayName("Debe convertir diversos números móviles colombianos")
    void testToE164_VariousMobileNumbers(String phone) {
        // Act
        String result = PhoneUtils.toE164(phone);

        // Assert
        assertTrue(result.startsWith("+57"));
        assertTrue(result.length() == 13); // +57 (2 caracteres) + 10 dígitos
    }

    @Test
    @DisplayName("Debe limpiar espacios y caracteres especiales")
    void testToE164_WithSpacesAndSpecialChars() {
        // Arrange
        String phone = "300 123 4567";

        // Act
        String result = PhoneUtils.toE164(phone);

        // Assert
        assertTrue(result.startsWith("+57"));
        assertTrue(result.matches("^\\+\\d+$")); // Solo + y dígitos
    }

    @Test
    @DisplayName("Debe manejar número con indicativo de país 57 sin +")
    void testToE164_WithCountryCodeWithout57Plus() {
        // Arrange
        String phone = "573001234567";

        // Act
        String result = PhoneUtils.toE164(phone);

        // Assert
        assertEquals("+573001234567", result);
    }

    @Test
    @DisplayName("Debe manejar número que comienza con 0")
    void testToE164_WithLeadingZero() {
        // Arrange
        String phone = "03001234567";

        // Act
        String result = PhoneUtils.toE164(phone);

        // Assert
        assertEquals("+573001234567", result);
    }
}

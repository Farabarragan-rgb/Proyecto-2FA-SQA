import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ValidadorAutenticacionTest {

    private ValidadorAutenticacion validador;

    @BeforeEach
    public void setUp() {
        validador = new ValidadorAutenticacion();
    }

    @Test
    @DisplayName("CP-01 y CP-03: Validación de formato de correo y longitud de contraseña")
    public void testValidacionCredencialesNivel1() {
        assertTrue(validador.esCorreoValido("tadeo@uacm.edu.mx"));
        assertFalse(validador.esCorreoValido("correo_sin_arroba.com"));
        assertFalse(validador.esCorreoValido(""));

        assertTrue(validador.cumpleLongitudContrasena("Pass1234"));
        assertFalse(validador.cumpleLongitudContrasena("12345"));
    }

    @Test
    @DisplayName("CP-04 y CP-05: Verificación de respuestas 2FA con normalización")
    public void testVerificacionSegundoFactor() {
        assertTrue(validador.validarRespuestas2FA("  HADES ", "hades", "MoRaDo  ", "morado"));
        assertFalse(validador.validarRespuestas2FA("hades", "hades", "azul", "morado"));
    }

    @Test
    @DisplayName("CP-06: Bloqueo automático al alcanzar el límite de 3 intentos fallidos")
    public void testReglaBloqueoTresIntentos() {
        assertFalse(validador.debeBloquearCuenta(0));
        assertFalse(validador.debeBloquearCuenta(2));
        assertTrue(validador.debeBloquearCuenta(3));
    }
}
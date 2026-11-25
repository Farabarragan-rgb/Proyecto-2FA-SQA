import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

    @Test
    @DisplayName("MOD-01: Inicialización correcta de entidad Usuario y cambio de estado")
    public void testCreacionYEstadoUsuario() {
        Usuario u = new Usuario(1, "tadeo@uacm.edu.mx", "Pass1234");

        assertEquals(1, u.getIdUsuario());
        assertEquals("tadeo@uacm.edu.mx", u.getNombreUsuario());
        assertEquals("Pass1234", u.getContrasenaHash());
        assertEquals(0, u.getIntentosFallidos());
        assertEquals("ACTIVA", u.getEstadoCuenta());

        u.setIntentosFallidos(3);
        u.setEstadoCuenta("BLOQUEADA");
        assertEquals(3, u.getIntentosFallidos());
        assertEquals("BLOQUEADA", u.getEstadoCuenta());
    }
}
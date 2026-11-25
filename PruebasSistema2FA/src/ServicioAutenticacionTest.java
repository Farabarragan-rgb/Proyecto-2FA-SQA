import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServicioAutenticacionTest {

    private UsuarioDAO usuarioDAO;
    private ServicioAutenticacion servicio;

    @BeforeEach
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
        servicio = new ServicioAutenticacion(usuarioDAO, new ValidadorAutenticacion());
    }

    @Test
    @DisplayName("SRV-01: Flujo completo Nivel 1 + Nivel 2 exitoso y registro en bitácora")
    public void testFlujoAutenticacionCompleto() {
        assertTrue(servicio.autenticarNivel1("tadeo@uacm.edu.mx", "Pass1234"));
        assertTrue(servicio.autenticarNivel2("tadeo@uacm.edu.mx", "Hades", "hades", "Morado", "morado"));
        assertTrue(servicio.getBitacoraEventos().contains("ACCESO_2FA_AUTORIZADO"));
    }

    @Test
    @DisplayName("SRV-02: Rechazo inmediato si la cuenta ya está bloqueada")
    public void testRechazoCuentaBloqueada() {
        Usuario u = usuarioDAO.obtenerPorNombre("bloqueo@uacm.edu.mx");
        u.setEstadoCuenta("BLOQUEADA");

        assertFalse(servicio.autenticarNivel1("bloqueo@uacm.edu.mx", "Pass1234"));
        assertTrue(servicio.getBitacoraEventos().contains("ACCESO_DENEGADO_USUARIO_O_BLOQUEO"));
    }
}
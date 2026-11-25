import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
    }

    @Test
    @DisplayName("DAO-01: Búsqueda insensible a mayúsculas y espacios")
    public void testObtenerUsuarioPorNombre() {
        Usuario u = usuarioDAO.obtenerPorNombre("  TADEO@UACM.EDU.MX ");
        assertNotNull(u);
        assertEquals(1, u.getIdUsuario());
        assertEquals("ACTIVA", u.getEstadoCuenta());
    }

    @Test
    @DisplayName("DAO-02: Usuario inexistente devuelve null")
    public void testUsuarioInexistente() {
        assertNull(usuarioDAO.obtenerPorNombre("noexiste@uacm.edu.mx"));
    }

    @Test
    @DisplayName("DAO-03: Bloqueo de cuenta en DAO al 3er fallo y reinicio de intentos")
    public void testBloqueoYReinicioIntentos() {
        Usuario u = usuarioDAO.obtenerPorNombre("bloqueo@uacm.edu.mx");
        usuarioDAO.registrarIntentoFallido(u);
        usuarioDAO.registrarIntentoFallido(u);
        assertEquals("ACTIVA", u.getEstadoCuenta());

        usuarioDAO.registrarIntentoFallido(u); // 3er fallo
        assertEquals("BLOQUEADA", u.getEstadoCuenta());
        assertEquals(3, u.getIntentosFallidos());

        usuarioDAO.reiniciarIntentos(u);
        assertEquals(0, u.getIntentosFallidos());
    }
}
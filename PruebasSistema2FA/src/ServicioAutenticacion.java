import java.util.ArrayList;
import java.util.List;

public class ServicioAutenticacion {
    private UsuarioDAO usuarioDAO;
    private ValidadorAutenticacion validador;
    private List<String> bitacoraEventos;

    public ServicioAutenticacion(UsuarioDAO usuarioDAO, ValidadorAutenticacion validador) {
        this.usuarioDAO = usuarioDAO;
        this.validador = validador;
        this.bitacoraEventos = new ArrayList<>();
    }

    public boolean autenticarNivel1(String correo, String password) {
        if (!validador.esCorreoValido(correo) || !validador.cumpleLongitudContrasena(password)) {
            bitacoraEventos.add("FORMATO_INVALIDO");
            return false;
        }
        Usuario u = usuarioDAO.obtenerPorNombre(correo);
        if (u == null || "BLOQUEADA".equals(u.getEstadoCuenta())) {
            bitacoraEventos.add("ACCESO_DENEGADO_USUARIO_O_BLOQUEO");
            return false;
        }
        if (!u.getContrasenaHash().equals(password)) {
            usuarioDAO.registrarIntentoFallido(u);
            bitacoraEventos.add("FALLO_NIVEL_1");
            return false;
        }
        bitacoraEventos.add("NIVEL_1_APROBADO");
        return true;
    }

    public boolean autenticarNivel2(String correo, String r1, String esp1, String r2, String esp2) {
        Usuario u = usuarioDAO.obtenerPorNombre(correo);
        if (u == null || "BLOQUEADA".equals(u.getEstadoCuenta())) return false;

        if (validador.validarRespuestas2FA(r1, esp1, r2, esp2)) {
            usuarioDAO.reiniciarIntentos(u);
            bitacoraEventos.add("ACCESO_2FA_AUTORIZADO");
            return true;
        } else {
            usuarioDAO.registrarIntentoFallido(u);
            bitacoraEventos.add("FALLO_NIVEL_2");
            return false;
        }
    }

    public List<String> getBitacoraEventos() {
        return bitacoraEventos;
    }
}
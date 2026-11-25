import java.util.HashMap;
import java.util.Map;

public class UsuarioDAO {
    private Map<String, Usuario> tablaUsuarios = new HashMap<>();

    public UsuarioDAO() {
        tablaUsuarios.put("tadeo@uacm.edu.mx", new Usuario(1, "tadeo@uacm.edu.mx", "Pass1234"));
        tablaUsuarios.put("bloqueo@uacm.edu.mx", new Usuario(2, "bloqueo@uacm.edu.mx", "Pass1234"));
    }

    public Usuario obtenerPorNombre(String correo) {
        if (correo == null) return null;
        return tablaUsuarios.get(correo.trim().toLowerCase());
    }

    public void registrarIntentoFallido(Usuario u) {
        int nuevos = u.getIntentosFallidos() + 1;
        u.setIntentosFallidos(nuevos);
        if (nuevos >= ValidadorAutenticacion.MAX_INTENTOS_PERMITIDOS) {
            u.setEstadoCuenta("BLOQUEADA");
        }
    }

    public void reiniciarIntentos(Usuario u) {
        u.setIntentosFallidos(0);
    }
}
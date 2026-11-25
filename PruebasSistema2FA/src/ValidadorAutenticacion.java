public class ValidadorAutenticacion {

    public static final int MAX_INTENTOS_PERMITIDOS = 3;

    public String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim().toLowerCase();
    }

    public boolean esCorreoValido(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return correo.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    public boolean cumpleLongitudContrasena(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean validarRespuestas2FA(String r1Ingresada, String r1Esperada, String r2Ingresada, String r2Esperada) {
        return normalizarTexto(r1Ingresada).equals(normalizarTexto(r1Esperada))
            && normalizarTexto(r2Ingresada).equals(normalizarTexto(r2Esperada));
    }

    public boolean debeBloquearCuenta(int intentosFallidos) {
        return intentosFallidos >= MAX_INTENTOS_PERMITIDOS;
    }
}
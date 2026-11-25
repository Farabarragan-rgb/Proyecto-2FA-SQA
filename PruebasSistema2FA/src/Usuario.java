public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String contrasenaHash;
    private int intentosFallidos;
    private String estadoCuenta;

    public Usuario(int idUsuario, String nombreUsuario, String contrasenaHash) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash;
        this.intentosFallidos = 0;
        this.estadoCuenta = "ACTIVA";
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getContrasenaHash() { return contrasenaHash; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    public String getEstadoCuenta() { return estadoCuenta; }
    public void setEstadoCuenta(String estadoCuenta) { this.estadoCuenta = estadoCuenta; }
}
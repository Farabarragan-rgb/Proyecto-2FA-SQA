// =========================================================
// CAPA DE CONTROLADOR - Navegación y control de sesión
// =========================================================

class AutenticacionController {
    constructor() {
        this.servicio = new ServicioAutenticacion();
        this.usuarioPendiente2FA = null;
        this.usuarioAutenticado = null;
    }

    mostrarVista(idVista) {
        document.getElementById("vistaLogin").style.display = "none";
        document.getElementById("vistaSegundoFactor").style.display = "none";
        document.getElementById("vistaDashboard").style.display = "none";
        document.getElementById(idVista).style.display = "block";
    }

    iniciarSesion(correo, password) {
        const usuario = this.servicio.validarNivel1(correo, password);
        this.usuarioPendiente2FA = usuario;

        const preguntas = this.servicio.obtenerPreguntasUsuario(usuario.idUsuario);
        document.getElementById("lblPregunta1").innerText = preguntas[0].textoPregunta;
        document.getElementById("lblPregunta2").innerText = preguntas[1].textoPregunta;
        document.getElementById("respuesta1").value = "";
        document.getElementById("respuesta2").value = "";
        document.getElementById("msgError2FA").style.display = "none";

        this.mostrarVista("vistaSegundoFactor");
    }

    verificarSegundoFactor(r1, r2) {
        if (!this.usuarioPendiente2FA) {
            this.mostrarVista("vistaLogin");
            return;
        }

        const resultado = this.servicio.validarRespuestas(this.usuarioPendiente2FA.idUsuario, r1, r2);

        if (resultado.autorizado) {
            this.usuarioAutenticado = this.usuarioPendiente2FA;
            this.usuarioPendiente2FA = null;
            document.getElementById("lblUsuarioActivo").innerText = this.usuarioAutenticado.nombreUsuario;
            this.mostrarVista("vistaDashboard");
        } else if (resultado.bloqueada) {
            this.usuarioPendiente2FA = null;
            this.mostrarVista("vistaLogin");
            const msgError = document.getElementById("msgError");
            msgError.innerText = "Cuenta bloqueada temporalmente por exceder 3 intentos fallidos.";
            msgError.style.display = "block";
        } else {
            throw new Error(`Respuestas de seguridad incorrectas. Intento ${resultado.intentos} de ${MAX_INTENTOS_PERMITIDOS}.`);
        }
    }

    cerrarSesion() {
        this.usuarioAutenticado = null;
        this.usuarioPendiente2FA = null;
        document.getElementById("correo").value = "";
        document.getElementById("password").value = "";
        document.getElementById("msgError").style.display = "none";

        this.mostrarVista("vistaLogin");
        const msgSuccess = document.getElementById("msgSuccess");
        msgSuccess.innerText = "Sesión cerrada correctamente.";
        msgSuccess.style.display = "block";
    }
}
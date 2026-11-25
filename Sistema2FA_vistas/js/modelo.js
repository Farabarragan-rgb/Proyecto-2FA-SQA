// =========================================================
// CAPA DE MODELO - Reglas de negocio 2FA
// =========================================================

const MAX_INTENTOS_PERMITIDOS = 3;

class ValidadorEntrada {
    static normalizarTexto(texto) {
        if (!texto) return "";
        return texto.trim().toLowerCase();
    }

    static esCampoVacio(valor) {
        return !valor || valor.trim() === "";
    }

    static esCorreoValido(correo) {
        const regexCorreo = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regexCorreo.test(correo);
    }
}

class ServicioAutenticacion {
    constructor() {
        this.usuarioDAO = new UsuarioDAO();
        this.preguntaDAO = new PreguntaSeguridadDAO();
        this.bitacoraDAO = new BitacoraAccesoDAO();
    }

    validarNivel1(correo, password) {
        if (ValidadorEntrada.esCampoVacio(correo) || ValidadorEntrada.esCampoVacio(password)) {
            throw new Error("ERROR: Todos los campos son obligatorios.");
        }
        if (!ValidadorEntrada.esCorreoValido(correo.trim())) {
            throw new Error('ERROR: El correo debe contener "@" y "." válidos.');
        }
        if (password.length < 8) {
            throw new Error("ERROR: La contraseña debe contener mínimo 8 caracteres.");
        }

        const usuario = this.usuarioDAO.obtenerPorNombre(correo.trim());
        if (!usuario) {
            this.bitacoraDAO.registrarEvento(0, "INTENTO_USUARIO_INEXISTENTE");
            throw new Error("Usuario o contraseña son inválidos.");
        }

        if (usuario.estadoCuenta === "BLOQUEADA") {
            this.bitacoraDAO.registrarEvento(usuario.idUsuario, "INTENTO_CUENTA_BLOQUEADA");
            throw new Error("Cuenta bloqueada temporalmente por exceder 3 intentos fallidos.");
        }

        if (usuario.contrasenaHash !== password) {
            this.registrarFallo(usuario);
            if (usuario.estadoCuenta === "BLOQUEADA") {
                throw new Error("Cuenta bloqueada temporalmente por exceder 3 intentos fallidos.");
            }
            throw new Error(`Usuario o contraseña son inválidos. Intento ${usuario.intentosFallidos} de ${MAX_INTENTOS_PERMITIDOS}.`);
        }

        this.bitacoraDAO.registrarEvento(usuario.idUsuario, "NIVEL_1_APROBADO");
        return usuario;
    }

    obtenerPreguntasUsuario(idUsuario) {
        return this.preguntaDAO.obtenerPreguntasPorUsuario(idUsuario);
    }

    validarRespuestas(idUsuario, r1, r2) {
        if (ValidadorEntrada.esCampoVacio(r1) || ValidadorEntrada.esCampoVacio(r2)) {
            throw new Error("ERROR: Debes responder ambas preguntas de seguridad.");
        }

        const usuarios = JSON.parse(localStorage.getItem("bd_usuarios"));
        const usuario = usuarios.find(u => u.idUsuario === idUsuario);
        const preguntas = this.preguntaDAO.obtenerPreguntasPorUsuario(idUsuario);

        const resp1 = ValidadorEntrada.normalizarTexto(r1);
        const resp2 = ValidadorEntrada.normalizarTexto(r2);
        const esp1 = ValidadorEntrada.normalizarTexto(preguntas[0].respuestaHash);
        const esp2 = ValidadorEntrada.normalizarTexto(preguntas[1].respuestaHash);

        if (resp1 === esp1 && resp2 === esp2) {
            this.usuarioDAO.reiniciarIntentos(usuario.idUsuario);
            this.bitacoraDAO.registrarEvento(usuario.idUsuario, "ACCESO_2FA_AUTORIZADO");
            return { autorizado: true };
        } else {
            this.registrarFallo(usuario);
            return {
                autorizado: false,
                bloqueada: usuario.estadoCuenta === "BLOQUEADA",
                intentos: usuario.intentosFallidos
            };
        }
    }

    registrarFallo(usuario) {
        const nuevosIntentos = usuario.intentosFallidos + 1;
        usuario.intentosFallidos = nuevosIntentos;
        this.usuarioDAO.actualizarIntentos(usuario.idUsuario, nuevosIntentos);

        if (nuevosIntentos >= MAX_INTENTOS_PERMITIDOS) {
            usuario.estadoCuenta = "BLOQUEADA";
            this.usuarioDAO.bloquearCuenta(usuario.idUsuario);
            this.bitacoraDAO.registrarEvento(usuario.idUsuario, "CUENTA_BLOQUEADA_3_INTENTOS");
        } else {
            this.bitacoraDAO.registrarEvento(usuario.idUsuario, `FALLO_AUTENTICACION_${nuevosIntentos}`);
        }
    }
}
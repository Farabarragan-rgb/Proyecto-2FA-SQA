// =========================================================
// CAPA DE PERSISTENCIA (DAO) - Base de datos simulada
// =========================================================

class ConexionBD {
    static inicializarDatos() {
        if (!localStorage.getItem("bd_usuarios")) {
            const usuariosIniciales = [
                {
                    idUsuario: 1,
                    nombreUsuario: "tadeo@uacm.edu.mx",
                    contrasenaHash: "Pass1234",
                    intentosFallidos: 0,
                    estadoCuenta: "ACTIVA"
                },
                {
                    idUsuario: 2,
                    nombreUsuario: "prueba.bloqueo@uacm.edu.mx",
                    contrasenaHash: "Pass1234",
                    intentosFallidos: 0,
                    estadoCuenta: "ACTIVA"
                }
            ];
            localStorage.setItem("bd_usuarios", JSON.stringify(usuariosIniciales));
        }

        if (!localStorage.getItem("bd_preguntas")) {
            const preguntasIniciales = [
                { idPregunta: 1, idUsuario: 1, textoPregunta: "¿Nombre de tu primera mascota?", respuestaHash: "hades" },
                { idPregunta: 2, idUsuario: 1, textoPregunta: "¿Cuál es tu color favorito?", respuestaHash: "morado" },
                { idPregunta: 3, idUsuario: 2, textoPregunta: "¿Nombre de tu primera mascota?", respuestaHash: "luna" },
                { idPregunta: 4, idUsuario: 2, textoPregunta: "¿Cuál es tu color favorito?", respuestaHash: "azul" }
            ];
            localStorage.setItem("bd_preguntas", JSON.stringify(preguntasIniciales));
        }

        if (!localStorage.getItem("bd_bitacora")) {
            localStorage.setItem("bd_bitacora", JSON.stringify([]));
        }
    }

    static reiniciarBD() {
        localStorage.removeItem("bd_usuarios");
        localStorage.removeItem("bd_preguntas");
        localStorage.removeItem("bd_bitacora");
        sessionStorage.clear();
        this.inicializarDatos();
    }
}

class UsuarioDAO {
    obtenerPorNombre(nombreUsuario) {
        ConexionBD.inicializarDatos();
        const usuarios = JSON.parse(localStorage.getItem("bd_usuarios"));
        return usuarios.find(u => u.nombreUsuario.toLowerCase() === nombreUsuario.toLowerCase()) || null;
    }

    actualizarIntentos(idUsuario, intentos) {
        const usuarios = JSON.parse(localStorage.getItem("bd_usuarios"));
        const idx = usuarios.findIndex(u => u.idUsuario === idUsuario);
        if (idx !== -1) {
            usuarios[idx].intentosFallidos = intentos;
            localStorage.setItem("bd_usuarios", JSON.stringify(usuarios));
        }
    }

    bloquearCuenta(idUsuario) {
        const usuarios = JSON.parse(localStorage.getItem("bd_usuarios"));
        const idx = usuarios.findIndex(u => u.idUsuario === idUsuario);
        if (idx !== -1) {
            usuarios[idx].estadoCuenta = "BLOQUEADA";
            localStorage.setItem("bd_usuarios", JSON.stringify(usuarios));
        }
    }

    reiniciarIntentos(idUsuario) {
        this.actualizarIntentos(idUsuario, 0);
    }
}

class PreguntaSeguridadDAO {
    obtenerPreguntasPorUsuario(idUsuario) {
        ConexionBD.inicializarDatos();
        const preguntas = JSON.parse(localStorage.getItem("bd_preguntas"));
        return preguntas.filter(p => p.idUsuario === idUsuario);
    }
}

class BitacoraAccesoDAO {
    registrarEvento(idUsuario, tipoEvento) {
        ConexionBD.inicializarDatos();
        const bitacora = JSON.parse(localStorage.getItem("bd_bitacora"));
        const registro = {
            fechaHora: new Date().toLocaleString(),
            idUsuario: idUsuario,
            tipoEvento: tipoEvento
        };
        bitacora.push(registro);
        localStorage.setItem("bd_bitacora", JSON.stringify(bitacora));
        console.log("[BITÁCORA 2FA]", registro);
    }
}
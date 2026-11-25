package com.pruebas;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Login2FATest {

    private WebDriver driver;
    // Ajusta esta ruta a la ubicación exacta donde guardaste tu login.html
private static final String RUTA_HTML = "file:///Users/tadeonavaelcaradepuerco/Documents/SQA/Sistema2FA_Vistas/login.html";
    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.get(RUTA_HTML);

        // Restablece localStorage antes de cada prueba para evitar bloqueos acumulados
        driver.findElement(By.id("btnResetBD")).click();
    }

    @Test
    @DisplayName("CP01 - Flujo completo exitoso: Login Nivel 1, 2FA, Dashboard y Cerrar Sesión")
    public void testFlujoCompletoExitoso() {
        // Nivel 1: Credenciales válidas
        driver.findElement(By.id("correo")).sendKeys("tadeo@uacm.edu.mx");
        driver.findElement(By.id("password")).sendKeys("Pass1234");
        driver.findElement(By.id("btnIngresar")).click();

        // Verificar transición a Vista 2FA
        WebElement vista2FA = driver.findElement(By.id("vistaSegundoFactor"));
        assertTrue(vista2FA.isDisplayed(), "Debe mostrarse la vista de segundo factor.");

        // Nivel 2: Preguntas de seguridad (prueba también normalización de mayúsculas/espacios)
        driver.findElement(By.id("respuesta1")).sendKeys("  Hades ");
        driver.findElement(By.id("respuesta2")).sendKeys("MORADO");
        driver.findElement(By.id("btnVerificar2FA")).click();

        // Verificar acceso al Dashboard
        WebElement vistaDashboard = driver.findElement(By.id("vistaDashboard"));
        WebElement lblUsuario = driver.findElement(By.id("lblUsuarioActivo"));
        assertTrue(vistaDashboard.isDisplayed(), "Debe mostrarse el Dashboard.");
        assertEquals("tadeo@uacm.edu.mx", lblUsuario.getText().trim());

        // Cerrar Sesión (CU-02)
        driver.findElement(By.id("btnCerrarSesion")).click();
        WebElement msgSuccess = driver.findElement(By.id("msgSuccess"));
        assertTrue(msgSuccess.isDisplayed());
        assertEquals("Sesión cerrada correctamente.", msgSuccess.getText().trim());
    }

    @Test
    @DisplayName("CP02 - Error Nivel 1: Campos obligatorios vacíos")
    public void testCamposVacios() {
        driver.findElement(By.id("btnIngresar")).click();

        WebElement msgError = driver.findElement(By.id("msgError"));
        assertTrue(msgError.isDisplayed());
        assertEquals("ERROR: Todos los campos son obligatorios.", msgError.getText().trim());
    }

    @Test
    @DisplayName("CP03 - Error Nivel 1: Contraseña menor a 8 caracteres (Valor Límite: 7)")
    public void testPasswordCorta() {
        driver.findElement(By.id("correo")).sendKeys("tadeo@uacm.edu.mx");
        driver.findElement(By.id("password")).sendKeys("Pass123"); // 7 caracteres
        driver.findElement(By.id("btnIngresar")).click();

        WebElement msgError = driver.findElement(By.id("msgError"));
        assertTrue(msgError.isDisplayed());
        assertEquals("ERROR: La contraseña debe contener mínimo 8 caracteres.", msgError.getText().trim());
    }

    @Test
    @DisplayName("CP04 - Error Nivel 2: Respuestas de seguridad incorrectas")
    public void testRespuestas2FAIncorrectas() {
        // Pasar Nivel 1
        driver.findElement(By.id("correo")).sendKeys("tadeo@uacm.edu.mx");
        driver.findElement(By.id("password")).sendKeys("Pass1234");
        driver.findElement(By.id("btnIngresar")).click();

        // Enviar respuestas incorrectas en Nivel 2
        driver.findElement(By.id("respuesta1")).sendKeys("firulais");
        driver.findElement(By.id("respuesta2")).sendKeys("rojo");
        driver.findElement(By.id("btnVerificar2FA")).click();

        WebElement msgError2FA = driver.findElement(By.id("msgError2FA"));
        assertTrue(msgError2FA.isDisplayed());
        assertEquals("Respuestas de seguridad incorrectas. Intento 1 de 3.", msgError2FA.getText().trim());
    }

    @Test
    @DisplayName("CP05 - Bloqueo de cuenta tras 3 intentos fallidos")
    public void testBloqueoPorTresIntentos() {
        WebElement inputCorreo = driver.findElement(By.id("correo"));
        WebElement inputPass = driver.findElement(By.id("password"));
        WebElement btnIngresar = driver.findElement(By.id("btnIngresar"));

        inputCorreo.sendKeys("prueba.bloqueo@uacm.edu.mx");
        inputPass.sendKeys("ClaveErronea1");

        // Intento 1
        btnIngresar.click();
        assertEquals("Usuario o contraseña son inválidos. Intento 1 de 3.",
                driver.findElement(By.id("msgError")).getText().trim());

        // Intento 2
        btnIngresar.click();
        assertEquals("Usuario o contraseña son inválidos. Intento 2 de 3.",
                driver.findElement(By.id("msgError")).getText().trim());

        // Intento 3 -> Bloqueo automático
        btnIngresar.click();
        assertEquals("Cuenta bloqueada temporalmente por exceder 3 intentos fallidos.",
                driver.findElement(By.id("msgError")).getText().trim());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
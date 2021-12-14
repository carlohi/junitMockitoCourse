package com.example.demo.model;

import com.example.demo.exceptions.SinSaldoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta(){
        Cuenta cuenta = new Cuenta();
        cuenta.setPersona("Carlos");
        assertEquals("Carlos",cuenta.getPersona());
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setSaldo(BigDecimal.valueOf(100.00));
        assertEquals(BigDecimal.valueOf(100.00),cuenta.getSaldo());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta(){
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        assertEquals(cuenta,cuenta2);
    }

    @Test
    void testDebidoCuenta(){
        Cuenta cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertEquals(900,cuenta.getSaldo().intValue());
    }

    @Test
    void testCreditoCuenta(){
        Cuenta cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertEquals(1100,cuenta.getSaldo().intValue());
    }

    @Test
    void testDineroInsuficiente(){
        Cuenta cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        Exception exc = assertThrows(SinSaldoException.class,()->{
            cuenta.debito(new BigDecimal("2000"));
        });
        assertEquals("Dinero insuficiente",exc.getMessage());
    }

    @Test
    @DisplayName("Probando transferencia de dinero entre cuentas")
    void testTransferirDinero(){
        Cuenta cuenta1 = new Cuenta("John doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1000"));

        Banco banco = new Banco();
        banco.setNombre("Banco de Chile");
        banco.transferir(cuenta1,cuenta2,new BigDecimal("500"));
        assertEquals("2000",cuenta1.getSaldo().toPlainString());
        assertEquals("1500",cuenta2.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas(){
        Cuenta cuenta1 = new Cuenta("John doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1000"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Banco de Chile");
        banco.transferir(cuenta1,cuenta2,new BigDecimal("500"));
        assertAll(
                () -> assertEquals(2,banco.getCuentas().size()),
                () -> assertEquals("Banco de Chile",cuenta1.getBanco().getNombre(),() -> "El nombre del banco debe coincidir"),
                () -> assertEquals("Andres",banco.getCuentas().stream().filter( c -> c.getPersona().equals("Andres")).findFirst().get().getPersona()),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Andres")))
        );
    }

    @Nested
    class EnvTest{
        @Test
        @EnabledOnOs({OS.MAC, OS.LINUX})
        void testOnlyMacandLinux () {
    }

        @Test
        @EnabledOnJre({JRE.JAVA_15})
        void testOnlyJava15 () {
    }

        @Test
        void printSystemProperties () {
        Properties prop = System.getProperties();
        prop.forEach((k, v) -> System.out.println(k + " : " + v));
    }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*15.*")
        void testSystemProperty () {
    }
    }

    @ParameterizedTest
    @ValueSource(strings = {"100","200","300","534"})
    void testDebidoCuenta(String monto){
        Cuenta cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(monto));
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @Timeout(value = 5,unit = TimeUnit.SECONDS)
    void testTimeout() throws InterruptedException{
        TimeUnit.SECONDS.sleep(4);
    }


}
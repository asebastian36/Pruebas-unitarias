package org.asebastian36.junit5app.ejemplos.models;

import org.asebastian36.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Angel", new BigDecimal("10000.12345"));

        //  cuenta.setPersona("Angel");

        String esperado = "Angel";//    valor que queremos
        String real = cuenta.getPersona();//    el que pasa

        assertNotNull(real.equals("Angel"), "La cuenta no puede ser nula");


        //  viene del import static
        assertEquals(esperado, real, "El nombre de la cuenta no es el que se esperaba");// opcion 1
        assertTrue(real.equals("Angel"), "El nombre proporcionado: " + esperado +  " no coincide con el registrado: " + real );// opcion 2
    }

    @Test
    @DisplayName("Probando la funcionalidad de obtener el saldo, cumpliendo las reglas basicas del negocio")
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Angel", new BigDecimal("10000.12345"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(10000.12345, cuenta.getSaldo().doubleValue());

        //  pruebas que se cumplen mientras el saldo sea mayor a 0
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Probando la funcionalidad de evaluar atributo a atributo al comparar dos cuentas")
    void testRefenciaCuenta() {
        //  que ambas instancias sean distintas
        Cuenta cuenta = new Cuenta("Angel", new BigDecimal("10000.12345"));
        Cuenta cuenta1 = new Cuenta("Angel", new BigDecimal("10000.12345"));

        //  assertNotEquals(cuenta, cuenta1);

        //  por valor de atributos
        assertEquals(cuenta1, cuenta);
    }

    @Test
    @DisplayName("Probando la funcion de resta de saldo o debito en las cuentas")
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Angel", new BigDecimal("10000.12345"));
        cuenta.debito(new BigDecimal(1500));

        assertNotNull(cuenta.getSaldo());
        assertEquals(8500, cuenta.getSaldo().intValue());
        assertEquals("8500.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando la funcion de deposito o credito en las cuentas")
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Angel", new BigDecimal("10000.12345"));
        cuenta.credito(new BigDecimal(1500));

        assertNotNull(cuenta.getSaldo());
        assertEquals(11500, cuenta.getSaldo().intValue());
        assertEquals("11500.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando si se lanza el error al llegar a numeros menores a cero en las cuentas")
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Angel", new BigDecimal("10000.12345"));

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(10001.12345));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";

        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Probando la funcion de credito o deposito entre cuentas, verificando las cifras tanto de resta a una cuenta, como de abono a la otra cuenta")
    void testTransferirDineroCuentas() {
        Cuenta cuentaOrigen = new Cuenta("Angel", new BigDecimal("10000.12345"));
        Cuenta cuentaDestino = new Cuenta("Paco", new BigDecimal("10000.12345"));

        Banco banco = new Banco("BBVA");
        banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal(1000));

        assertEquals("9000.12345", cuentaOrigen.getSaldo().toPlainString());
        assertEquals("11000.12345", cuentaDestino.getSaldo().toPlainString());
    }

    @Test
    //  @Disabled
    @DisplayName("Probando la condicion de que ambas cuentas pertenezcan al mismo banco para poder hacer operaciones entre ellas, ademas revisamos que ambos usuarios esten entre los usuarios esten en la lista de clientes del banco")
    void testRelacionBancoCuentas() {
        //  fail();
        Cuenta cuentaOrigen = new Cuenta("Angel", new BigDecimal("10000.12345"));
        Cuenta cuentaDestino = new Cuenta("Paco", new BigDecimal("10000.12345"));

        Banco banco = new Banco("BBVA");

        banco.addCuenta(cuentaOrigen);
        banco.addCuenta(cuentaDestino);

        assertAll(
                () -> assertEquals(2, banco.getCuentas().size(), "El banco no tiene las cuentas esperadas"),//  identificar si la lista de cuentas funciona
                () -> assertEquals("BBVA", cuentaDestino.getBanco().getNombre(), "No coincide el banco afiliado: " + cuentaDestino.getBanco().getNombre() + " con el banco que se desea hacer la operacion: " + banco.getNombre()),//  identificar si tienen ese banco
                () -> assertEquals("BBVA", cuentaOrigen.getBanco().getNombre(), "No coincide el banco afiliado: " + cuentaOrigen.getBanco().getNombre() + " con el banco que se desea hacer la operacion: " + banco.getNombre()),
                () -> {
                    //  para buscar a Angel en la lista de cuentas del banco
                    assertEquals("Angel", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Angel"))
                            .findFirst()
                            .get().getPersona());
                },
                () -> {
                    //  para buscar a Paco en la lista de cuentas del banco
                    assertEquals("Paco", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Paco"))
                            .findFirst()
                            .get().getPersona());
                },
                () -> {
                    //  para buscar si existe Angel en la lista de cuentas del banco
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Angel")));
                },
                () -> {
                    //  para buscar si existe Paco en la lista de cuentas del banco
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Paco")));
                }
        );
    }
}
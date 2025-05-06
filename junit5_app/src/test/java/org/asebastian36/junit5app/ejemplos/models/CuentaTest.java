package org.asebastian36.junit5app.ejemplos.models;

import org.asebastian36.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//  @TestInstance(TestInstance.LifeCycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo, TestReporter testReporter) {
        //  usaran esta instancia en cada prueba
        this.cuenta = new Cuenta("Angel", new BigDecimal("10000.12345"));

        this.testInfo = testInfo;
        this.testReporter = testReporter;

        testReporter.publishEntry("Iniciando Metodo.");
        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void afterEach() {
        System.out.println("Finalizando el metodo de prueba.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando las propiedades de las cuentas")
    class NombreYSaldoTest {
        @Test
        @DisplayName("Probando el nombre de la cuenta corriente")
        void testNombreCuenta() {

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
            Cuenta cuenta1 = new Cuenta("Angel", new BigDecimal("10000.12345"));

            //  assertNotEquals(cuenta, cuenta1);

            //  por valor de atributos
            assertEquals(cuenta1, cuenta);
        }
    }

    @Nested
    @DisplayName("Probando las operaciones de las cuentas")
    class OperacionesTest {

        @Tag("cuenta")
        @Test
        @DisplayName("Probando la funcion de resta de saldo o debito en las cuentas")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(1500));

            assertNotNull(cuenta.getSaldo());
            assertEquals(8500, cuenta.getSaldo().intValue());
            assertEquals("8500.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        @DisplayName("Probando la funcion de deposito o credito en las cuentas")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(1500));

            assertNotNull(cuenta.getSaldo());
            assertEquals(11500, cuenta.getSaldo().intValue());
            assertEquals("11500.12345", cuenta.getSaldo().toPlainString());
        }
    }

    @Tag("error")
    @Tag("cuenta")
    @Test
    @DisplayName("Probando si se lanza el error al llegar a numeros menores a cero en las cuentas")
    void testDineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(10001.12345));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";

        assertEquals(esperado, actual);
    }

    @Tag("cuenta")
    @Tag("banco")
    @Test
    @DisplayName("Probando la funcion de credito o deposito entre cuentas, verificando las cifras tanto de resta a una cuenta, como de abono a la otra cuenta")
    void testTransferirDineroCuentas() {
        Cuenta cuentaOrigen = cuenta;
        Cuenta cuentaDestino = new Cuenta("Paco", new BigDecimal("10000.12345"));

        Banco banco = new Banco("BBVA");
        banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal(1000));

        assertEquals("9000.12345", cuentaOrigen.getSaldo().toPlainString());
        assertEquals("11000.12345", cuentaDestino.getSaldo().toPlainString());
    }

    @Tag("banco")
    @Tag("cuenta")
    @Test
    //  @Disabled
    @DisplayName("Probando la condicion de que ambas cuentas pertenezcan al mismo banco para poder hacer operaciones entre ellas, ademas revisamos que ambos usuarios esten entre los usuarios esten en la lista de clientes del banco")
    void testRelacionBancoCuentas() {
        //  fail();
        Cuenta cuentaOrigen = cuenta;
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

    @Nested
    @DisplayName("Experimentos relacionados a la evaluacion del so")
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({ OS.MAC, OS.LINUX })
        void testSoloLinuxMac() {
        }
    }

    @Nested
    @DisplayName("Experimentos relacionados a la evaluacion del jdk")
    class JavaVersionTest {
        @Test
        @EnabledOnJre({ JRE.JAVA_11, JRE.JAVA_8 })
        void soloJdksLts() {
        }

        @Test
        @DisabledOnJre({ JRE.JAVA_12, JRE.JAVA_13, JRE.JAVA_14, JRE.JAVA_15 })
        void noNewJdk() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "java.version", matches = ".*17.*")
        void javaVersion() {
            System.out.println("Java version: " + System.getProperty("java.version"));
        }
    }

    @Nested
    @DisplayName("Probando las propiedades del sistema")
    class SystemPropertiesTest {
        @Test
        void getSystemProperties() {
            System.out.println( "Propiedades del sistema: ");
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + " : " + v));
            System.out.println();
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testPropiedadPersonalizada() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = ".*asebastian36.*")
        void testUsername() {
            System.out.println("Bienvenido: " + System.getProperty("user.name"));
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
        void solo64() {
            System.out.println("Tienes un sistema operativo 64 bits");
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void solo32() {
            System.out.println("Tienes un sistema operativo 32 bits");
        }

        @Test
        @DisplayName("Probando la funcionalidad de obtener el saldo, cumpliendo las reglas basicas del negocio, solo cuando se este en entorno Dev")
        void testSaldoCuentaDev() {

            //  se establece condicion
            boolean esDev = "dev".equals(System.getProperty("ENV"));

            //  se evalua para saber si se hace la prueba
            assumeTrue(esDev);

            assertNotNull(cuenta.getSaldo());
            assertEquals(10000.12345, cuenta.getSaldo().doubleValue());

            //  pruebas que se cumplen mientras el saldo sea mayor a 0
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("TestSaldoCuentaDev2")
        void testSaldoCuentaDev2() {

            //  se establece condicion
            boolean esDev = "dev".equals(System.getProperty("ENV"));

            //  se evalua para saber si se hace la prueba
            assumingThat(esDev, () -> {
                assertNotNull(cuenta.getSaldo());
                assertEquals(10000.12345, cuenta.getSaldo().doubleValue());

            });

            //  pruebas que se cumplen mientras el saldo sea mayor a 0
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Nested
    @DisplayName("Probando las variables de ambiente")
    class VariableAmbienteTest {

        @Test
        void getVariablesAmbiente() {
            System.out.println( "Variables de ambiente: ");
            Map<String, String> variables = System.getenv();
            variables.forEach((k, v) -> System.out.println(k + " : " + v));
            System.out.println();
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*idea.*")
        void testJavaEnv() {
            System.out.println("JAVA_HOME: " + System.getenv("JAVA_HOME"));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = ".*8.*")
        void testProcesadores() {
            System.out.println("Numero de procesadores: " + Runtime.getRuntime().availableProcessors());
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIROMENT", matches = ".*dev.*")
        void testEnv() {

        }
    }

    @DisplayName("Probando la repeticion de un metodo")
    @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testBucle(RepetitionInfo info) {
        System.out.println("Repetition info: " + info);
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest {
        @ParameterizedTest(name = "No = {index} | Valor = {0}")
        @ValueSource(strings = {"100", "200", "300", "500", "1000.12345"})
        //  @Source
        @DisplayName("Prueba parametrizada, para operacion debito")
        void testParametrizado(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "No = {index} | Valor = {0} - {argumentsWithNames}")
        @CsvSource( { "1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345" } )
        @DisplayName("Prueba parametrizada con csv, para operacion debito")
        void testParametrizadoCsv(String index, String monto) {

            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "No = {index} | Valor = {0} - {argumentsWithNames}")
        @CsvSource( { "120,100,Angel,Sebastian", "260,200,Diego,Isaac", "318,300,Maria,Elisa", "4870,500,Alma,Delia", "5999,700,Lucio,Ivan", "6999,1000.12345,Johny,Ruben" } )
        @DisplayName("Prueba parametrizada con csv2, para operacion debito")
        void testParametrizadoCsv2(String saldo, String monto, String esperado, String actual) {

            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));

            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());

            assertEquals(esperado, actual);

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "No = {index} | Valor = {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("Prueba parametrizada con csv file resource, para operacion debito")
        void testParametrizadoCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "No = {index} | Valor = {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        @DisplayName("Prueba parametrizada con csv file resource 2, para operacion debito")
        void testParametrizadoCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));

            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());

            assertEquals(esperado, actual);

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        static List<String> montoList() {
            return Arrays.asList("100", "200", "300", "500", "1000.12345");
        }

        @ParameterizedTest(name = "No = {index} | Valor = {0} - {argumentsWithNames}")
        @MethodSource("montoList")
        @DisplayName("Prueba parametrizada con method source, para operacion debito")
        void testParametrizadoMethodSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }
}
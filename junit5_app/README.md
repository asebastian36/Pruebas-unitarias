# Notas

## Dependencia

JUnit es un framework de pruebas unitarias ampliamente utilizado en Java. Su propósito es:

- **Probar código**: Permite escribir pruebas para verificar que tu código funciona como se espera.
- **Automatizar validaciones**: Las pruebas se ejecutan automáticamente, lo que facilita detectar errores rápidamente.
- **Facilitar mantenimiento**: Al tener pruebas automatizadas, es más fácil realizar cambios en el código sin romper funcionalidades existentes.

---

## Anotaciones y metodos nuevos

| Método/Annotación    | Descripción                                                                |
|----------------------|----------------------------------------------------------------------------|
| `@Test`              | Marca un método como una prueba unitaria.                                  |
| `@DisplayName`       | Proporciona un nombre descriptivo para la prueba.                          |
| `assertNotNull`      | Verifica que un objeto no sea `null`.                                      |
| `assertFalse`        | Verifica que una condición sea falsa.                                      |
| `assertTrue`         | Verifica que una condición sea verdadera.                                  |
| `assertEquals`       | Verifica que dos valores sean iguales.                                     |
| `assertAll`          | Agrupa múltiples aserciones para que se ejecuten todas, incluso si fallan. |
| `@Disabled`          | Desactiva una prueba o conjunto de pruebas temporalment.                   |
| `@Nested`            |
| `@RepeatedTest`      |
| `@ParameterizedTest` |
| `@Source`            |

---

## Anotaciones del ciclo de vida

[anotaciones ciclo de vida](./images/anotaciones-ciclo-vida.png)


### **1. Propósito general**

Estas anotaciones permiten ejecutar código en momentos específicos durante el ciclo de vida de las pruebas. Son útiles para:

- Configurar el entorno de prueba (por ejemplo, inicializar objetos o bases de datos).
- Limpiar recursos después de las pruebas (por ejemplo, cerrar conexiones o eliminar archivos temporales).
- Evitar la duplicación de código al compartir configuraciones entre múltiples pruebas.

---

### **2. Descripción de cada anotación**

#### **a) `@BeforeAll`**
- **Qué hace**:
    - Se ejecuta **una sola vez** antes de todas las pruebas de una clase.
    - Es útil para realizar configuraciones globales que se usan en todas las pruebas.

- **Requisitos**:
    - El método debe ser **estático** (`static`), ya que se ejecuta antes de instanciar la clase de prueba.

- **Ejemplo**:
  ```java
  @BeforeAll
  static void setupAll() {
      System.out.println("Esta configuración se ejecuta una vez antes de todas las pruebas.");
  }
  ```

---

#### **b) `@BeforeEach`**
- **Qué hace**:
    - Se ejecuta **antes de cada prueba** en la clase.
    - Es útil para reiniciar el estado o configurar recursos específicos para cada prueba.

- **Ejemplo**:
  ```java
  @BeforeEach
  void setupEach() {
      System.out.println("Esta configuración se ejecuta antes de cada prueba.");
  }
  ```

---

#### **c) `@AfterEach`**
- **Qué hace**:
    - Se ejecuta **después de cada prueba** en la clase.
    - Es útil para limpiar recursos o verificar el estado después de cada prueba.

- **Ejemplo**:
  ```java
  @AfterEach
  void tearDownEach() {
      System.out.println("Esta limpieza se ejecuta después de cada prueba.");
  }
  ```

---

#### **d) `@AfterAll`**
- **Qué hace**:
    - Se ejecuta **una sola vez** después de todas las pruebas de una clase.
    - Es útil para liberar recursos globales (por ejemplo, cerrar conexiones a bases de datos).

- **Requisitos**:
    - El método debe ser **estático** (`static`), ya que se ejecuta después de completar todas las pruebas.

- **Ejemplo**:
  ```java
  @AfterAll
  static void tearDownAll() {
      System.out.println("Esta limpieza se ejecuta una vez después de todas las pruebas.");
  }
  ```

---

### **3. Casos de uso comunes**

| Anotación                       | Caso de uso                                                                        |
|---------------------------------|------------------------------------------------------------------------------------|
| `@BeforeAll`                    | Inicializar una base de datos o cargar datos compartidos para todas las pruebas.   |
| `@BeforeEach`                   | Reiniciar el estado antes de cada prueba (por ejemplo, crear objetos nuevos).      |
| `@AfterEach`                    | Limpiar recursos después de cada prueba (por ejemplo, borrar archivos temporales). |
| `@AfterAll`                     | Liberar recursos globales (por ejemplo, cerrar conexiones a bases de datos).       |
| `@EnabledIfSystemProperty`      | Ejecutar pruebas basadas en propiedades del sistema (JVM).                         |
| `@EnabledIfEnvironmentVariable` | Ejecutar pruebas basadas en variables de entorno del sistema.                      |
| `@DisabledOnJre`                | Desactivar pruebas en versiones específicas de Java.                               |
| `@EnabledOnOs`                  | Ejecutar pruebas solo en sistemas operativos específicos (Windows, Linux, etc.).   |

Estas anotaciones son útiles para asegurar que las pruebas se ejecuten solo en los entornos adecuados, evitando fallos innecesarios o comportamientos inesperados. ¡Espero que esto te ayude! 😊

---

### **4. Resumen**

- **`@BeforeAll`**: Configuración global que se ejecuta una vez antes de todas las pruebas.
- **`@BeforeEach`**: Configuración específica que se ejecuta antes de cada prueba.
- **`@AfterEach`**: Limpieza que se ejecuta después de cada prueba.
- **`@AfterAll`**: Limpieza global que se ejecuta una vez después de todas las pruebas.

Estas anotaciones son fundamentales para organizar y optimizar tus pruebas unitarias, asegurando que el entorno esté correctamente configurado y limpiado en cada paso.

## **¿Qué hace `@TestInstance(TestInstance.Lifecycle.PER_CLASS)`?**

Esta anotación cambia el ciclo de vida de las pruebas en JUnit 5. Por defecto, JUnit crea una **nueva instancia de la clase de prueba para cada método de prueba** (`PER_METHOD`). Con `PER_CLASS`, se usa **una sola instancia de la clase para todas las pruebas**.

---

### **¿Para qué sirve?**

1. **Compartir estado entre pruebas**:
    - Permite que los métodos de prueba compartan datos o configuraciones en la misma instancia de la clase.

2. **Optimización**:
    - Reduce la creación de múltiples instancias si no es necesario.

3. **Compatibilidad con `@BeforeAll` y `@AfterAll` no estáticos**:
    - Con `PER_CLASS`, los métodos `@BeforeAll` y `@AfterAll` pueden ser no estáticos.

---

### **Resumen**
- **`PER_CLASS`**: Una sola instancia para todas las pruebas (comparte estado y recursos).
- **Por defecto (`PER_METHOD`)**: Una nueva instancia por cada prueba (aisla las pruebas).

## Agregar propiedad a la configuracion de la clase `CuentaTest`

se agrego la propiedad `ENV` con el valor `dev`.

> Pantallazo de como se hace

[Agregando propiedad a la configuracion](./images/configurar-propiedad.png)

> Seguna manera

[agregando propiedad a la configuracion 1](./images/configurar-propiedad-1.png)

## Explicacion de `Assumptions`

Aquí tienes un ejemplo completo de un archivo **README.md** para tu repositorio GitHub:

```markdown
# EduLynch Micro Prueba Técnica Backend

Este repositorio contiene el backend de la prueba técnica de EduLynch, desarrollado en **Spring Boot**. El sistema implementa diversas funcionalidades, como autenticación con JWT, gestión de usuarios y roles, catálogo de productos, carretilla de compras, y gestión de órdenes.

---

## Tabla de Contenidos

- [Características](#características)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Configuración e Instalación](#configuración-e-instalación)
- [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
- [Endpoints Principales](#endpoints-principales)
- [Mejoras y Backlog](#mejoras-y-backlog)
- [Licencia](#licencia)

---

## Características

- **Registro de Usuarios Nuevos:**  
  Validación de campos obligatorios y verificación de que el usuario tenga al menos 18 años.

- **Autenticación y Login:**  
  Autenticación de usuarios utilizando JWT para el control de acceso.

- **Gestión de Perfiles de Usuario:**  
  Consulta y actualización del perfil del cliente mediante DTOs específicos.

- **Recuperación de Contraseña:**  
  Proceso robustecido que permite solicitar y restablecer la contraseña, controlando la expiración del código y evitando solicitudes demasiado frecuentes.

- **Catálogo de Productos:**  
  Gestión de productos con imagen, descripción, monto y cantidad disponible, con funcionalidad de búsqueda por descripción.

- **Carretilla de Compras:**  
  Permite agregar, actualizar (cambiar cantidad) y eliminar ítems del carrito, además de mostrar un resumen del mismo mediante DTOs.

- **Gestión de Órdenes:**  
  Flujo de pedido que permite iniciar un pedido (estado PENDING), actualizar la dirección de envío y confirmar el pedido (estado CONFIRMED), generando un número de orden único.

- **Seguridad y Manejo de Errores:**  
  Uso de JWT con validación robusta y un manejo global de excepciones que retorna respuestas con formato Problem Details.  
  Documentación de la API ampliada a través de Swagger.

---

## Tecnologías Utilizadas

- **Java 11/17** (según la versión que estés usando)
- **Spring Boot**
- **Spring Security** (con JWT)
- **Spring Data JPA**
- **MariaDB/MySQL**
- **Gradle** (con wrapper)
- **Lombok**
- **Swagger/OpenAPI**

---

## Estructura del Proyecto

La estructura del proyecto se organiza de forma modular. Algunas de las carpetas principales son:

```
edulynch-micpruebatecnica_backend/
├── gradlew, gradlew.bat, gradle/           # Gradle Wrapper y configuración
└── src/main/java/com/blautech/pruebaTecnica/demo/
    ├── Main.java                           # Clase principal
    ├── api/
    │   ├── auth/                           # Autenticación y manejo de JWT
    │   ├── cart/                           # Gestión de la carretilla de compras
    │   ├── catalog/                        # Gestión de productos
    │   ├── order/                          # Gestión de órdenes
    │   ├── roles/                          # Gestión de roles
    │   └── users/                          # Gestión de usuarios y recuperación de contraseña
    ├── config/                             # Configuración de seguridad, correo, Swagger, etc.
    ├── exception/                          # Manejo global de excepciones y definición de ProblemDetails
    ├── audit/                              # Auditoría de acciones
    ├── util/                               # Constantes y utilidades
    └── validation/                         # Validaciones personalizadas (ej. @Adult, ValidationGroups)
```

---

## Configuración e Instalación

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/tu_usuario/edulynch-micpruebatecnica_backend.git
   cd edulynch-micpruebatecnica_backend
   ```

2. **Configurar la base de datos:**

   - Asegúrate de tener una instancia de **MariaDB** o **MySQL** en ejecución.
   - Modifica el archivo `src/main/resources/application.properties` con la URL, usuario y contraseña correspondientes a tu base de datos.

3. **Configuración de correo:**

   - En el archivo `application.properties` se configuran las propiedades de envío de correo (por ejemplo, para Gmail). Asegúrate de que los datos sean correctos para el envío de correos.

4. **Codificación y Gradle:**

   - Verifica que el archivo `application.properties` esté guardado en **UTF-8 sin BOM**.
   - En la raíz del proyecto, crea o edita el archivo `gradle.properties` y añade:
     
     ```properties
     org.gradle.jvmargs=-Dfile.encoding=UTF-8
     ```

5. **Configuración del IDE:**

   - Configura tu IDE (IntelliJ IDEA, Eclipse, etc.) para utilizar UTF-8 como codificación predeterminada.

---

## Ejecución de la Aplicación

Para ejecutar la aplicación, utiliza el wrapper de Gradle:

```bash
./gradlew bootRun   # En Linux/Mac
gradlew.bat bootRun # En Windows
```

La aplicación se iniciará en el puerto por defecto (8080) a menos que hayas configurado otro puerto en `application.properties`.

---

## Endpoints Principales

Algunos endpoints disponibles son:

- **Autenticación:**  
  `POST /api/auth/login` – Login y generación de token JWT.  
  `POST /api/auth/validate` – Validación del token.

- **Usuarios:**  
  `POST /api/users` – Registro de nuevos usuarios.  
  `GET /api/users/profile` – Consulta del perfil del usuario autenticado.  
  `PUT /api/users/profile` – Actualización del perfil del usuario.

- **Recuperación de Contraseña:**  
  `POST /api/users/password-reset/request` – Solicitud de restablecimiento de contraseña.  
  `POST /api/users/password-reset/reset` – Restablecimiento de contraseña.

- **Catálogo de Productos:**  
  `GET /api/catalog` – Listado de productos.  
  `GET /api/catalog/active` – Productos activos.  
  `GET /api/catalog/search?query=texto` – Búsqueda de productos por descripción.

- **Carretilla de Compras:**  
  `GET /api/cart` – Consulta del carrito actual.  
  `POST /api/cart/items` – Agregar ítem al carrito.  
  `PUT /api/cart/items/{itemId}` – Actualizar la cantidad de un ítem.  
  `DELETE /api/cart/items/{itemId}` – Eliminar un ítem del carrito.  
  `GET /api/cart/summary` – Resumen del carrito (subtotal y total).

- **Órdenes:**  
  `POST /api/orders/initiate` – Iniciar un pedido a partir del carrito (estado PENDING).  
  `PUT /api/orders/{orderId}/shipping-address` – Actualizar la dirección de envío de un pedido pendiente.  
  `POST /api/orders/{orderId}/confirm` – Confirmar el pedido (cambiar estado a CONFIRMED y generar número de orden).  
  `GET /api/orders` – Listar órdenes (según rol: usuario normal o WORKER/ADMIN).  
  `GET /api/orders/{orderId}` – Detalle de una orden.

---

## Mejoras y Backlog

### Mejoras Completadas

- Registro de usuarios nuevos con validaciones.
- Consulta y actualización del perfil del cliente.
- Proceso de recuperación de contraseña robustecido.
- Funcionalidad de búsqueda en el catálogo.
- Carretilla de compras con actualización de cantidad y resumen mediante DTO.
- Gestión de pedidos y órdenes con flujo de pedido (iniciar, actualizar dirección, confirmar).
- Seguridad y JWT robustos.
- Documentación y manejo de errores (Swagger, GlobalExceptionHandler).
- Configuración de codificación (UTF-8) y Gradle.

### Para el Backlog

- **Login de Usuarios (Producción):**  
  Asegurar HTTPS y almacenamiento seguro de claves secretas para JWT.
- **Notificación asíncrona (opcional):**  
  Considerar el uso de colas para el envío asíncrono de correos de recuperación.

---

## Licencia

Este proyecto se distribuye bajo la licencia **[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)**.

---

¡Gracias por revisar este proyecto! Si tienes alguna duda o sugerencia, no dudes en abrir un issue o contactarme.
```

Este README incluye una descripción general del proyecto, instrucciones de instalación y ejecución, detalles de los endpoints principales, la estructura del proyecto y un resumen de las mejoras implementadas y pendientes. Puedes ajustarlo según tus necesidades o agregar información adicional según evolucione el proyecto.

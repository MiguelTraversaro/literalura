# 📚 LiterAlura - Challenge Oracle Next Education

**LiterAlura** es una aplicación de consola desarrollada en Java que permite gestionar un catálogo de libros interactuando con la API [Gutendex](https://gutendex.com/). El sistema permite buscar libros, registrar autores y realizar análisis estadísticos sobre los datos almacenados en una base de datos relacional.



---

## 🎯 Objetivo del Proyecto
Desarrollar una herramienta que facilite la búsqueda de información literaria, permitiendo la persistencia de datos y ofreciendo una interfaz de usuario sencilla a través de la consola para interactuar con un catálogo dinámico.

## 🚀 Funcionalidades

1.  **Búsqueda de libros por título**: Consulta la API de Gutendex y guarda el libro (y su autor) en la base de datos si no existe previamente.
2.  **Listar libros registrados**: Muestra todos los ejemplares almacenados en el catálogo local.
3.  **Listar autores registrados**: Proporciona un listado de los autores guardados, detallando sus fechas de nacimiento/fallecimiento y sus obras.
4.  **Listar autores vivos en un año determinado**: Filtra los autores basándose en un año específico ingresado por el usuario.
5.  **Estadísticas por idioma**: Calcula y muestra la cantidad de libros por idioma junto con su porcentaje de participación en el catálogo total.

---

## 🛠️ Tecnologías Utilizadas

* **Java 17**: Lenguaje de programación principal.
* **Spring Boot 3.2.3**: Framework para agilizar el desarrollo.
* **Spring Data JPA**: Para el mapeo objeto-relacional y gestión de la base de datos.
* **PostgreSQL**: Sistema de gestión de base de datos relacional.
* **Jackson**: Biblioteca para la conversión de datos JSON a objetos Java.
* **Maven**: Gestor de dependencias y construcción del proyecto.

---

## 📋 Configuración y Ejecución

### 1. Requisitos
* JDK 17 instalado.
* PostgreSQL 16 (o superior) instalado y configurado.
* Maven.

### 2. Base de Datos
Crea la base de datos necesaria en tu terminal de PostgreSQL:
```sql
CREATE DATABASE literalura_db;
```

### 3. Configuración del Entorno
* Edita el archivo src/main/resources/application.properties para incluir tus credenciales:

#### Properties
* spring.datasource.url=jdbc:postgresql://localhost:5432/literalura_db
* spring.datasource.username=tu_usuario_postgres
* spring.datasource.password=tu_contraseña
* spring.jpa.hibernate.ddl-auto=update
* spring.jpa.show-sql=true
  
### 4. Ejecución
* Puedes ejecutar la aplicación desde tu IDE o mediante la terminal:

* Bash
* mvn spring-boot:run
  
## 📂 Estructura del Código
* model: Contiene los Records para el mapeo del JSON y las Entidades para la base de datos.

* repository: Interfaces que extienden JpaRepository para las consultas a PostgreSQL.

* service: Clases encargadas de la conexión HTTP (ConsumoAPI) y la deserialización de datos (ConvierteDatos).

* principal: Clase que gestiona el menú interactivo y la lógica de usuario.

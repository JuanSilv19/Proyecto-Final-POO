# 🅿️ EpicParqueadero PRO

**Proyecto del Taller de Panel Administrativo**
Tecnología de Desarrollo de Sistemas Informáticos
📅 I Semestre 2026
👨‍🏫 Profesor: Mag. Carlos Adolfo Beltrán Castro

👨‍💻 Estudiantes:
- Juan Andrés Cárdenas Silva — CC: 1014739348
- Christian Marín Delgado — CC: 1005334750


---


> Pantalla inicial del Sistema de Parqueadero con menú de navegación
<img width="484" height="361" alt="image" src="https://github.com/user-attachments/assets/ddc108c1-abb0-4a95-a496-fff7284cdd03" />


---

## 🚀 Descripción del Proyecto

EpicParqueadero PRO es un sistema de gestión de parqueadero desarrollado con **Java SE - SWING**. Permite administrar usuarios, vehículos y tarifas mediante interfaces gráficas modernas conectadas a una base de datos PostgreSQL en la nube. Incluye navegación entre módulos y funcionalidades CRUD completas.

---

## 🗂️ Estructura del Proyecto

```
EpicParqueaderoPRO/
├── src/main/java/
│   ├── app/
│   │   └── Main.java                  # Punto de entrada
│   ├── model/
│   │   ├── Persona.java               # Clase abstracta base
│   │   ├── Usuario.java               # Extiende Persona
│   │   ├── Vehiculo.java
│   │   ├── Tarifa.java
│   │   ├── Registro.java
│   │   └── TipoVehiculo.java
│   ├── persistencia/
│   │   ├── ConexionDB.java            # Conexión PostgreSQL
│   │   ├── I_CRUD.java                # Interfaz genérica CRUD
│   │   ├── UsuarioDAO.java
│   │   ├── VehiculoDAO.java
│   │   ├── TarifaDAO.java
│   │   └── RegistroDAO.java
│   └── view/
│       ├── PrincipalView.java         # Menú principal SWING
│       ├── UsuarioView.java           # CRUD Usuarios
│       ├── VehiculoView.java          # CRUD Vehículos
│       └── TarifaView.java            # CRUD Tarifas
├── database.sql                       # Script de base de datos
├── Diagrama Relacional.png            # Diagrama ER
└── DiagramDeclases.png                # Diagrama de clases
```

**Lista de Menú de Opciones:**
- 👤 Gestión de Usuarios
- 🚗 Gestión de Vehículos
- 💰 Gestión de Tarifas
- 🚪 Salir

**Vistas CRUD** con tablas en: Usuarios, Vehículos y Tarifas

**Salir** con mensaje informativo de cierre de sesión

---

## 🛠️ Lista de Tecnologías Usadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java SE | JDK 23 | Lenguaje principal |
| Java SWING | Incluido en JDK | Interfaz gráfica |
| PostgreSQL | 42.6.0 (JDBC) | Base de datos |
| Clever Cloud | — | Hosting BD en la nube |
| Maven | 3.x | Gestión de dependencias |
| NetBeans IDE | 24 | Entorno de desarrollo |

---

## ⚙️ Instalación y Ejecución

### Requisitos
- Java JDK 17 o superior instalado
- Conexión a internet (la BD está en la nube)

### Opción 1 — Ejecutar el JAR
```bash
java -jar EpicParqueaderoPRO-1.0.jar
```

### Opción 2 — Desde NetBeans
1. Abrir NetBeans IDE
2. File → Open Project → seleccionar carpeta `Proyecto Parqueadero`
3. Clic derecho en el proyecto → Properties → Run → Main Class: `app.Main`
4. Presionar F6

### Opción 3 — Maven
```bash
cd "Proyecto Parqueadero"
./mvnw clean package
java -jar target/EpicParqueaderoPRO-1.0.jar
```

---

## 🗄️ Base de Datos

La base de datos está alojada en **Clever Cloud** (PostgreSQL en la nube).

### Tablas principales:
- `usuarios` — gestión de clientes del parqueadero
- `vehiculos` — vehículos registrados por usuario
- `tarifas` — tarifas por tipo de vehículo (hora/día)
- `ingresos_salidas` — registro de entradas y salidas

El script completo está en [`database.sql`](database.sql)

---

## 🧱 Diagrama Entidad-Relación

![Diagrama ER](Diagrama%20Relacional.png)

---

## 📐 Patrones y Principios POO Aplicados

- **Herencia** — `Usuario` extiende la clase abstracta `Persona`
- **Abstracción** — clase abstracta `Persona` con atributos base
- **Interfaz** — `I_CRUD<T>` implementada por todos los DAOs
- **Encapsulamiento** — atributos privados con getters/setters
- **Patrón DAO** — separación entre lógica de negocio y acceso a datos

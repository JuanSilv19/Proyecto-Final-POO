# 🅿️ EpicParqueadero PRO

**Proyecto del Taller de Panel Administrativo**  
Tecnología de Desarrollo de Sistemas Informáticos — Unidades Tecnológicas de Santander (UTS)  
📅 I Semestre 2026  
👨‍🏫 Profesor: Mag. Carlos Adolfo Beltrán Castro  

👨‍💻 Estudiantes:  
- Juan Andrés Cárdenas Silva — CC: 1014739348  
- Christian Marín Delgado — CC: 1005334750  

---


## 🚀 Descripción del Proyecto

EpicParqueadero PRO es un sistema de gestión y control de estacionamientos desarrollado bajo el paradigma de Programación Orientada a Objetos utilizando **Java SE (Swing)**. La aplicación expone un entorno administrativo robusto conectado a una base de datos **PostgreSQL** para centralizar la gestión de usuarios y el control de vehículos en tiempo real.

### ✨ Características Principales
* **Seguridad en el Acceso:** Flujo de autenticación obligatorio para resguardar las operaciones del sistema.
* **Panel Administrativo Limpio:** Interfaz unificada orientada a la experiencia del operador.
* **Persistencia Robusta:** Operaciones CRUD completas mapeadas directamente a una base de datos relacional.

---
```
EpicParqueaderoPRO/
├── src/main/java/
│   ├── app/
│   │   └── Main.java                  # Punto de entrada oficial del sistema
│   ├── model/
│   │   ├── Persona.java               # Clase abstracta base
│   │   ├── Usuario.java               # Entidad que extiende de Persona
│   │   ├── Vehiculo.java              # Entidad de gestión de automotores
│   │   └── Registro.java              # Control de entradas y salidas
│   ├── persistencia/
│   │   ├── ConexionDB.java            # Conexión centralizada a PostgreSQL
│   │   ├── I_CRUD.java                # Interfaz genérica para operaciones CRUD
│   │   ├── UsuarioDAO.java            # Implementación de persistencia de usuarios
│   │   └── RegistroDAO.java           # Control de persistencia de flujos
│   └── view/
│       ├── LoginView.java             # Interfaz de acceso obligatorio
│       ├── PrincipalView.java         # Panel administrativo y menú de navegación
│       ├── OperationalView.java       # Módulo operativo del sistema
│       └── UsuarioView.java           # Panel de gestión de usuarios
├── database.sql                       # Script de estructura y DDL de la base de datos
├── Diagrama Relacional.png            # Diagrama Entidad-Relación
└── DiagramDeclases.png                # Estructura de clases del sistema
```

## 🖼️ Módulos de la Interfaz Gráfica (Capturas del Sistema)

### 🔐 1. Flujo de Autenticación (Inicio de Sesión)
Control de acceso seguro que valida las credenciales de los usuarios administradores antes de permitir el ingreso al panel de control.

<img width="507" height="315" alt="image" src="https://github.com/user-attachments/assets/a24a66e7-d8fc-4292-9aa1-0514cfe7af80" />

### 🖥️ 2. Panel Administrativo (Vista Principal del Admin)
Consola centralizada de navegación que permite al operador redirigirse a los submódulos del sistema y realizar un cierre de sesión seguro.

<img width="1596" height="927" alt="image" src="https://github.com/user-attachments/assets/8c71261a-2b1a-4a92-b9e3-f16ef01ce81d" />

### 👤 3. Gestión Operativa y Control de Vehículos (Vista del Cliente)
Interfaces dinámicas con componentes de tablas (`JTable`) para visualizar, registrar, actualizar y controlar los flujos de entrada y salida de vehículos vinculados a sus respectivos clientes.


<img width="1603" height="922" alt="image" src="https://github.com/user-attachments/assets/8e70faf5-1532-4a9b-931a-18b43140804d" />

---

## 🛠️ Arquitectura Tecnológica

| Tecnología | Versión | Uso / Rol en el Sistema |
|---|---|---|
| **Java SE** | JDK 25 | Lenguaje y entorno de ejecución principal |
| **Java SWING** | Incluido | Framework para el diseño de interfaces de usuario |
| **PostgreSQL Driver** | 42.7.7 | Conector JDBC para la persistencia de datos |
| **IntelliJ IDEA** | 2025.2.4 | Entorno de Desarrollo Integrado (IDE) |
| **Git / GitHub** | Versión Activa | Control de versiones y despliegue del artefacto |

---

## ⚙️ Instalación y Despliegue

### Requisitos Mínimos
* Java JDK 17 o superior instalado en el sistema.
* Conexión a red activa (para el consumo de datos de la BD en la nube).

### Ejecución Directa (Producción)
Para ejecutar la aplicación compilada estable sin necesidad de abrir un IDE, descarga el repositorio y ejecuta el archivo empaquetado desde tu consola:

```bash
java -jar out/artifacts/EpicParqueaderoPRO_jar/EpicParqueaderoPRO.jar


Ejecución en Entorno de Desarrollo (IntelliJ IDEA)
Clona el repositorio e importa la carpeta raíz como proyecto de Java.

Asegúrate de mapear el SDK del proyecto a tu versión de JDK instalada.

Ubica la clase src/main/java/app/Main.java, haz clic derecho y selecciona Run 'Main.main()'.

🧱 Pilares de Programación Orientada a Objetos Aplicados
Herencia: La clase Usuario hereda la estructura de datos y comportamiento común definidos en la clase abstracta Persona.

Abstracción: Definición conceptual de entidades base mediante clases abstractas y métodos de contratos lógicos que no exponen su implementación interna.

Uso de Interfaces (I_CRUD<T>): Desacoplamiento total del acceso a datos mediante contratos abstractos implementados obligatoriamente por los objetos de acceso a datos (DAOs).

Encapsulamiento: Restricción de acceso directo a las propiedades del modelo mediante modificadores de acceso privados, exponiendo la interacción segura a través de métodos accesores (Getters y Setters).

-- Tabla de Usuarios
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(255) NOT NULL,
    apellido_usuario VARCHAR(255) NOT NULL,
    telefono_usuario VARCHAR(20),
    email_usuario VARCHAR(255) UNIQUE,
    cliente_frecuente BOOLEAN DEFAULT FALSE
);

-- Tabla de Vehiculos
CREATE TABLE vehiculos (
    id_vehiculo SERIAL PRIMARY KEY,
    id_usuario INT,
    placa VARCHAR(10) NOT NULL UNIQUE,
    tipo_vehiculo VARCHAR(50),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Tabla de Tarifas
CREATE TABLE tarifas (
    id_tarifa SERIAL PRIMARY KEY,
    tipo_vehiculo VARCHAR(50) NOT NULL UNIQUE,
    tarifa_hora DECIMAL(10, 2) NOT NULL,
    tarifa_dia DECIMAL(10, 2) NOT NULL
);

-- Tabla de Registros de Ingresos y Salidas
CREATE TABLE ingresos_salidas (
    id_ingreso_salida SERIAL PRIMARY KEY,
    id_vehiculo INT NOT NULL,
    id_espacio INT NOT NULL,
    hora_entrada TIMESTAMP NOT NULL,
    hora_salida TIMESTAMP,
    id_tarifa INT NOT NULL,
    en_taller BOOLEAN DEFAULT FALSE,
    monto_pagado DECIMAL(10, 2),
    FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo),
    FOREIGN KEY (id_tarifa) REFERENCES tarifas(id_tarifa)
    -- FOREIGN KEY (id_espacio) REFERENCES espacios(id_espacio) -- Descomentar si existe la tabla espacios
);

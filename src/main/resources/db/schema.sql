-- Esquema PostgreSQL para Cosmos (perfil prod).
-- En desarrollo (perfil dev) se usa H2 y Hibernate genera las tablas automaticamente.
-- La base de datos se crea desde Neon / Render; aqui solo van las tablas.

CREATE TABLE IF NOT EXISTS rol (
    id_rol SERIAL PRIMARY KEY,
    rol    VARCHAR(25) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario SERIAL PRIMARY KEY,
    username   VARCHAR(50) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    nombre     VARCHAR(100),
    apellidos  VARCHAR(100),
    correo     VARCHAR(150),
    telefono   VARCHAR(30),
    direccion  VARCHAR(255),
    activo     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS usuario_rol (
    id_usuario INT NOT NULL REFERENCES usuario (id_usuario),
    id_rol     INT NOT NULL REFERENCES rol (id_rol),
    PRIMARY KEY (id_usuario, id_rol)
);

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria SERIAL PRIMARY KEY,
    descripcion  VARCHAR(50) NOT NULL UNIQUE,
    ruta_imagen  VARCHAR(1024),
    activo       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS producto (
    id_producto  SERIAL PRIMARY KEY,
    nombre       VARCHAR(80) NOT NULL,
    descripcion  VARCHAR(500),
    precio       NUMERIC(12,2),
    bajo_pedido  BOOLEAN NOT NULL DEFAULT FALSE,
    ruta_imagen  VARCHAR(1024),
    disponible   BOOLEAN NOT NULL DEFAULT TRUE,
    destacado    BOOLEAN NOT NULL DEFAULT FALSE,
    id_categoria INT REFERENCES categoria (id_categoria)
);

CREATE TABLE IF NOT EXISTS pedido (
    id_pedido  SERIAL PRIMARY KEY,
    id_usuario INT REFERENCES usuario (id_usuario),
    fecha      TIMESTAMP NOT NULL,
    estado     VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    total      NUMERIC(12,2) NOT NULL DEFAULT 0,
    nota       VARCHAR(300)
);

CREATE TABLE IF NOT EXISTS pedido_detalle (
    id_pedido_detalle SERIAL PRIMARY KEY,
    id_pedido         INT NOT NULL REFERENCES pedido (id_pedido),
    id_producto       INT NOT NULL REFERENCES producto (id_producto),
    cantidad          INT NOT NULL DEFAULT 1,
    precio_unitario   NUMERIC(12,2) NOT NULL DEFAULT 0
);

INSERT INTO rol (rol) VALUES ('ADMIN'), ('CLIENTE')
    ON CONFLICT (rol) DO NOTHING;

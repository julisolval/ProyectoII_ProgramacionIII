-- ELIMINAR BASE DE DATOS EXISTENTE Y CREAR NUEVA
DROP DATABASE IF EXISTS recetas_medicas;
CREATE DATABASE recetas_medicas;
USE recetas_medicas;

-- Crear tablas con nombres en MINÚSCULAS
CREATE TABLE usuario (
    id VARCHAR(20) PRIMARY KEY,
    clave VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo ENUM('MEDICO', 'FARMACEUTA', 'ADMIN') NOT NULL,
    especialidad VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE paciente (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    telefono VARCHAR(15)
);

CREATE TABLE medicamento (
    codigo VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    presentacion VARCHAR(50)
);

CREATE TABLE receta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente VARCHAR(20),
    id_medico VARCHAR(20),
    fecha_confeccion DATE,
    fecha_retiro DATE,
    estado ENUM('CONFECCIONADA', 'PROCESO', 'LISTA', 'ENTREGADA') DEFAULT 'CONFECCIONADA',
    FOREIGN KEY (id_paciente) REFERENCES paciente(id),
    FOREIGN KEY (id_medico) REFERENCES usuario(id)
);

CREATE TABLE detallereceta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_receta INT,
    codigo_medicamento VARCHAR(20),
    cantidad INT,
    indicaciones TEXT,
    duracion INT,
    FOREIGN KEY (id_receta) REFERENCES receta(id),
    FOREIGN KEY (codigo_medicamento) REFERENCES medicamento(codigo)
);

-- Insertar FARMACÉUTICOS
INSERT INTO usuario (id, clave, nombre, tipo, especialidad) VALUES
    ('5555', '5555', 'Benjamin', 'FARMACEUTA', NULL),
    ('4567', '4567', 'Sebastian', 'FARMACEUTA', NULL),
    ('8987', '8987', 'Ema', 'FARMACEUTA', NULL),
    ('9090', '9090', 'Ale', 'FARMACEUTA', NULL),
    ('2211', '2211', 'Mario', 'FARMACEUTA', NULL);

-- Insertar MÉDICOS
INSERT INTO usuario (id, clave, nombre, tipo, especialidad) VALUES
    ('1111', '2222', 'Isaac', 'MEDICO', 'Cardiologia'),
    ('2222', '2222', 'Julissa', 'MEDICO', 'Dermatologia'),
    ('3333', '3333', 'Carlos', 'MEDICO', 'Pediatria'),
    ('7777', '7777', 'Jose', 'MEDICO', 'General'),
    ('1122', '1122', 'Pablo', 'MEDICO', 'Forense');

-- Insertar ADMINISTRADOR
INSERT INTO usuario (id, clave, nombre, tipo, especialidad) VALUES
    ('admin', 'admin', 'Administrador', 'ADMIN', NULL);

-- Insertar PACIENTES
INSERT INTO paciente (id, nombre, fecha_nacimiento, telefono) VALUES
    ('6666', 'Andrea', '2024-02-08', '1111-1111'),
    ('6789', 'Antonio', '2025-09-03', '2222-2222'),
    ('2233', 'Maria', '2025-09-02', '3333-3333');

-- Insertar MEDICAMENTOS
INSERT INTO medicamento (codigo, nombre, presentacion) VALUES
    ('1111', 'Ibuprofeno', '250.0'),
    ('2222', 'Panadol', '2.3'),
    ('3333', 'Pronol', '500.0'),
    ('4444', 'Loratadina', '50.0');

-- Insertar RECETAS
INSERT INTO receta (id_paciente, id_medico, fecha_confeccion, fecha_retiro, estado) VALUES
    ('6666', '1111', '2025-09-08', '2025-09-11', 'ENTREGADA'),
    ('6666', '1111', '2025-09-14', '2025-10-14', 'CONFECCIONADA'),
    ('2233', '2222', '2025-09-14', '2025-09-15', 'LISTA'),
    ('6789', '3333', '2025-09-14', '2025-09-13', 'PROCESO'),
    ('6789', '7777', '2025-09-16', '2025-01-24', 'CONFECCIONADA');

-- Insertar DETALLES DE RECETAS
INSERT INTO detallereceta (id_receta, codigo_medicamento, cantidad, indicaciones, duracion) VALUES
    (1, '1111', 1, 'Cada 12 horas', 8),
    (2, '1111', 1, 'Cada 12 horas', 8),
    (3, '4444', 1, 'Tomar cada 10 horas', 12),
    (4, '4444', 2, 'Cada 8 horas', 3),
    (5, '1111', 4, 'Tarde', 4);

-- VERIFICACIÓN FINAL
SELECT '=== TABLAS CREADAS ===' as '';
SHOW TABLES;

SELECT '=== USUARIOS ===' as '';
SELECT id, nombre, tipo, especialidad FROM usuario;

SELECT '=== RESUMEN DE DATOS ===' as '';
SELECT 
    (SELECT COUNT(*) FROM usuario) as total_usuarios,
    (SELECT COUNT(*) FROM paciente) as total_pacientes,
    (SELECT COUNT(*) FROM medicamento) as total_medicamentos,
    (SELECT COUNT(*) FROM receta) as total_recetas,
    (SELECT COUNT(*) FROM detallereceta) as total_detalles;

SELECT '=== USUARIOS PARA LOGIN ===' as '';
SELECT id as usuario, clave as password, tipo, nombre 
FROM usuario 
WHERE tipo IN ('MEDICO', 'FARMACEUTA', 'ADMIN');

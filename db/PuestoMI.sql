-- ============================================================================
-- PuestoMI - Refresqueria Maria Isabel (POS / inventario)
-- Script unico de creacion de la base de datos desde cero.
-- Incluye los cambios de los antiguos scripts 001 y 002:
--   * producto.codigo_corto (unico) en lugar de sku / codigo_barras
--   * sin caducidades (sin lote.fecha_caducidad ni producto.dias_alerta_caducidad)
--   * lote.costo_unitario
--   * producto.stock_minimo (>= 0) y stock_maximo (>= 2 y >= minimo) obligatorios
-- OJO: borra la base PuestoMI si ya existe. Ejecutar completo, una sola vez.
-- ============================================================================

DROP DATABASE IF EXISTS PuestoMI;
CREATE DATABASE PuestoMI CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE PuestoMI;

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------------
-- Catalogos y seguridad
-- ---------------------------------------------------------------------------
CREATE TABLE usuario (
  id_usuario      INT NOT NULL AUTO_INCREMENT,
  nombre_usuario  VARCHAR(100) NOT NULL,
  nombre_completo VARCHAR(255) NOT NULL,
  contrasena_hash VARCHAR(255) NOT NULL,
  activo          TINYINT(1) DEFAULT 1,
  fecha_creacion  DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY nombre_usuario (nombre_usuario)
) ENGINE=InnoDB;

CREATE TABLE categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(100) NOT NULL,
  PRIMARY KEY (id_categoria),
  UNIQUE KEY nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE proveedor (
  id_proveedor INT NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(255) NOT NULL,
  correo       VARCHAR(255) DEFAULT NULL,
  telefono     VARCHAR(50) DEFAULT NULL,
  empresa      VARCHAR(255) DEFAULT NULL,
  activo       TINYINT(1) DEFAULT 1,
  PRIMARY KEY (id_proveedor)
) ENGINE=InnoDB;

CREATE TABLE cliente (
  id_cliente     INT NOT NULL AUTO_INCREMENT,
  nombre         VARCHAR(255) NOT NULL,
  rfc            VARCHAR(13) DEFAULT NULL,
  regimen_fiscal VARCHAR(100) DEFAULT NULL,
  activo         TINYINT(1) DEFAULT 1,
  PRIMARY KEY (id_cliente),
  UNIQUE KEY rfc (rfc)
) ENGINE=InnoDB;

CREATE TABLE parametro (
  clave       VARCHAR(100) NOT NULL,
  valor       VARCHAR(255) NOT NULL,
  descripcion TEXT,
  PRIMARY KEY (clave)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Productos e inventario
-- ---------------------------------------------------------------------------
CREATE TABLE producto (
  id_producto  INT NOT NULL AUTO_INCREMENT,
  codigo_corto VARCHAR(50) NOT NULL,
  nombre       VARCHAR(255) NOT NULL,
  id_categoria INT NOT NULL,
  id_proveedor INT NOT NULL,
  costo        DECIMAL(12,2) NOT NULL,
  precio_venta DECIMAL(12,2) NOT NULL,
  stock_minimo INT NOT NULL,
  stock_maximo INT NOT NULL,
  activo       TINYINT(1) DEFAULT 1,
  PRIMARY KEY (id_producto),
  UNIQUE KEY uk_producto_codigo_corto (codigo_corto),
  KEY id_categoria (id_categoria),
  KEY id_proveedor (id_proveedor),
  CONSTRAINT producto_ibfk_1 FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT producto_ibfk_2 FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_producto_stock_minimo CHECK (stock_minimo >= 0),
  CONSTRAINT chk_producto_stock_maximo CHECK (stock_maximo >= 2 AND stock_maximo >= stock_minimo)
) ENGINE=InnoDB;

CREATE TABLE entrada_inventario (
  id_entrada    INT NOT NULL AUTO_INCREMENT,
  id_proveedor  INT NOT NULL,
  id_usuario    INT NOT NULL,
  fecha_hora    DATETIME DEFAULT CURRENT_TIMESTAMP,
  observaciones TEXT,
  PRIMARY KEY (id_entrada),
  KEY id_proveedor (id_proveedor),
  KEY id_usuario (id_usuario),
  CONSTRAINT entrada_inventario_ibfk_1 FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT entrada_inventario_ibfk_2 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE lote (
  id_lote             INT NOT NULL AUTO_INCREMENT,
  id_entrada          INT NOT NULL,
  id_producto         INT NOT NULL,
  cantidad_recibida   INT NOT NULL,
  cantidad_disponible INT NOT NULL,
  costo_unitario      DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id_lote),
  KEY id_entrada (id_entrada),
  KEY id_producto (id_producto),
  CONSTRAINT lote_ibfk_1 FOREIGN KEY (id_entrada) REFERENCES entrada_inventario (id_entrada) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT lote_ibfk_2 FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Ventas, pagos y devoluciones
-- ---------------------------------------------------------------------------
CREATE TABLE venta (
  id_venta           INT NOT NULL AUTO_INCREMENT,
  fecha_hora         DATETIME DEFAULT CURRENT_TIMESTAMP,
  id_usuario         INT NOT NULL,
  id_cliente         INT DEFAULT NULL,
  total              DECIMAL(12,2) NOT NULL,
  estado             VARCHAR(50) NOT NULL,
  motivo_cancelacion TEXT,
  PRIMARY KEY (id_venta),
  KEY id_usuario (id_usuario),
  KEY id_cliente (id_cliente),
  CONSTRAINT venta_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT venta_ibfk_2 FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
  id_detalle      INT NOT NULL AUTO_INCREMENT,
  id_venta        INT NOT NULL,
  id_producto     INT NOT NULL,
  cantidad        INT NOT NULL,
  precio_unitario DECIMAL(12,2) NOT NULL,
  costo_unitario  DECIMAL(12,2) NOT NULL,
  subtotal        DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id_detalle),
  KEY id_venta (id_venta),
  KEY id_producto (id_producto),
  CONSTRAINT detalle_venta_ibfk_1 FOREIGN KEY (id_venta) REFERENCES venta (id_venta) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT detalle_venta_ibfk_2 FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE detalle_venta_lote (
  id_detalle INT NOT NULL,
  id_lote    INT NOT NULL,
  cantidad   INT NOT NULL,
  PRIMARY KEY (id_detalle, id_lote),
  KEY id_lote (id_lote),
  CONSTRAINT detalle_venta_lote_ibfk_1 FOREIGN KEY (id_detalle) REFERENCES detalle_venta (id_detalle) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT detalle_venta_lote_ibfk_2 FOREIGN KEY (id_lote) REFERENCES lote (id_lote) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pago_venta (
  id_pago  INT NOT NULL AUTO_INCREMENT,
  id_venta INT NOT NULL,
  metodo   VARCHAR(100) NOT NULL,
  monto    DECIMAL(12,2) NOT NULL,
  comision DECIMAL(12,2) DEFAULT 0.00,
  PRIMARY KEY (id_pago),
  KEY id_venta (id_venta),
  CONSTRAINT pago_venta_ibfk_1 FOREIGN KEY (id_venta) REFERENCES venta (id_venta) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE devolucion (
  id_devolucion     INT NOT NULL AUTO_INCREMENT,
  id_venta          INT NOT NULL,
  id_usuario        INT NOT NULL,
  fecha_hora        DATETIME DEFAULT CURRENT_TIMESTAMP,
  monto_reembolsado DECIMAL(12,2) NOT NULL,
  motivo            TEXT,
  PRIMARY KEY (id_devolucion),
  KEY id_venta (id_venta),
  KEY id_usuario (id_usuario),
  CONSTRAINT devolucion_ibfk_1 FOREIGN KEY (id_venta) REFERENCES venta (id_venta) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT devolucion_ibfk_2 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE detalle_devolucion (
  id_detalle_devolucion INT NOT NULL AUTO_INCREMENT,
  id_devolucion         INT NOT NULL,
  id_detalle            INT NOT NULL,
  id_lote               INT NOT NULL,
  cantidad              INT NOT NULL,
  revendible            TINYINT(1) DEFAULT 1,
  PRIMARY KEY (id_detalle_devolucion),
  KEY id_devolucion (id_devolucion),
  KEY id_detalle (id_detalle),
  KEY id_lote (id_lote),
  CONSTRAINT detalle_devolucion_ibfk_1 FOREIGN KEY (id_devolucion) REFERENCES devolucion (id_devolucion) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT detalle_devolucion_ibfk_2 FOREIGN KEY (id_detalle) REFERENCES detalle_venta (id_detalle) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT detalle_devolucion_ibfk_3 FOREIGN KEY (id_lote) REFERENCES lote (id_lote) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Mermas, caja, facturacion y bitacora
-- ---------------------------------------------------------------------------
CREATE TABLE merma (
  id_merma       INT NOT NULL AUTO_INCREMENT,
  id_lote        INT NOT NULL,
  id_usuario     INT NOT NULL,
  id_devolucion  INT DEFAULT NULL,
  fecha_hora     DATETIME DEFAULT CURRENT_TIMESTAMP,
  cantidad       INT NOT NULL,
  motivo         TEXT,
  costo_unitario DECIMAL(12,2) NOT NULL,
  observaciones  TEXT,
  PRIMARY KEY (id_merma),
  KEY id_lote (id_lote),
  KEY id_usuario (id_usuario),
  KEY id_devolucion (id_devolucion),
  CONSTRAINT merma_ibfk_1 FOREIGN KEY (id_lote) REFERENCES lote (id_lote) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT merma_ibfk_2 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT merma_ibfk_3 FOREIGN KEY (id_devolucion) REFERENCES devolucion (id_devolucion) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE corte_caja (
  id_corte            INT NOT NULL AUTO_INCREMENT,
  fecha               DATE NOT NULL,
  id_usuario          INT NOT NULL,
  total_efectivo      DECIMAL(12,2) DEFAULT 0.00,
  total_transferencia DECIMAL(12,2) DEFAULT 0.00,
  total_tarjeta       DECIMAL(12,2) DEFAULT 0.00,
  total_devoluciones  DECIMAL(12,2) DEFAULT 0.00,
  total_comision      DECIMAL(12,2) DEFAULT 0.00,
  total_neto          DECIMAL(12,2) DEFAULT 0.00,
  fecha_hora_cierre   DATETIME DEFAULT NULL,
  PRIMARY KEY (id_corte),
  UNIQUE KEY fecha (fecha),
  KEY id_usuario (id_usuario),
  CONSTRAINT corte_caja_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE facturacion_cliente (
  id_facturacion    INT NOT NULL AUTO_INCREMENT,
  id_cliente        INT NOT NULL,
  anio              INT NOT NULL,
  mes               INT NOT NULL,
  total_compras     DECIMAL(12,2) DEFAULT 0.00,
  facturada         TINYINT(1) DEFAULT 0,
  fecha_facturacion DATE DEFAULT NULL,
  folio_fiscal      VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (id_facturacion),
  KEY id_cliente (id_cliente),
  CONSTRAINT facturacion_cliente_ibfk_1 FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE bitacora (
  id_bitacora    INT NOT NULL AUTO_INCREMENT,
  id_usuario     INT NOT NULL,
  fecha_hora     DATETIME DEFAULT CURRENT_TIMESTAMP,
  accion         VARCHAR(100) NOT NULL,
  tabla_afectada VARCHAR(100) NOT NULL,
  id_registro    INT DEFAULT NULL,
  valor_anterior TEXT,
  valor_nuevo    TEXT,
  motivo         TEXT,
  PRIMARY KEY (id_bitacora),
  KEY id_usuario (id_usuario),
  CONSTRAINT bitacora_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------------
-- Datos iniciales (solo el usuario; categorias, proveedores y productos van en PuestoMI_datos.sql)
-- La app compara la contrasena tal cual (sin cifrar): usuario admin / admin
-- ---------------------------------------------------------------------------
INSERT INTO usuario (nombre_usuario, nombre_completo, contrasena_hash, activo)
VALUES ('admin', 'Administrador', 'admin', 1);

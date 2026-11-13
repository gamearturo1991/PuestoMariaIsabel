-- Migracion 001: SKU / codigo_barras  ->  codigo_corto
-- Ejecutar UNA vez por cada base de datos local del equipo (schema: inventario).
-- Despues de correrlo, actualizar el codigo (git pull) para que Producto.java use codigoCorto.
-- Idempotente a medias: si ya existe codigo_corto, no lo vuelvas a correr.

USE inventario;

-- 1) Nueva columna (nullable mientras se llena)
ALTER TABLE producto ADD COLUMN codigo_corto VARCHAR(50) NULL AFTER id_producto;

-- 2) Conservar el SKU actual como codigo corto (si excede 50 caracteres, usar P + id)
UPDATE producto
   SET codigo_corto = IF(CHAR_LENGTH(sku) <= 50, sku, CONCAT('P', id_producto));

-- 3) Obligatorio y unico
ALTER TABLE producto
  MODIFY COLUMN codigo_corto VARCHAR(50) NOT NULL,
  ADD CONSTRAINT uk_producto_codigo_corto UNIQUE (codigo_corto);

-- 4) Eliminar SKU y codigo de barras (sus indices unicos 'sku' y 'codigo_barras' se eliminan con la columna)
ALTER TABLE producto
  DROP COLUMN sku,
  DROP COLUMN codigo_barras;

-- Verificacion
-- DESCRIBE producto;
-- SELECT id_producto, codigo_corto, nombre FROM producto;

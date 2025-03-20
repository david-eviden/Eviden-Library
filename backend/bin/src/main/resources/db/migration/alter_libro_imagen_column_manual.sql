-- Script para ejecutar manualmente en la base de datos MySQL
-- Este script modifica la columna 'imagen' en la tabla 'libros' para que pueda almacenar datos más grandes

USE evidenlibrarydb;

-- Modificar la columna 'imagen' en la tabla 'libros'
ALTER TABLE libros MODIFY COLUMN imagen LONGTEXT; 
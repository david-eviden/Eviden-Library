-- Usuarios (incluye admin, empleados y usuarios regulares)
INSERT INTO usuarios (nombre, email, password, rol) VALUES
('Admin Sistema', 'admin@libreria.com', 'admin', 'ADMIN'),
('Empleado Uno', 'emp1@libreria.com', '1234', 'EMPLEADO'),
('Empleado Dos', 'emp2@libreria.com', '1234', 'EMPLEADO'),
('Juan Pérez', 'juan@email.com', '1234', 'USUARIO'),
('María López', 'maria@email.com', '1234', 'USUARIO'),
('Carlos Ruiz', 'carlos@email.com', '1234', 'USUARIO'),
('Ana García', 'ana@email.com', '1234', 'USUARIO'),
('Pedro Sánchez', 'pedro@email.com', '1234', 'USUARIO');

-- Géneros literarios
INSERT INTO generos (nombre, descripcion) VALUES
('Ficción Literaria', 'Obras de ficción con valor literario significativo'),
('Ciencia Ficción', 'Literatura que explora impactos de la ciencia y tecnología'),
('Fantasía', 'Literatura con elementos mágicos y sobrenaturales'),
('Romance', 'Historias centradas en relaciones románticas'),
('Misterio', 'Obras que involucran enigmas y suspense'),
('Terror', 'Literatura diseñada para provocar miedo'),
('Historia', 'Obras basadas en hechos históricos'),
('Biografía', 'Relatos de vidas reales'),
('Autoayuda', 'Libros para el desarrollo personal'),
('Infantil', 'Literatura para niños');

-- Autores
INSERT INTO autores (nombre, biografia) VALUES
('Gabriel García Márquez', 'Premio Nobel de Literatura 1982, escritor colombiano conocido por su realismo mágico'),
('Jorge Luis Borges', 'Escritor argentino, maestro del relato corto, la poesía y el ensayo'),
('Isabel Allende', 'Escritora chilena, reconocida por sus novelas que mezclan realismo mágico con hechos históricos'),
('Julio Cortázar', 'Escritor argentino, uno de los grandes innovadores de la literatura en español'),
('Mario Vargas Llosa', 'Premio Nobel de Literatura 2010, escritor peruano'),
('George R.R. Martin', 'Autor estadounidense de fantasía épica'),
('J.K. Rowling', 'Autora británica de la serie Harry Potter'),
('Stephen King', 'Prolífico autor estadounidense de terror y suspense'),
('Jane Austen', 'Novelista británica del siglo XIX'),
('Ernest Hemingway', 'Escritor estadounidense, premio Nobel de Literatura');

-- Libros
INSERT INTO libros (titulo, precio, stock) VALUES
('Cien años de soledad', 29.99, 50),
('El Aleph', 19.99, 30),
('La casa de los espíritus', 24.99, 40),
('Rayuela', 22.99, 25),
('La ciudad y los perros', 21.99, 35),
('Juego de tronos', 34.99, 60),
('Harry Potter y la piedra filosofal', 24.99, 100),
('El resplandor', 19.99, 45),
('Orgullo y prejuicio', 14.99, 55),
('El viejo y el mar', 16.99, 40),
('Ficciones', 20.99, 30),
('Paula', 23.99, 25),
('62/Modelo para armar', 18.99, 20),
('Conversación en la catedral', 26.99, 30),
('Choque de reyes', 34.99, 45);

-- Relaciones Libro-Autor
INSERT INTO libro_autor (libro_id, autor_id) VALUES
(1, 1), -- García Márquez - Cien años de soledad
(2, 2), -- Borges - El Aleph
(3, 3), -- Allende - La casa de los espíritus
(4, 4), -- Cortázar - Rayuela
(5, 5), -- Vargas Llosa - La ciudad y los perros
(6, 6), -- Martin - Juego de tronos
(7, 7), -- Rowling - Harry Potter
(8, 8), -- King - El resplandor
(9, 9), -- Austen - Orgullo y prejuicio
(10, 10), -- Hemingway - El viejo y el mar
(11, 2), -- Borges - Ficciones
(12, 3), -- Allende - Paula
(13, 4), -- Cortázar - 62/Modelo para armar
(14, 5), -- Vargas Llosa - Conversación en la catedral
(15, 6); -- Martin - Choque de reyes

-- Relaciones Libro-Género
INSERT INTO libro_genero (libro_id, genero_id) VALUES
(1, 1), (1, 3), -- Cien años de soledad: Ficción, Fantasía
(2, 1), (2, 5), -- El Aleph: Ficción, Misterio
(3, 1), (3, 3), -- La casa de los espíritus: Ficción, Fantasía
(4, 1), -- Rayuela: Ficción
(5, 1), -- La ciudad y los perros: Ficción
(6, 3), -- Juego de tronos: Fantasía
(7, 3), (7, 10), -- Harry Potter: Fantasía, Infantil
(8, 6), -- El resplandor: Terror
(9, 1), (9, 4), -- Orgullo y prejuicio: Ficción, Romance
(10, 1), -- El viejo y el mar: Ficción
(11, 1), (11, 5), -- Ficciones: Ficción, Misterio
(12, 8), -- Paula: Biografía
(13, 1), -- 62/Modelo para armar: Ficción
(14, 1), (14, 7), -- Conversación en la catedral: Ficción, Historia
(15, 3); -- Choque de reyes: Fantasía

-- Carritos activos
INSERT INTO carritos (usuario_id, fecha_creacion, estado) VALUES
(4, '2025-02-18', 'ACTIVO'),
(5, '2025-02-18', 'ACTIVO'),
(6, '2025-02-18', 'ACTIVO'),
(7, '2025-02-17', 'PROCESADO'),
(8, '2025-02-17', 'PROCESADO');

-- Detalles de carritos
INSERT INTO detalles_carrito (carrito_id, libro_id, cantidad) VALUES
(1, 1, 1), -- Juan: Cien años de soledad
(1, 7, 1), -- Juan: Harry Potter
(2, 6, 1), -- María: Juego de tronos
(2, 15, 1), -- María: Choque de reyes
(3, 8, 1), -- Carlos: El resplandor
(3, 9, 1), -- Carlos: Orgullo y prejuicio
(4, 3, 1), -- Ana: La casa de los espíritus
(5, 10, 1); -- Pedro: El viejo y el mar

-- Pedidos
INSERT INTO pedidos (usuario_id, fecha_pedido, estado, total, direccion_envio) VALUES
(4, '2025-02-15', 'ENTREGADO', 54.98, 'Calle Mayor 123'),
(5, '2025-02-16', 'ENVIADO', 69.98, 'Avenida Principal 456'),
(6, '2025-02-17', 'EN_PROCESO', 34.98, 'Plaza Central 789'),
(7, '2025-02-17', 'PENDIENTE', 24.99, 'Callejón Real 321'),
(8, '2025-02-18', 'EN_PROCESO', 26.99, 'Calle Nueva 654');

-- Detalles de pedidos
INSERT INTO detalles_pedido (pedido_id, libro_id, cantidad, precio_unitario) VALUES
(1, 1, 1, 29.99), -- Juan: Cien años de soledad
(1, 7, 1, 24.99), -- Juan: Harry Potter
(2, 6, 1, 34.99), -- María: Juego de tronos
(2, 15, 1, 34.99), -- María: Choque de reyes
(3, 8, 1, 19.99), -- Carlos: El resplandor
(3, 9, 1, 14.99), -- Carlos: Orgullo y prejuicio
(4, 3, 1, 24.99), -- Ana: La casa de los espíritus
(5, 10, 1, 16.99), -- Pedro: El viejo y el mar
(5, 11, 1, 20.99); -- Pedro: Ficciones

-- Valoraciones
INSERT INTO valoraciones (usuario_id, libro_id, puntuacion, comentario, fecha) VALUES
(4, 1, 5, 'Una obra maestra del realismo mágico', '2025-02-15'),
(4, 7, 4, 'Excelente inicio de la saga', '2025-02-15'),
(5, 6, 5, 'Fantasía épica en su máxima expresión', '2025-02-16'),
(5, 15, 5, 'Continúa la excelente saga', '2025-02-16'),
(6, 8, 4, 'Terrorífico y envolvente', '2025-02-17'),
(6, 9, 5, 'Un clásico imprescindible', '2025-02-17'),
(7, 3, 4, 'Narrativa cautivadora', '2025-02-17'),
(8, 10, 5, 'Una historia conmovedora', '2025-02-18'),
(8, 11, 5, 'Borges en su mejor momento', '2025-02-18');

-- Favoritos
INSERT INTO favoritos (usuario_id, libro_id, fecha_agregado) VALUES
(4, 1, '2025-02-15'),
(4, 7, '2025-02-15'),
(5, 6, '2025-02-16'),
(5, 15, '2025-02-16'),
(6, 8, '2025-02-17'),
(6, 9, '2025-02-17'),
(7, 3, '2025-02-17'),
(8, 10, '2025-02-18'),
(8, 11, '2025-02-18');
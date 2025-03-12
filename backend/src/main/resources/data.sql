-- Usuarios (incluye admin, empleados y usuarios regulares)
INSERT INTO usuarios (nombre, apellido, direccion, email, password, rol) VALUES
('Admin', 'Sistema', 'localhost', 'admin@libreria.com', 'admin', 'ADMIN'),
('Empleado', 'Uno', 'Calle Falsa nº1, Valladolid', 'emp1@libreria.com', '1234', 'EMPLEADO'),
('Empleado', 'Dos', 'Calle Falsa nº2, Valladolid', 'emp2@libreria.com', '1234', 'EMPLEADO'),
('Juan', 'Pérez', 'Calle Falsa nº3, Valladolid', 'juan@email.com', '1234', 'USUARIO'),
('María', 'López', 'Calle Falsa nº4, Valladolid', 'maria@email.com', '1234', 'USUARIO'),
('Carlos', 'Ruiz', 'Calle Falsa nº5, Valladolid', 'carlos@email.com', '1234', 'USUARIO'),
('Ana', 'García', 'Calle Falsa nº6, Valladolid', 'ana@email.com', '1234', 'USUARIO'),
('Pedro', 'Sánchez', 'Calle Falsa nº7, Valladolid', 'pedro@email.com', '1234', 'USUARIO');

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
INSERT INTO libros (titulo, precio, stock, descripcion, anio) VALUES
('Cien años de soledad', 29.99, 2, 'Una novela épica de Gabriel García Márquez que narra la historia de la familia Buendía en el mítico pueblo de Macondo. Un clásico de la literatura latinoamericana que explora el amor, la soledad y el destino, todo en un marco de realismo mágico.', '1967'),
('El Aleph', 19.99, 4, 'Una colección de relatos de Jorge Luis Borges que aborda temas como el infinito, los laberintos, el tiempo y la literatura misma, con la típica maestría filosófica y literaria del autor.', '1945'),
('La casa de los espíritus', 24.99, 15, 'Isabel Allende presenta una saga familiar en la que lo sobrenatural se entrelaza con los eventos políticos y sociales de Chile. Un relato de amores, tragedias y esperanzas en el contexto histórico de América Latina.', '1982'),
('Rayuela', 22.99, 25, 'Julio Cortázar crea una obra revolucionaria, experimentando con la estructura narrativa. El protagonista, Horacio Oliveira, vive un romance intelectual con La Maga mientras navega por la incomodidad de su existencia.', '1963'),
('La ciudad y los perros', 21.99, 35, 'Mario Vargas Llosa presenta una crítica profunda de la vida en un internado militar en Lima, explorando temas de violencia, lealtad y la lucha de poder entre jóvenes estudiantes en un contexto de dictadura.', '1963'),
('Juego de tronos', 34.99, 8, 'El primer libro de la famosa saga "Canción de Hielo y Fuego" de George R.R. Martin, donde intrigas políticas, traiciones y luchas por el poder definen un mundo medieval de fantasía.', '1996'),
('Harry Potter y la piedra filosofal', 24.99, 35, 'La historia que introduce al joven mago Harry Potter en su mundo de magia, amistad y aventura, cuando descubre que es un "niño elegido" para enfrentar las fuerzas oscuras del mal.', '1997'),
('El resplandor', 19.99, 3, 'Una novela de terror psicológico escrita por Stephen King, que relata la historia de una familia que se ve atrapada en un hotel aislado por el invierno, donde las fuerzas oscuras que acechan comienzan a manifestarse.', '1977'),
('Orgullo y prejuicio', 14.99, 55, 'La famosa novela de Jane Austen, que relata la historia de Elizabeth Bennet y su relación con el orgulloso pero intrigante Sr. Darcy. Una historia sobre el amor, los prejuicios y la lucha por la autonomía femenina.', '1813'),
('El viejo y el mar', 16.99, 40, 'Una novela breve pero profunda de Ernest Hemingway sobre un viejo pescador cubano y su lucha solitaria contra un enorme marlín, que simboliza su enfrentamiento con la naturaleza y la vida misma.', '1952'),
('Ficciones', 20.99, 30, 'Una serie de relatos breves de Jorge Luis Borges que exploran mundos paralelos, laberintos y la incertidumbre de la realidad, creando una obra maestra literaria de la literatura contemporánea.', '1944'),
('Paula', 23.99, 25, 'La novela de Isabel Allende sobre la vida de su hija Paula, quien sufrió una enfermedad terminal. Una obra profundamente personal que mezcla la narración de su vida con la reflexión sobre la maternidad y la muerte.', '1994'),
('Modelo para armar', 18.99, 20, 'Una novela de Juan José Saer que explora la vida cotidiana de un grupo de personas en un pequeño pueblo argentino, atrapados en sus recuerdos y en sus deseos frustrados.', '1983'),
('Conversación en la catedral', 26.99, 30, 'Mario Vargas Llosa presenta una compleja narrativa sobre el Perú de mediados del siglo XX, donde se mezclan la política, la corrupción y la vida personal de un periodista y un amigo que intentan desentrañar la realidad de su país.', '1969'),
('Choque de reyes', 34.99, 45, 'El segundo libro de la serie "Canción de Hielo y Fuego", donde los reinos de Westeros continúan en guerra. La lucha por el Trono de Hierro intensifica las traiciones, alianzas y secretos en un mundo lleno de magia y violencia.', '1998'),
('Daughter of Fortune', 27.99, 12, 'Una novela que narra la vida de Eliza Sommers, una joven chilena que emigra a California en busca de su amor. Una historia de aventuras, pasión y descubrimientos personales.', '1999'),
('Portrait in Sepia', 25.99, 18, 'Una novela histórica de Isabel Allende que relata la vida de la joven Aurora del Valle, quien, a través de una serie de secretos familiares y revelaciones, trata de comprender su identidad y su pasado.', '2000'),
('La isla bajo el mar', 22.99, 20, 'Una obra que se sitúa en la época de la esclavitud en Haití y relata la vida de Zarité, una esclava que lucha por su libertad en un mundo lleno de opresión y lucha social.', '2009'),
('El cuaderno de Maya', 21.99, 15, 'Maya, una joven que huye de la vida en California y se encuentra con un nuevo destino en una isla chilena. Una novela sobre autodescubrimiento, redención y la conexión con el pasado.', '2011'),
('El amante japonés', 23.99, 25, 'Una historia de amor, secretos y lealtades en la que los protagonistas, Alma Belasco y Ichimei, navegan entre la tragedia personal, los vínculos familiares y la relación con el mundo durante la Segunda Guerra Mundial.', '2015');



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
(7, '2025-02-17', 'ACTIVO'),
(8, '2025-02-17', 'ACTIVO');

-- Detalles de carritos
INSERT INTO detalles_carrito (carrito_id, libro_id, cantidad, precio_unitario) VALUES
(1, 1, 2, 29.99),
(1, 3, 1, 24.99),
(1, 7, 1, 24.99),
(2, 2, 1, 19.99),
(2, 4, 2, 22.99),
(2, 6, 1, 34.99),
(2, 15, 1, 34.99),
(3, 8, 2, 19.99),
(3, 9, 1, 14.99),
(3, 11, 1, 20.99),
(4, 3, 1, 24.99),
(4, 12, 1, 23.99),
(4, 5, 2, 21.99),
(5, 10, 1, 16.99),
(5, 13, 2, 18.99),
(5, 14, 1, 26.99);

-- Pedidos
INSERT INTO pedidos (usuario_id, fecha_pedido, estado, total, direccion_envio) VALUES
(4, '2025-02-15', 'ENTREGADO', 54.98, 'Calle Mayor 123'),
(5, '2025-02-16', 'ENVIADO', 69.98, 'Avenida Principal 456'),
(6, '2025-02-17', 'EN_PROCESO', 34.98, 'Plaza Central 789'),
(7, '2025-02-17', 'PENDIENTE', 24.99, 'Callejón Real 321'),
(8, '2025-02-18', 'EN_PROCESO', 26.99, 'Calle Nueva 654'),
(4, '2025-02-20', 'EN_PROCESO', 49.98, 'Calle Mayor 123'),
(5, '2025-02-21', 'PENDIENTE', 39.98, 'Avenida Principal 456'),
(6, '2025-02-22', 'ENVIADO', 44.98, 'Plaza Central 789'),
(7, '2025-02-22', 'ENTREGADO', 34.99, 'Callejón Real 321'),
(8, '2025-02-23', 'EN_PROCESO', 59.97, 'Calle Nueva 654');

-- Detalles de pedidos
INSERT INTO detalles_pedido (pedido_id, libro_id, cantidad, precio_unitario) VALUES
(1, 1, 3, 29.99), -- Juan: Cien años de soledad
(1, 7, 1, 24.99), -- Juan: Harry Potter
(2, 6, 1, 34.99), -- María: Juego de tronos
(2, 15, 2, 34.99), -- María: Choque de reyes
(3, 8, 1, 19.99), -- Carlos: El resplandor
(3, 9, 1, 14.99), -- Carlos: Orgullo y prejuicio
(4, 3, 2, 24.99), -- Ana: La casa de los espíritus
(5, 10, 1, 16.99), -- Pedro: El viejo y el mar
(5, 11, 1, 20.99), -- Pedro: Ficciones
(1, 3, 2, 24.99), -- Juan: La casa de los espíritus
(1, 10, 1, 16.99), -- Juan: El viejo y el mar
(2, 5, 1, 21.99), -- María: La ciudad y los perros
(2, 4, 2, 22.99), -- María: Rayuela
(3, 15, 1, 34.99), -- Carlos: Choque de reyes
(3, 12, 1, 23.99), -- Carlos: Paula
(4, 6, 1, 34.99), -- Ana: Juego de tronos
(5, 2, 2, 19.99), -- Pedro: El Aleph
(5, 1, 1, 29.99), -- Pedro: Cien años de soledad
(6, 3, 1, 24.99),  -- La casa de los espíritus
(6, 5, 1, 21.99),  -- La ciudad y los perros
(7, 2, 1, 19.99),  -- El Aleph
(7, 4, 1, 22.99),  -- Rayuela
(8, 6, 1, 34.99),  -- Juego de tronos
(8, 8, 1, 19.99),  -- El resplandor
(9, 7, 1, 24.99),  -- Harry Potter y la piedra filosofal
(9, 9, 1, 14.99),  -- Orgullo y prejuicio
(10, 1, 1, 29.99), -- Cien años de soledad
(10, 10, 1, 16.99), -- El viejo y el mar
(10, 11, 1, 20.99); -- Ficciones

-- Valoraciones
INSERT INTO valoraciones (usuario_id, libro_id, puntuacion, comentario, fecha) VALUES
(4, 1, 5, 'Una obra maestra del realismo mágico', '2025-02-15'),
(4, 7, 4, 'Excelente inicio de la saga', '2025-02-15'),
(5, 6, 5, 'Fantasía épica en su máxima expresión', '2025-02-16'),
(5, 15, 3, 'Continúa la excelente saga', '2025-02-16'),
(6, 8, 4, 'Terrorífico y envolvente', '2025-02-17'),
(6, 9, 5, 'Un clásico imprescindible', '2025-02-17'),
(7, 3, 4, 'Narrativa cautivadora', '2025-02-17'),
(8, 10, 3, 'Una historia conmovedora', '2025-02-18'),
(8, 11, 5, 'Borges en su mejor momento', '2025-02-18'),
(5, 1, 4, 'Una obra literaria que no deja indiferente', '2025-02-19'),
(5, 2, 4, 'Fascinante colección de relatos, siempre sorprendente', '2025-02-19'),
(6, 4, 5, 'Rayuela es una obra que me marcó profundamente', '2025-02-19'),
(6, 7, 5, 'Harry Potter siempre ha sido una de mis sagas favoritas', '2025-02-19'),
(7, 6, 4, 'A pesar de ser muy largo, vale la pena', '2025-02-19'),
(7, 9, 4, 'Orgullo y prejuicio es un clásico que no pasa de moda', '2025-02-19'),
(8, 2, 5, 'Es imposible no quedar atrapado por el estilo de Borges', '2025-02-19'),
(8, 5, 4, 'Una obra profunda, llena de contexto histórico', '2025-02-19'),
(4, 10, 4, 'Una novela profunda y reflexiva', '2025-02-20'),
(4, 12, 5, 'Paula es un libro que toca el alma', '2025-02-20'),
(4, 3, 5, 'Un relato fascinante que te atrapa desde la primera página', '2025-02-20'),
(4, 10, 4, 'Una reflexión profunda sobre la vida y la muerte', '2025-02-20'),
(5, 5, 5, 'Una historia impresionante, con una crítica social profunda', '2025-02-21'),
(5, 4, 4, 'Una obra que siempre invita a la reflexión', '2025-02-21'),
(6, 15, 5, 'La saga continúa con gran nivel de detalle y giros inesperados', '2025-02-22'),
(6, 12, 4, 'Una obra conmovedora sobre la vida y la muerte', '2025-02-22'),
(7, 6, 5, 'Un libro que cada vez que lo releo, me sorprende', '2025-02-22'),
(8, 2, 5, 'Borges siempre te transporta a un mundo de infinitas posibilidades', '2025-02-23'),
(8, 1, 4, 'Un clásico que sigue siendo relevante, a pesar del tiempo', '2025-02-23');


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
(8, 11, '2025-02-18'),
(4, 3, '2025-02-20'),
(4, 10, '2025-02-20'),
(5, 5, '2025-02-21'),
(5, 4, '2025-02-21'),
(6, 15, '2025-02-22'),
(6, 12, '2025-02-22'),
(7, 6, '2025-02-22'),
(8, 2, '2025-02-23'),
(8, 1, '2025-02-23');
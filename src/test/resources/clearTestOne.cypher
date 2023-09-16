MATCH (c:Compania {usuarioModificacion:'TEST_ONE'}) DETACH DELETE c
MATCH (a:Area {usuarioModificacion:'TEST_ONE'}) DETACH DELETE a
MATCH (ag:AreaAsignada {usuarioModificacion:'TEST_ONE'}) DETACH DELETE ag
MATCH (g:Grupo {usuarioModificacion:'TEST_ONE'}) DETACH DELETE g
MATCH (u:Usuario {usuarioModificacion:'TEST_ONE'}) DETACH DELETE u
MATCH (r:Rol {usuarioModificacion:'TEST_ONE'}) DETACH DELETE r
MATCH (f:Facultad {usuarioModificacion:'TEST_ONE'}) DETACH DELETE f
CREATE CONSTRAINT unique_compania IF NOT EXISTS FOR (compania:Compania) REQUIRE compania.nombre IS UNIQUE
CREATE CONSTRAINT unique_usuario IF NOT EXISTS FOR (usuario:Usuario) REQUIRE usuario.idUsuario IS UNIQUE
CREATE CONSTRAINT unique_usuario2 IF NOT EXISTS FOR (usuario:Usuario) REQUIRE usuario.nombreUsuario IS UNIQUE
CREATE CONSTRAINT unique_area IF NOT EXISTS FOR (area:Area) REQUIRE area.isArea IS UNIQUE
CREATE CONSTRAINT unique_grupo IF NOT EXISTS FOR (grupo:Grupo) REQUIRE grupo.nombre IS UNIQUE


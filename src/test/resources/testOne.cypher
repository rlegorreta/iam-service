/* ======================================================================== */
/* Script to add Test ONE additional data from iamDBStart.cypher            */
/*                                                                          */
/* Date: September 2023                                                     */
/* ======================================================================== */
MERGE (br:Compania {nombre:'BlackRock', padre:true, negocio:'OPERADORA', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (brrf:Area {nombre:'Tesoreria', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (brrv:Area {nombre:'Sistemas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (brar:Area {nombre:'Operacion', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (brrf)-[:CONTIENE]-(br)
MERGE (brrv)-[:CONTIENE]-(br)
MERGE (brar)-[:CONTIENE]-(br)

MERGE (brrfa:AreaAsignada {nombre:'Tesoreria', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})
MERGE (brrva:AreaAsignada {nombre:'Sistemas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})
MERGE (brara:AreaAsignada {nombre:'Operacion+', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})

MERGE (ef:Compania {nombre:'Aurrera', padre:true, negocio:'OPERADORA', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (efrf:Area {nombre:'Sistemas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (efrv:Area {nombre:'Ventas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (efrf)-[:CONTIENE]-(ef)
MERGE (efrv)-[:CONTIENE]-(ef)

MERGE (efrfa:AreaAsignada {nombre:'Sistemas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, isArea:0})
MERGE (efrva:AreaAsignada {nombre:'Ventas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, isArea:0})

MERGE (ab:Compania {nombre:'Afore Banamex', padre:true, negocio:'AFORE', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (abrf:Area {nombre:'Ventas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (abrv:Area {nombre:'Direccion', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (abar:Area {nombre:'Planta', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (abrf)-[:CONTIENE]-(ab)
MERGE (abrv)-[:CONTIENE]-(ab)
MERGE (abar)-[:CONTIENE]-(ab)

MERGE (abrfa:AreaAsignada {nombre:'Ventas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})
MERGE (abrva:AreaAsignada {nombre:'Direccion', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})
MERGE (abara:AreaAsignada {nombre:'Planta', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})

MERGE (act:Compania {nombre:'Actinver', padre:true, negocio:'OPERADORA', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (actc:Area {nombre:'ACTCOMM', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (acti5:Area {nombre:'ACTI500', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (actic:Area {nombre:'ACTICOB', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0, idPersona:500})
MERGE (actc)-[:CONTIENE]-(act)
MERGE (acti5)-[:CONTIENE]-(act)
MERGE (actic)-[:CONTIENE]-(act)

MERGE (actca:AreaAsignada {nombre:'ACTCOMM', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})
MERGE (acti5a:AreaAsignada {nombre:'ACTI500', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})
MERGE (actica:AreaAsignada {nombre:'ACTICOB', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true, idArea:0})

MERGE (gbr:Grupo {nombre:'Admin BlackRock', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true})
MERGE (gbr)-[:PERMITE]->(br)

MERGE (gef:Grupo {nombre:'Admin Es Mas', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true})
MERGE (gef)-[:PERMITE]->(ef)

MERGE (abg:Grupo {nombre:'Admin Banamex', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true})
MERGE (abg)-[:PERMITE]->(ab)

MERGE (actg:Grupo {nombre:'Admin Actinver', usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo:true})
MERGE (actg)-[:PERMITE]->(act)

MERGE (ubr:Usuario {idUsuario:5000, nombre:'AdminBR', nombreUsuario:'Administrador BlackRock', apellido:'Blackrock', telefono:"5591495040",
                  mail:"staff@br.com.mx", interno:false, activo:true, administrador: true,
                  fechaIngreso:date(),
                  usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})

MERGE (uef:Usuario {idUsuario:5001, nombre:'AdminEF', nombreUsuario:'Administrador Aurrera',  apellido:'Aurrera', telefono:"5591495040",
                  mail:"staff@esmas.com.mx", interno:false, activo:true, administrador: true,
                  fechaIngreso:date(),
                  usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})

MERGE (uab:Usuario {idUsuario:5002, nombre:'AdminAB', nombreUsuario:'Administrador Afore Banamex', apellido:'Banamex', telefono:"5591495040",
                  mail:"staff@banamex.com.mx", interno:false, activo:true, administrador: true,
                  fechaIngreso:date(),
                  usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})

MERGE (uac:Usuario {idUsuario:5003, nombre:'AdminAC', nombreUsuario:'Administrador Actinver',  apellido:'Actinver', telefono:"5591495040",
                mail:"staff@banamex.com.mx", interno:false, activo:true, administrador: true,
                fechaIngreso:date(),
                usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})

MERGE (ubrrf2:Usuario {idUsuario:6000, nombre:'Agente1', nombreUsuario:'Agente 1',  apellido:'1', telefono:"5591495040",
                  mail:"staff@br.com.mx", interno:true, activo:true, administrador: false,
                  fechaIngreso:date(),
                  usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})
MERGE (ubrrf3:Usuario {idUsuario:6001, nombre:'Agente2', nombreUsuario:'Agente 2',  apellido:'2', telefono:"5591495040",
                  mail:"staff@br.com.mx", interno:true, activo:true, administrador: false,
                  fechaIngreso:date(),
                  usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})
MERGE (ubrrf4:Usuario {idUsuario:6002, nombre:'Agente3', nombreUsuario:'Agente 3',  apellido:'3', telefono:"5591495040",
                  mail:"staff@br.com.mx", interno:true, activo:true, administrador: false,
                  fechaIngreso:date(),
                  usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})
MERGE (ubrrf5:Usuario {idUsuario:6003, nombre:'Agente4', nombreUsuario:'Agente 4',  apellido:'4', telefono:"5591495040",
                  mail:"staff@br.com.mx", interno:true, activo:true, administrador: false,
                  fechaIngreso:date(),
                  usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})
MERGE (ubrrf6:Usuario {idUsuario:6004, nombre:'Agente5', nombreUsuario:'Agente 5',  apellido:'5', telefono:"5591495040",
                mail:"staff@br.com.mx", interno:true, activo:true, administrador: false,
                fechaIngreso:date(),
                usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})

MERGE (ubr)-[:MIEMBRO]->(gbr)
MERGE (ubr)-[:TRABAJA]->(br)

MERGE (uef)-[:MIEMBRO]->(gef)
MERGE (uef)-[:TRABAJA]->(ef)

MERGE (uab)-[:MIEMBRO]->(gab)
MERGE (uab)-[:TRABAJA]->(ab)

MERGE (uac)-[:MIEMBRO]->(actg)
MERGE (uac)-[:TRABAJA]->(act)

MERGE (ubr)-[:TIENE_PERFIL]->(pa)
MERGE (uef)-[:TIENE_PERFIL]->(pa)
MERGE (uab)-[:TIENE_PERFIL]->(pa)
MERGE (uac)-[:TIENE_PERFIL]->(pa)

MERGE (rbbr:Rol {idRol:3, nombre:'Cartera', activo:true, usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})
MERGE (rbbr2:Rol {idRol:4, nombre:'Cartera Admin', activo:true, usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime()})

MERGE (fbra:Facultad {nombre:'CARTERA', descripcion:'Facultad para imprimir los reportes regulatorios',
       tipo:"SIMPLE",usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo: true})
MERGE (fbrb:Facultad {nombre:'MOVIMIENTOS', descripcion:'Facultad para cargar vector precios',
       tipo:"SIMPLE",usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo: true})
MERGE (fbrc:Facultad {nombre:'CARTERA_ADMIN', descripcion:'Facultad para la generación contable',
       tipo:"SIMPLE",usuarioModificacion:'TEST_ONE', fechaModificacion:localdatetime(), activo: true})

MERGE (rbbr)-[:TIENE_FACULTAD]->(fbra)
MERGE (rbbr)-[:TIENE_FACULTAD]->(fbrb)
MERGE (rbbr)-[:TIENE_FACULTAD]->(fbrc)
MERGE (rbbr2)-[:TIENE_FACULTAD]->(fbrc)

MERGE (lmass:Compania {nombre:'LMASS Desarrolladores SA de CV'})
/* ^ note: this node was created in iamDbstart.cypher script */
MERGE (ubrrf2)-[:TRABAJABA]->(lmass)
MERGE (ubrrf3)-[:TRABAJABA]->(lmass)
MERGE (ubrrf4)-[:TRABAJABA]->(lmass)
MERGE (ubrrf5)-[:TRABAJABA]->(lmass)
MERGE (ubrrf6)-[:TRABAJABA]->(lmass)

/* Example for a new group without access to its subsidiaries */
MERGE (uAACMEJR:Usuario {idUsuario:2341, nombreUsuario:'adminACMEJr', nombre:'Administrador Jr', apellido:'ACME Jr', telefono:"5591495040",
                         mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                         zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (gACME:Grupo {nombre:'Admin ACME Jr', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true})
MERGE (acme:Compania {nombre:'ACME SA de CV'})
MERGE (gACME)-[:PERMITE_SIN_HERENCIA]->(acme)
MERGE (uAACMEJR)-[:MIEMBRO]->(gACME)
MERGE (uAACMEJR)-[:TRABAJA{puesto:'administrador Jr'}]->(acme)
/* Check to supervisor */
MERGE (uAACME:Usuario {nombreUsuario:'adminACME'})
MERGE (uAACMEJR)-[:SUPERVISOR]->(uAACME)
;
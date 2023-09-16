/* ======================================================================== */
/* This script is to initialize the IAM Neo4j database for testing purpose  */
/* All data from the start.cypher is added plus some developer data needed  */
/* for testing purposes:                                                    */
/*                                                                          */
/* Date: July 2022                                                          */
/* ======================================================================== */

MATCH (n) DETACH DELETE n
                        
/* ======================================================= */
/* =====     C O M P A N I E S  LMASS                ===== */
/* ======================================================= */
MERGE (lmass:Compania {nombre:'LMASS Desarrolladores SA de CV', padre:true, negocio:'NA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (aiml:Compania {nombre:'AI/ML SA de CV', padre:false, negocio:'NA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (acme:Compania {nombre:'ACME SA de CV', padre:true, negocio:'INDUSTRIAL', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idPersona:1})
MERGE (acmet:Compania {nombre:'ACME Tienda SA de CV', padre:false, negocio:'INDUSTRIAL', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idPersona:2})
MERGE (acmeb:Compania {nombre:'ACME Bodega SA de CV', padre:false, negocio:'INDUSTRIAL', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idPersona:3})
MERGE (prov1:Compania {nombre:'AMAZON SA DE CV', padre:true, negocio:'INDUSTRIAL', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idPersona:4})
MERGE (prov2:Compania {nombre:'ABOGADOS SC', padre:true, negocio:'PARTICULAR', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idPersona:5})
MERGE (ixe:Compania {nombre:'IXE BANCO', padre:true, negocio:'FINANCIERA', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idPersona:6})
MERGE (intercam:Compania {nombre:'INTERCAM BANCO', padre:true, negocio:'FINANCIERA', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idPersona:7})

/* =====     S U B S I D I A R Y E S    ===== */
MERGE (aiml)-[:SUBSIDIARIA]-(lmass)
MERGE (acmet)-[:SUBSIDIARIA]-(acme)
MERGE (acmeb)-[:SUBSIDIARIA]-(acme)

/* =====     O P E R A T I O N   D A T E S  B Y  C O M P A N Y   (TODO maybe not needed)  ===== */
MERGE (sistLMASS:Sistema {compania:'LMASS Desarrolladores', fechaOperacion:date(), fechaModificacion:localdatetime(), estatus:'EN_OPERACION'})
MERGE (sistAIML:Sistema {compania:'AI/ML SA de CV', fechaOperacion:date(), fechaModificacion:localdatetime(), estatus:'EN_OPERACION'})
MERGE (sistACME:Sistema {compania:'ACME SA de CV', fechaOperacion:date(), fechaModificacion:localdatetime(), estatus:'EN_OPERACION'})
MERGE (sistACMET:Sistema {compania:'ACME Tienda SA de CV', fechaOperacion:date(), fechaModificacion:localdatetime(), estatus:'EN_OPERACION'})
MERGE (sistACMEB:Sistema {compania:'ACME Bodega SA de CV', fechaOperacion:date(), fechaModificacion:localdatetime(), estatus:'EN_OPERACION'})

/* =====     G R O U P   A D M I N I S T R A T O R     ===== */
MERGE (gLMASS:Grupo {nombre:'Admin LMASS', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true})
MERGE (gAIML:Grupo {nombre:'Admin AI/ML', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true})
MERGE (gACME:Grupo {nombre:'Admin ACME', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true})
MERGE (gACMET:Grupo {nombre:'Admin ACME Tienda', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true})
MERGE (gACMEB:Grupo {nombre:'Admin ACME Bodega', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true})

/* =====     R E L A T I O N   G R U O P - C O M P A N Y     ===== */
MERGE (gLMASS)-[:PERMITE]->(lmass)
MERGE (gAIML)-[:PERMITE]->(aiml)
MERGE (gACME)-[:PERMITE]->(acme)
MERGE (gACMET)-[:PERMITE]->(acmet)
MERGE (gACMEB)-[:PERMITE]->(acmeb)

/* =====     A D M I N I S T R A T O R S  U S E R S     ===== */
MERGE (u:Usuario {idUsuario:0, nombreUsuario:'adminIAM', nombre:'Administrador', apellido:'IAM', telefono:"5591495040",
                  mail:"staff@lmass.com.mx", interno:true, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (u)-[:MIEMBRO]->(gLMASS)
MERGE (u)-[:TRABAJA{puesto:'administrador'}]->(lmass)

MERGE (uLMASS:Usuario {idUsuario:1, nombreUsuario:'adminLMASS', nombre:'Administrador ', apellido:'LMASS', telefono:"5591495040",
                  mail:"staff@lmass.com.mx", interno:true, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uLMASS)-[:MIEMBRO]->(gLMASS)
MERGE (uLMASS)-[:TRABAJA{puesto:'administrador'}]->(lmass)

MERGE (uAIML:Usuario {idUsuario:2, nombreUsuario:'adminIAML', nombre:'Administrador', apellido:'AI/ML', telefono:"5591495040",
                  mail:"staff@aiml.com.mx", interno:true, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAIML)-[:MIEMBRO]->(gAIML)
MERGE (uAIML)-[:TRABAJA{puesto:'administrador'}]->(aiml)

MERGE (uAACME:Usuario {idUsuario:3, nombreUsuario:'adminACME', nombre:'Administrador', apellido:'ACME', telefono:"5591495040",
                  mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAACME)-[:MIEMBRO]->(gACME)
MERGE (uAACME)-[:TRABAJA{puesto:'administrador'}]->(acme)

MERGE (uAACMET:Usuario {idUsuario:4, nombreUsuario:'adminACMET', nombre:'Administrador ACME Tienda', apellido:'ACME', telefono:"5591495040",
                  mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAACMET)-[:MIEMBRO]->(gACMET)
MERGE (uAACMET)-[:TRABAJA{puesto:'administrador'}]->(acmet)

MERGE (uAACMEB:Usuario {idUsuario:5, nombreUsuario:'adminACMEB', nombre:'Administrador ACME Bodega', apellido:'ACME', telefono:"5591495040",
                  mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAACMEB)-[:MIEMBRO]->(gACMEB)
MERGE (uAACMEB)-[:TRABAJA{puesto:'administrador'}]->(acmeb)

/* =====     A D M I N I S T R A T O R   P R O F I L E S   ===== */
MERGE (pIAM:Perfil {nombre:'Administrador IAM', descripcion:'Puesto único de administrador de la IAM',
                 activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pMaestro:Perfil {nombre:'Administrador Maestro', descripcion:'Puesto único de administrador de un Corporativo',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pAdmin:Perfil {nombre:'Administrador', descripcion:'Puesto de administrador de una o varias empresas',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pAdminACME:Perfil {nombre:'Administrador ACME', descripcion:'Puesto de administrador de empresas ACME',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})

/* =====     R E L A T I O N S H I P  B E T W E E N   U S E R - P R O F I L E      ===== */
MERGE (u)-[:TIENE_PERFIL]->(pIAM)
MERGE (uLMASS)-[:TIENE_PERFIL]->(pMaestro)
MERGE (uLMASS)-[:TIENE_PERFIL]->(pAdmin)
MERGE (uAIML)-[:TIENE_PERFIL]->(pAdmin)
MERGE (uAACME)-[:TIENE_PERFIL]->(pAdminACME)
MERGE (uAACMET)-[:TIENE_PERFIL]->(pAdminACME)
MERGE (uAACMEB)-[:TIENE_PERFIL]->(pAdminACME)

/* =====     A D M I N I S T R A T O R     R O L E S ===== */
MERGE (rIAM:Rol {idRol:0, nombre:'Admin IAM', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rMaestro:Rol {idRol:1, nombre:'Admin Maestro', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rAdmin:Rol {idRol:2, nombre:'Admin', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rAdminACME:Rol {idRol:3, nombre:'Admin ACME', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})

/* =====     R E L A T I O N S H I P  B E T W E E N  P R O F I L E - R O L     ===== */
MERGE (pIAM)-[:TIENE_ROL]->(rIAM)
MERGE (pMaestro)-[:TIENE_ROL]->(rMaestro)
MERGE (pAdmin)-[:TIENE_ROL]->(rAdmin)
MERGE (pAdminACME)-[:TIENE_ROL]->(rAdminACME)

/* =====     P E R M I T S     ===== */
MERGE (fa:Facultad {nombre:'Crear_Corporativo', descripcion:'Facultad para crear nuevos corporativos',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fb:Facultad {nombre:'Admin_IAM', descripcion:'Facultad para la administración del IAM. Solo un usuario debe de tener esta facultad',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (foauth:Facultad {nombre:'SERVIDOR_TOKEN', descripcion:'Facultad para consultar usuarios en el OAuth server',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fc:Facultad {nombre:'Admin', descripcion:'Facultad para la administración de Corporativos. Solo un usuario debe de tener esta facultad',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fd:Facultad {nombre:'Admin_Emp', descripcion:'Facultad para la administración de Empresas',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fsys:Facultad {nombre:'ADMIN_SISTEMA', descripcion:'Facultad para la modificación de parámetros del sistema',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fadminACME:Facultad {nombre:'Admin ACME', descripcion:'Facultad para la administración empresas ACME',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
       
/* =====     R E L A T I O N S H I P  B E T W E E N   R O L E S - P E R M I T S   ===== */
MERGE (rIAM)-[:TIENE_FACULTAD]->(fa)
MERGE (rIAM)-[:TIENE_FACULTAD]->(fb)
MERGE (rIAM)-[:TIENE_FACULTAD]->(fc)
MERGE (rIAM)-[:TIENE_FACULTAD]->(fd)
MERGE (rIAM)-[:TIENE_FACULTAD]->(foauth)
MERGE (rMaestro)-[:TIENE_FACULTAD]->(fc)
MERGE (rMaestro)-[:TIENE_FACULTAD]->(fd)
MERGE (pAdmin)-[:TIENE_FACULTAD]->(fd)
MERGE (u)-[:FACULTAD_EXTRA]->(fsys)
MERGE (pAdminACME)-[:TIENE_FACULTAD]->(fadminACME)

/* ========================================================================= */
/* =====       E N D  O F  M I N I M U M  D A T A  R E Q U I R E D     ===== */
/* ========================================================================= */


/* ==== Developer data =============== */

MERGE (pbr:Perfil {nombre:'Completo', descripcion:'Ejecuta todos los roles declarados',
                  activo:true, patron: false, usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (pbr2:Perfil {nombre:'Limitado', descripcion:'Ejecuta solo algunas operaciones',
                  activo:true, patron: false, usuarioModificacion:'TEST', fechaModificacion:localdatetime()})

MERGE (aACMET:Area {nombre:'Tesorería ACME', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:1, idPersona:500})
MERGE (aACMEA:Area {nombre:'Administración ACME', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:2, idPersona: 501})
MERGE (aACMED:Area {nombre:'Dirección ACME', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:3, idPersona:502})
MERGE (aACMET)-[:CONTIENE]-(acme)
MERGE (aACMEA)-[:CONTIENE]-(acme)
MERGE (aACMED)-[:CONTIENE]-(acme)
MERGE (aaACMET:AreaAsignada {nombre:'Tesorería ACME', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:1, idPersona:500})
MERGE (aaACMEA:AreaAsignada {nombre:'Administración ACME', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:2, idPersona:501})
MERGE (aaACMED:AreaAsignada {nombre:'Dirección ACME', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:3, idPersona:502})

MERGE (aACMETT:Area {nombre:'Tesorería ACME Tienda', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:4, idPersona:503})
MERGE (aACMEAT:Area {nombre:'Administración ACME Tienda', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:5, idPersona: 504})
MERGE (aACMEDT:Area {nombre:'Dirección ACME Tienda', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:6, idPersona:505})
MERGE (aACMETT)-[:CONTIENE]-(acmet)
MERGE (aACMEAT)-[:CONTIENE]-(acmet)
MERGE (aACMEDT)-[:CONTIENE]-(acmet)
MERGE (aaACMETT:AreaAsignada {nombre:'Tesorería ACME Tienda', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:4, idPersona:503})
MERGE (aaACMEAT:AreaAsignada {nombre:'Administración ACME Tienda', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:5, idPersona:504})
MERGE (aaACMEDT:AreaAsignada {nombre:'Dirección ACME Tienda', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:6, idPersona:505})

MERGE (aACMEAB:Area {nombre:'Administración ACME Bodega', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:7, idPersona: 506})
MERGE (aACMEDB:Area {nombre:'Dirección ACME Bodega', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:8, idPersona:507})
MERGE (aACMEAB)-[:CONTIENE]-(acmeb)
MERGE (aACMEDB)-[:CONTIENE]-(acmeb)
MERGE (aaACMEAB:AreaAsignada {nombre:'Administración ACME Bodega', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:7, idPersona:506})
MERGE (aaACMEDB:AreaAsignada {nombre:'Dirección ACME Bodega', usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo:true, idArea:8, idPersona:507})

MERGE (uACMET:Usuario {idUsuario:500, nombreUsuario:'tesACME', nombre:'Pato', apellido:'Donald', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (uACMEA:Usuario {idUsuario:501, nombreUsuario:'adACME', nombre:'Mickey', apellido:'Mouse', telefono:"5591495040",
                       mail:"staff@esmas.com.mx", interno:false, activo:true, administrador: true,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (uACMED:Usuario {idUsuario:502, nombreUsuario:'directorACME', nombre:'Donald', apellido:'Kunth', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})

MERGE (uACMETT:Usuario {idUsuario:503, nombreUsuario:'tesACMET', nombre:'Steven', apellido:'Jobs', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (uACMEAT:Usuario {idUsuario:504, nombreUsuario:'adACMET', nombre:'Bill', apellido:'Gates', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (uACMEDT:Usuario {idUsuario:505, nombreUsuario:'directorACMET', nombre:'John', apellido:'McCarthy', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})

MERGE (uACMEAB:Usuario {idUsuario:506, nombreUsuario:'adACMEB', nombre:'Rafael', apellido:'Nadal', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (uACMEDB:Usuario {idUsuario:507, nombreUsuario:'directorACMEB', nombre:'Roger', apellido:'Federer', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'TEST', fechaModificacion:localdatetime()})


MERGE (uACMET)-[:TRABAJA{puesto:'tesorero'}]->(acme)
MERGE (uACMEA)-[:TRABAJA{puesto:'programador'}]->(acme)
MERGE (uACMED)-[:TRABAJA{puesto:'director'}]->(acme)

MERGE (uACMETT)-[:TRABAJA{puesto:'tesorero'}]->(acmet)
MERGE (uACMEAT)-[:TRABAJA{puesto:'contador'}]->(acmet)
MERGE (uACMETD)-[:TRABAJA{puesto:'director'}]->(acmet)

MERGE (uACMEAB)-[:TRABAJA{puesto:'administrador'}]->(acmeb)
MERGE (uACMEBD)-[:TRABAJA{puesto:'director'}]->(acmeb)

MERGE (uACMET)-[:TIENE_PERFIL]->(pbr)
MERGE (uACMEA)-[:TIENE_PERFIL]->(pbr)
MERGE (uACMED)-[:TIENE_PERFIL]->(pbr2)

MERGE (uACMETT)-[:TIENE_PERFIL]->(pbr)
MERGE (uACMEAT)-[:TIENE_PERFIL]->(pbr)
MERGE (uACMEDT)-[:TIENE_PERFIL]->(pbr2)

MERGE (uACMEA)-[:TIENE_PERFIL]->(pbr)
MERGE (uACMED)-[:TIENE_PERFIL]->(pbr2)

/* ========================================================================== */
/* =====       E J E M P L O   D E  L A  A P P  C  A  R  T  E  R  A     ===== */
/* ========================================================================== */

/* ##       F A C U L T A D E S   */
MERGE (f1_Cartera:Facultad {nombre:'Cartera_Menu', descripcion:'Presenta la opcion de menu de Cartera', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f2_Cartera:Facultad {nombre:'Cartera_Consulta', descripcion:'Este privilegio da permisos para consultar la cartera', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f3_Cartera:Facultad {nombre:'Cartera_Admin', descripcion:'Este privilegio da permisos para administrar la cartera', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f4_Cartera:Facultad {nombre:'Cartera_Movimientos', descripcion:'Facultad para cargar vector precios', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f5_Cartera:Facultad {nombre:'Admin_Vendedor', descripcion:'Que pueda ver posiciones', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f6_Cartera:Facultad {nombre:'Todos', descripcion:'Que pueda ver todos los sectores', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f7_Cartera:Facultad {nombre:'Gobierno', descripcion:'Que pueda ver sector gobierno', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f8_Cartera:Facultad {nombre:'Financiera', descripcion:'Que pueda ver todos el sector Financiero', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f9_Cartera:Facultad {nombre:'Industrial', descripcion:'Que pueda ver todos el sector Industrial', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f10_Cartera:Facultad {nombre:'Privado', descripcion:'Que pueda ver todos el sector Privado', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})

/* ##       R O L E S     */
MERGE (r1_Cartera:Rol {idRol:500, nombre:'Cartera_Admin', activo:true, usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (r1_Cartera)-[:TIENE_FACULTAD]->(f1_Cartera)
MERGE (r1_Cartera)-[:TIENE_FACULTAD]->(f2_Cartera)
MERGE (r1_Cartera)-[:TIENE_FACULTAD]->(f3_Cartera)
MERGE (r1_Cartera)-[:TIENE_FACULTAD]->(f5_Cartera)
MERGE (r1_Cartera)-[:TIENE_FACULTAD]->(f6_Cartera)

MERGE (r2_Cartera:Rol {idRol:501, nombre:'Cartera_Consulta', activo:true, usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (r2_Cartera)-[:TIENE_FACULTAD]->(f1_Cartera)
MERGE (r2_Cartera)-[:TIENE_FACULTAD]->(f2_Cartera)
MERGE (r2_Cartera)-[:TIENE_FACULTAD]->(f4_Cartera)
MERGE (r2_Cartera)-[:TIENE_FACULTAD]->(f5_Cartera)
MERGE (r2_Cartera)-[:TIENE_FACULTAD]->(f7_Cartera)

/* ##       P E R F I L E S       */
MERGE (p1_Cartera:Perfil {nombre:'Cartera_Consulta', descripcion:'Perfil de administración de cartera', activo:true, patron: true, usuarioModificacion:'TECVAL', fechaModificacion:localdatetime()})
MERGE (p1_Cartera)-[:TIENE_ROL]->(r1_Cartera)

MERGE (p2_Cartera:Perfil {nombre:'Cartera_Admin', descripcion:'Perfil de consulta de cartera', activo:true, patron: true, usuarioModificacion:'TECVAL', fechaModificacion:localdatetime()})
MERGE (p2_Cartera)-[:TIENE_ROL]->(r2_Cartera)

MERGE (p2_Cartera)-[:TIENE_ROL]->(r2_Cartera)



/* ##       U S U A R I O S  Y  U S U A R I O S  A  P E R F I L E S      */
                  
MERGE (u1_Cartera:Usuario {idUsuario:1000, nombreUsuario:'adminCartera', nombre:'Maria', apellido:'Shaparova', telefono:"5591495040",
      mail:"rramirez@legosoft.com.mx", interno:true, activo:true, administrador:false, fechaIngreso:date(),
      zonaHoraria: 'America/Mexico', usuarioModificacion:'TECVAL', fechaModificacion:localdatetime()})
MERGE (u1_Cartera)-[:TIENE_PERFIL]->(p1_Cartera)

MERGE (u2_Cartera:Usuario {idUsuario:1001, nombreUsuario:'consultaCartera', nombre:'Billie', apellido:'Jean King', telefono:"5591495040",
      mail:"rramirez@legosoft.com.mx", interno:true, activo:true, administrador:false, fechaIngreso:date(),
      zonaHoraria: 'America/Mexico', usuarioModificacion:'TECVAL', fechaModificacion:localdatetime()})
MERGE (u2_Cartera)-[:TIENE_PERFIL]->(p2_Cartera)

/* ##       U S U A R I O S  A  E M P R E S A S      */
MERGE (u1_Cartera)-[:TRABAJA{puesto:'administrador'}]->(lmass)
MERGE (u2_Cartera)-[:TRABAJA{puesto:'usuario'}]->(lmass)


/* ============================================================ */
/* =====      C  A  P  T  U  R  A    M  A  N  U  A  L     ===== */
/* ============================================================ */

/* ##       F A C U L T A D E S   */
MERGE (fcma:Facultad {nombre:'Captura_Eventos_Corporativos', descripcion:'Captura manual eventos corporativos', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (fcmb:Facultad {nombre:'Captura_Eventos_Corporativos_Admin', descripcion:'Administración de captura manual eventos corporativos', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (fcmd:Facultad {nombre:'Captura_Operaciones', descripcion:'Captura manual de operaciones', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (fcme:Facultad {nombre:'Captura_Operaciones_Admin', descripcion:'Administración para la captura manual de operaciones', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})

/* ##       R O L E S     */
MERGE (rcmec:Rol {idRol:600, nombre:'Eventos_Corporativos', activo:true, usuarioModificacion:'TEST', fechaModificacion:localdatetime()})
MERGE (rcmop:Rol {idRol:601, nombre:'Operaciones', activo:true, usuarioModificacion:'TEST', fechaModificacion:localdatetime()})

MERGE (rcmec)-[:TIENE_FACULTAD]->(fcma)
MERGE (rcmec)-[:TIENE_FACULTAD]->(fcmb)
MERGE (rcmop)-[:TIENE_FACULTAD]->(fcmd)
MERGE (rcmop)-[:TIENE_FACULTAD]->(fcme)

/* ##       P E R F I L E S       */
MERGE (pbr)-[:TIENE_ROL]->(rcmec)
MERGE (pbr)-[:TIENE_ROL]->(rcmop)
MERGE (pbr2)-[:TIENE_ROL]->(rcmec)

/* ============================================================ */
/* =====      U D F U I                                   ===== */
/* ============================================================ */

/* ##       F A C U L T A D E S   */
MERGE (f1_UDFUI:Facultad {nombre:'Captura_UDF', descripcion:'Facultad para la captura de UDFs', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f2_UDFUI:Facultad {nombre:'Captura_Microservicio', descripcion:'Facultad para la captura de un microservcio en UDFs', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})
MERGE (f3_UDFUI:Facultad {nombre:'Captura_UDF_Admin', descripcion:'Facultad para la administración de UDFs', tipo:"FACULTAD_SIMPLE",usuarioModificacion:'TEST', fechaModificacion:localdatetime(), activo: true})

/* ##       R O L E S     */
MERGE (r1_UDFUI:Rol {idRol:550, nombre:'Udf_Admin', activo:true, usuarioModificacion:'TECVAL', fechaModificacion:localdatetime()})
MERGE (r1_UDFUI)-[:TIENE_FACULTAD]->(f1_UDFUI)
MERGE (r1_UDFUI)-[:TIENE_FACULTAD]->(f2_UDFUI)
MERGE (r1_UDFUI)-[:TIENE_FACULTAD]->(f3_UDFUI)

/* No se da de alta un perfila para el rol r1_UDF1 se utiliza el perfile de admin_IAM */
MERGE (pIAM)-[:TIENE_ROL]->(r1_UDFUI)

/* ################################################### */
/* #####        S U P E R  U S U A R I O S        #### */
/* ################################################### */

;


/* ========================================================================= */
/*                 C O N S T R A I N T S                                     */

SHOW INDEXES
DROP CONSTRAINT unique_compania;

CREATE CONSTRAINT unique_compania FOR (compania:Compania) REQUIRE compania.nombre IS UNIQUE


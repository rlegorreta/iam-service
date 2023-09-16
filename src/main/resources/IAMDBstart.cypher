/* ======================================================================== */
/* This script is to initialize the IAM Neo4j database minimum data         */
/* All data from the start.cypher is added plus some developer data needed  */
/* for testing purposes:                                                    */
/* Administrator: AdminIAM.                                                 */
/* AdminIAM belongs to Admin Grupo IAM group.                               */
/* Admin Grupo IAM permits to use de LMASS company.                         */
/*                                                                          */
/* Three profiles are created:                                              */
/* a) Administrador IAM: unique profile just for the AdminIAM user.         */
/* b) Administrador Maestro: Profile for the Corporate master Administrator */
/* c) Administrator: profile for an Administrator for one Company(or more)  */
/*                                                                          */
/* Three roles are created:                                                 */
/* a) Admin IAM: Role for the the IAM Profile. Unique role.                 */
/* b) Admin Maestro: Role for master corporate Administrators.              */
/* c) Admin: Role for normal administrators.                                */
/*                                                                          */
/* Four permits are created                                                 */
/* a) CREAR_CORPORATIVO: Permit just for new Corporates (not used yet).     */
/* b) ADMIN_IAM: Unique permit for IAM administrator pages like facultades, */
/*               roles, permisos y vista.                                   */
/* c) ADMIN: Master permit to see all possible Administrators for one       */
/*           Corporate.                                                     */
/* d) ADMIN_EMP: Permit to Administrate single or more Companies.           */
/*                                                                          */
/* Set the system date to companies LMASS                                   */
/*                                                                          */
/* And more data for developer purpose....                                  */
/*                                                                          */
/* Date: July 2022                                                          */
/* ======================================================================== */


/* ======================================================= */
/* =====     C O M P A N I E S  LMASS                ===== */
/* ======================================================= */
MERGE (lmass:Compania {nombre:'LMASS Desarrolladores', padre:true, negocio:'NA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (aiml:Compania {nombre:'AI/ML SA de CV', padre:false, negocio:'NA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:0})

/* =====     S U B S I D I A R Y E S    ===== */
MERGE (aiml)-[:SUBSIDIARIA]-(lmass)

/* =====     O P E R A T I O N   D A T E S  B Y  C O M P A N Y     ===== */
MERGE (sistLMASS:Sistema {compania:'LMASS Desarrolladores', fechaOperacion:date(), fechaModificacion:localdatetime(), estatus:'EN_OPERACION'})
MERGE (sistAIML:Sistema {compania:'AI/ML SA de CV', fechaOperacion:date(), fechaModificacion:localdatetime(), estatus:'EN_OPERACION'})

/* =====     G R O U P   A D M I N I S T R A T O R     ===== */
MERGE (gLMASS:Grupo {nombre:'Admin LMASS', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true})
MERGE (gAIML:Grupo {nombre:'Admin AI/ML', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true})

/* =====     R E L A T I O N   G R U O P - C O M P A N Y     ===== */
MERGE (gLMASS)-[:PERMITE]->(lmass)
MERGE (gAIML)-[:PERMITE]->(aiml)

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

/* =====     A D M I N I S T R A T O R   P R O F I L E S   ===== */
MERGE (pIAM:Perfil {nombre:'Administrador IAM', descripcion:'Puesto único de administrador de la IAM',
                 activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pMaestro:Perfil {nombre:'Administrador Maestro', descripcion:'Puesto único de administrador de un Corporativo',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pAdmin:Perfil {nombre:'Administrador', descripcion:'Puesto de administrador de una o varias empresas',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})

/* =====     R E L A T I O N S H I P  B E T W E E N   U S E R - P R O F I L E      ===== */
MERGE (u)-[:TIENE_PERFIL]->(pIAM)
MERGE (uLMASS)-[:TIENE_PERFIL]->(pMaestro)
MERGE (uLMASS)-[:TIENE_PERFIL]->(pAdmin)
MERGE (uAIML)-[:TIENE_PERFIL]->(pAdmin)

/* =====     A D M I N I S T R A T O R     R O L E S ===== */
MERGE (rIAM:Rol {idRol:0, nombre:'Admin IAM', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rMaestro:Rol {idRol:1, nombre:'Admin Maestro', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rAdmin:Rol {idRol:2, nombre:'Admin', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})

/* =====     R E L A T I O N S H I P  B E T W E E N  P R O F I L E - R O L     ===== */
MERGE (pIAM)-[:TIENE_ROL]->(rIAM)
MERGE (pMaestro)-[:TIENE_ROL]->(rMaestro)
MERGE (pAdmin)-[:TIENE_ROL]->(rAdmin)

/* =====     P E R M I T S     ===== */
MERGE (fa:Facultad {nombre:'Crear_Corporativo', descripcion:'Facultad para crear nuevos corporativos',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fb:Facultad {nombre:'Admin_IAM', descripcion:'Facultad para la administración del IAM. Solo un usuario debe de tener esta facultad',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fc:Facultad {nombre:'Admin', descripcion:'Facultad para la administración de Corporativos. Solo un usuario debe de tener esta facultad',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fd:Facultad {nombre:'Admin_Emp', descripcion:'Facultad para la administración de Empresas',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fsys:Facultad {nombre:'ADMIN_SISTEMA', descripcion:'Facultad para la modificación de parámetros del sistema',
       tipo:"FACULTAD_SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (foauth:Facultad {nombre:'SERVIDOR_TOKEN', descripcion:'Facultad para consultar usuarios en el OAuth server',
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

;

/* ======================================================================== */
/* This script is to initialize the IAM Neo4j database for testing purpose  */
/* All data from the start.cypher is added plus some developer data needed  */
/* for testing purposes:                                                    */
/*                                                                          */
/* Date: September 2023                                                      */
/* ======================================================================== */

/* ======================================================= */
/* =====     C O M P A N I E S  LMASS & ACME         ===== */
/* ======================================================= */
MERGE (lmass:Compania {nombre:'LMASS Desarrolladores SA de CV', padre:true, negocio:'NA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (aiml:Compania {nombre:'AI/ML SA de CV', padre:false, negocio:'NA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:0})
MERGE (acme:Compania {nombre:'ACME SA de CV', padre:true, negocio:'INDUSTRIAL', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:1})
MERGE (acmet:Compania {nombre:'ACME Tienda SA de CV', padre:false, negocio:'INDUSTRIAL', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:2})
MERGE (acmeb:Compania {nombre:'ACME Bodega SA de CV', padre:false, negocio:'INDUSTRIAL', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:3})
MERGE (prov1:Compania {nombre:'AMAZON SA DE CV', padre:true, negocio:'INDUSTRIAL', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:4})
MERGE (prov2:Compania {nombre:'ABOGADOS SC', padre:true, negocio:'PARTICULAR', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:5})
MERGE (ixe:Compania {nombre:'IXE BANCO', padre:true, negocio:'FINANCIERA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:6})
MERGE (intercam:Compania {nombre:'INTERCAM BANCO', padre:true, negocio:'FINANCIERA', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idPersona:7})

/* =====     S U B S I D I A R Y E S    ===== */
MERGE (aiml)-[:SUBSIDIARIA]->(lmass)
MERGE (acmet)-[:SUBSIDIARIA]->(acme)
MERGE (acmeb)-[:SUBSIDIARIA]->(acme)

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

/* =====     R E L A T I O N   G R O U P - C O M P A N Y     ===== */
MERGE (gLMASS)-[:PERMITE]->(lmass)
MERGE (gAIML)-[:PERMITE]->(aiml)
MERGE (gACME)-[:PERMITE]->(acme)
MERGE (gACMET)-[:PERMITE]->(acmet)
MERGE (gACMEB)-[:PERMITE]->(acmeb)

/* =====     A D M I N I S T R A T O R S  U S E R S     ===== */
MERGE (uDios:Usuario {idUsuario:0, nombreUsuario:'adminALL', nombre:'Dios', apellido:'Mkt.place', telefono:"5591495040",
                  mail:"god@legosoft.com.mx", interno:true, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uDios)-[:MIEMBRO]->(gLMASS)
MERGE (uDios)-[:TRABAJA{puesto:'dios'}]->(lmass)

MERGE (uIAM:Usuario {idUsuario:1, nombreUsuario:'adminIAM', nombre:'Administrador', apellido:'IAM', telefono:"5591495040",
                  mail:"iam@legosoft.com.mx", interno:true, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uIAM)-[:MIEMBRO]->(gLMASS)
MERGE (uIAM)-[:TRABAJA{puesto:'administrador'}]->(lmass)

MERGE (uLMASS:Usuario {idUsuario:2, nombreUsuario:'adminLEGO', nombre:'Administrador ', apellido:'LegoSoft', telefono:"5591495040",
                  mail:"staff@lmass.com.mx", interno:true, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uLMASS)-[:MIEMBRO]->(gLMASS)
MERGE (uLMASS)-[:TRABAJA{puesto:'administrador'}]->(lmass)

MERGE (uAIML:Usuario {idUsuario:3, nombreUsuario:'adminAIML', nombre:'Administrador', apellido:'AI/ML', telefono:"5591495040",
                  mail:"admin@aiml.com.mx", interno:true, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAIML)-[:MIEMBRO]->(gAIML)
MERGE (uAIML)-[:TRABAJA{puesto:'administrador'}]->(aiml)

MERGE (uAdminACME:Usuario {idUsuario:4, nombreUsuario:'adminMACME', nombre:'Administrador Maestro', apellido:'Maestro', telefono:"5591495040",
                  mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAdminACME)-[:MIEMBRO]->(gACME)
MERGE (uAdminACME)-[:TRABAJA{puesto:'administrador'}]->(acme)

MERGE (uAACME:Usuario {idUsuario:5, nombreUsuario:'adminACME', nombre:'Director General', apellido:'General', telefono:"5591495040",
                  mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAACME)-[:MIEMBRO]->(gACME)
MERGE (uAACME)-[:TRABAJA{puesto:'director'}]->(acme)

MERGE (uAACMET:Usuario {idUsuario:6, nombreUsuario:'adminACMET', nombre:'Administrador ACME Tienda', apellido:'ACME', telefono:"5591495040",
                  mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAACMET)-[:MIEMBRO]->(gACMET)
MERGE (uAACMET)-[:TRABAJA{puesto:'administrador'}]->(acmet)

MERGE (uAACMEB:Usuario {idUsuario:7, nombreUsuario:'adminACMEB', nombre:'Administrador ACME Bodega', apellido:'ACME', telefono:"5591495040",
                  mail:"staff@acme.com.mx", interno:false, activo:true, administrador: true, fechaIngreso:date(),
                  zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uAACMEB)-[:MIEMBRO]->(gACMEB)
MERGE (uAACMEB)-[:TRABAJA{puesto:'administrador'}]->(acmeb)


/* =====     A D M I N I S T R A T O R   P R O F I L E S   ===== */
MERGE (pDios:Perfil {nombre:'Dios del mrk.place', descripcion:'Acceso ilimitado al Mrk.place',
                 activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pDiosSimple:Perfil {nombre:'Dios simple del mrk.place', descripcion:'Acceso ilimitado al Mrk.place',
                 activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pRecHum:Perfil {nombre:'Recursos humanos', descripcion:'Administrador del IAM',
                  activo:true, patron: false, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pDirLego:Perfil {nombre:'Direccion LegoSoft', descripcion:'Dirección general de la empresa LegoSoft',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pDirACME:Perfil {nombre:'Dirección ACME', descripcion:'Dirección de la empresa ACME',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pCartera:Perfil {nombre:'Cartera', descripcion:'Usuario de la operación de fondos',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
/* This profiles MUST exists in the database in order to insert new Company Corporation and new Adinistrators */
MERGE (pAdminMaestro:Perfil {nombre:'Administrador Maestro', descripcion:'Perfil de administrador maestro de un Corporativo',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pAdmin:Perfil {nombre:'Administrador', descripcion:'Perfil de administrador de una Empresa',
                  activo:true, patron: true, usuarioModificacion:'START', fechaModificacion:localdatetime()})

/* =====     R E L A T I O N S H I P  B E T W E E N   U S E R - P R O F I L E      ===== */
MERGE (uDios)-[:TIENE_PERFIL]->(pDios)
MERGE (uIAM)-[:TIENE_PERFIL]->(pRecHum)
MERGE (uLMASS)-[:TIENE_PERFIL]->(pDirLego)
MERGE (uAIML)-[:TIENE_PERFIL]->(pDirLego)
MERGE (uAdminACME)-[:TIENE_PERFIL]->(pAdminMaestro)
MERGE (uAACME)-[:TIENE_PERFIL]->(pDirACME)
MERGE (uAACMET)-[:TIENE_PERFIL]->(pDirACME)
MERGE (uAACMEB)-[:TIENE_PERFIL]->(pDirACME)

/* =====     A D M I N I S T R A T O R     R O L E S ===== */
MERGE (rDios:Rol {idRol:100, nombre:'Dios', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rIAM:Rol {idRol:0, nombre:'AdminIAM', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rUserIAM:Rol {idRol:1, nombre:'UserIAM', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rDevOp:Rol {idRol:3, nombre:'devOp', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rParamSys:Rol {idRol:4, nombre:'paramSys', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rLegal:Rol {idRol:5, nombre:'legal', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rAdminACME:Rol {idRol:6, nombre:'adminACME', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rNormatividad:Rol {idRol:7, nombre:'normatividad', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rRevisionDocs:Rol {idRol:8, nombre:'revisionDocs', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rRevisionLegalDocs:Rol {idRol:9, nombre:'revisionLegalDocs', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rExpedientes:Rol {idRol:10, nombre:'expedientes', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rOrdenes:Rol {idRol:11, nombre:'ordenes', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rAdminLEGO:Rol {idRol:12, nombre:'adminLEGO', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (rCartera:Rol {idRol:13, nombre:'cartera', activo:true, usuarioModificacion:'START', fechaModificacion:localdatetime()})

/* =====     R E L A T I O N S H I P  B E T W E E N  P R O F I L E - R O L     ===== */
MERGE (pDios)-[:TIENE_ROL]->(rIAM)
MERGE (pDios)-[:TIENE_ROL]->(rDevOp)
MERGE (pDios)-[:TIENE_ROL]->(rParamSys)
MERGE (pDios)-[:TIENE_ROL]->(rLegal)
MERGE (pDios)-[:TIENE_ROL]->(rAdminACME)
MERGE (pDios)-[:TIENE_ROL]->(rNormatividad)
MERGE (pDios)-[:TIENE_ROL]->(rRevisionDocs)
MERGE (pDios)-[:TIENE_ROL]->(rRevisionLegalDocs)
MERGE (pDios)-[:TIENE_ROL]->(rExpedientes)
MERGE (pDios)-[:TIENE_ROL]->(rOrdenes)
MERGE (pDios)-[:TIENE_ROL]->(rAdminLEGO)
MERGE (pDios)-[:TIENE_ROL]->(rCartera)
MERGE (pDiosSimple)-[:TIENE_ROL]->(rDios)
MERGE (pRecHum)-[:TIENE_ROL]->(rIAM)
MERGE (pRecHum)-[:TIENE_ROL]->(rDevOp)
MERGE (pDirLego)-[:TIENE_ROL]->(rUserIAM)
MERGE (pDirLego)-[:TIENE_ROL]->(rAdminLEGO)
MERGE (pDirLego)-[:TIENE_ROL]->(rDevOp)
MERGE (pDirLego)-[:TIENE_ROL]->(rParamSys)
MERGE (pDirACME)-[:TIENE_ROL]->(rUserIAM)
MERGE (pDirACME)-[:TIENE_ROL]->(rAdminACME)
MERGE (pDirACME)-[:TIENE_ROL]->(rNormatividad)
MERGE (pDirACME)-[:TIENE_ROL]->(rRevisionDocs)
MERGE (pDirACME)-[:TIENE_ROL]->(rRevisionLegalDocs)
MERGE (pDirACME)-[:TIENE_ROL]->(rExpedientes)
MERGE (pDirACME)-[:TIENE_ROL]->(rDevOp)
MERGE (pDirACME)-[:TIENE_ROL]->(rOrdenes)
MERGE (pCartera)-[:TIENE_ROL]->(rCartera)
MERGE (pAdminMaestro)-[:TIENE_ROL]->(rUserIAM)
MERGE (pAdmin)-[:TIENE_ROL]->(rUserIAM)

/* =====     P E R M I T S     ===== */
MERGE (fGod:Facultad {nombre:'ALL', descripcion:'Acceso ilimitado al mkt placeAcceso ilimitado al mkt place',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fAdminIAM:Facultad {nombre:'adminIAM', descripcion:'Administración total del IAM',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fUserIAM:Facultad {nombre:'userIAM', descripcion:'Administración del IAM solo de usuarios',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fMasterAdmin:Facultad {nombre:'masterAdmin', descripcion:'Facultad para ser un administrador master en los corporativos',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fadminLEGO:Facultad {nombre:'adminLEGO', descripcion:'Facultad para la poder enviar notifiaciones manuales',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fTemplates:Facultad {nombre:'templates', descripcion:'Facultad para editar templates',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fDataSource:Facultad {nombre:'datasources', descripcion:'Facultad para editar datasource del ingestor',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fMonitorMail:Facultad {nombre:'monitorMail', descripcion:'Facultad enviar email manualment como monitoreo',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fAdminUDF:Facultad {nombre:'adminUDF', descripcion:'Facultad para administrar UDFs',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fCartera:Facultad {nombre:'cartera', descripcion:'Facultad para la consulta de los fondos',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fAuditoria:Facultad {nombre:'auditoria', descripcion:'Facultad para la consulta de la audtoría del Mrk.place',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fCache:Facultad {nombre:'cache', descripcion:'Facultad la prueba de funcionalidad del Caché',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fFechasSYS:Facultad {nombre:'fechasSYS', descripcion:'Facultad para el calendario de fechas del Mrk.place',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fTasasSYS:Facultad {nombre:'tasasSYS', descripcion:'Facultad para el catálogo de tasas',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fDocsSYS:Facultad {nombre:'docsSYS', descripcion:'Facultad para el catálogo de tipos de documentos',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fSmartContract:Facultad {nombre:'smartContract', descripcion:'Facultad para la definición de tipos de contratos',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fSectoresEmpresariales:Facultad {nombre:'sectoresEmpresariales', descripcion:'Facultad para el calendarios de sectores empresariales',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fCompanias:Facultad {nombre:'companias', descripcion:'Facultad para el mantenimiento de Personas Morales',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fPersonas:Facultad {nombre:'personas', descripcion:'Facultad para el mantenimiento de Personas Físicas',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fRelaciones:Facultad {nombre:'relaciones', descripcion:'Facultad para relacionar compañías y personas entre si',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fTareasPendientes:Facultad {nombre:'tareasPendientes', descripcion:'Facultad para la consulta de todas las tareas pendientes',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fRecepcionDocs:Facultad {nombre:'recepcionDocs', descripcion:'Facultad para la recepción de documentos',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fRevisionDocs:Facultad {nombre:'revisionDocs', descripcion:'Facultad para la revisión de documetos',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fRevisionLegalDocs:Facultad {nombre:'revisionLegalDocs', descripcion:'Facultad para la revisión de documetos legales',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fExpedientes:Facultad {nombre:'expedientes', descripcion:'Facultad para la consulta de expedientes',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (fOrdenes:Facultad {nombre:'ordenes', descripcion:'Facultad para la consulta de ordenes venidas del ingestor',
       tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})


/* =====     R E L A T I O N S H I P  B E T W E E N   R O L E S - P E R M I T S   ===== */
MERGE (rDios)-[:TIENE_FACULTAD]->(fGod)
MERGE (rIAM)-[:TIENE_FACULTAD]->(fAdminIAM)
MERGE (rUserIAM)-[:TIENE_FACULTAD]->(fAdminIAM)
MERGE (rAdminLEGO)-[:TIENE_FACULTAD]->(fadminLEGO)
MERGE (rAdminLEGO)-[:TIENE_FACULTAD]->(fTemplates)
MERGE (rAdminLEGO)-[:TIENE_FACULTAD]->(fDataSource)
MERGE (rAdminLEGO)-[:TIENE_FACULTAD]->(fMonitorMail)
MERGE (rCartera)-[:TIENE_FACULTAD]->(fCartera)
MERGE (rDevOp)-[:TIENE_FACULTAD]->(fAuditoria)
MERGE (rDevOp)-[:TIENE_FACULTAD]->(fCache)
MERGE (rParamSys)-[:TIENE_FACULTAD]->(fFechasSYS)
MERGE (rParamSys)-[:TIENE_FACULTAD]->(fTasasSYS)
MERGE (rParamSys)-[:TIENE_FACULTAD]->(fDocsSYS)
MERGE (rLegal)-[:TIENE_FACULTAD]->(fSmartContract)
MERGE (rAdminACME)-[:TIENE_FACULTAD]->(fSectoresEmpresariales)
MERGE (rAdminACME)-[:TIENE_FACULTAD]->(fCompanias)
MERGE (rAdminACME)-[:TIENE_FACULTAD]->(fPersonas)
MERGE (rAdminACME)-[:TIENE_FACULTAD]->(fRelaciones)
MERGE (rAdminACME)-[:TIENE_FACULTAD]->(fTareasPendientes)
MERGE (rNormatividad)-[:TIENE_FACULTAD]->(fRecepcionDocs)
MERGE (rRevisionDocs)-[:TIENE_FACULTAD]->(fRevisionDocs)
MERGE (rRevisionLegalDocs)-[:TIENE_FACULTAD]->(fRevisionLegalDocs)
MERGE (rExpedientes)-[:TIENE_FACULTAD]->(fExpedientes)
MERGE (rOrdenes)-[:TIENE_FACULTAD]->(fOrdenes)


/* ===== E X T R A  P E R M I T S ===== */
MERGE (uIAM)-[:FACULTAD_EXTRA]->(fOrdenes)
MERGE (uDios)-[:FACULTAD_EXTRA]->(fMasterAdmin)
MERGE (uIAM)-[:FACULTAD_EXTRA]->(fMasterAdmin)
MERGE (uLMASS)-[:FACULTAD_EXTRA]->(fMasterAdmin)
MERGE (uAACME)-[:FACULTAD_EXTRA]->(fMasterAdmin)


/* ========================================================================= */
/* =====       E N D  O F  M I N I M U M  D A T A  R E Q U I R E D     ===== */
/* ========================================================================= */


/* =====     U S E R S   P R O F I L E S   ===== */

MERGE (pTesACME:Perfil {nombre:'Tesorero', descripcion:'Tesorero de la empresa ACME',
                  activo:true, patron: false, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pNormatividad:Perfil {nombre:'Normatividad', descripcion:'Normatividad en LegoSoft',
                  activo:true, patron: false, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pAbogado:Perfil {nombre:'Abogado', descripcion:'Abogado en LegoSoft',
                  activo:true, patron: false, usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (pPromotor:Perfil {nombre:'Promotor', descripcion:'Promotor en LegoSoft',
                  activo:true, patron: false, usuarioModificacion:'START', fechaModificacion:localdatetime()})

MERGE (aACMET:Area {nombre:'Tesorería ACME', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:1, idPersona:500})
MERGE (aACMEA:Area {nombre:'Administración ACME', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:2, idPersona: 500})
MERGE (aACMED:Area {nombre:'Dirección ACME', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:3, idPersona:500})
MERGE (aACMET)-[:CONTIENE]-(acme)
MERGE (aACMEA)-[:CONTIENE]-(acme)
MERGE (aACMED)-[:CONTIENE]-(acme)
MERGE (aaACMET:AreaAsignada {nombre:'Tesorería ACME', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:1, idPersona:501})
MERGE (aaACMEA:AreaAsignada {nombre:'Administración ACME', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:2, idPersona:501})
MERGE (aaACMED:AreaAsignada {nombre:'Dirección ACME', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:3, idPersona:501})

MERGE (aACMETT:Area {nombre:'Tesorería ACME Tienda', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:4, idPersona:502})
MERGE (aACMEAT:Area {nombre:'Administración ACME Tienda', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:5, idPersona: 502})
MERGE (aACMEDT:Area {nombre:'Dirección ACME Tienda', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:6, idPersona:502})
MERGE (aACMETT)-[:CONTIENE]-(acmet)
MERGE (aACMEAT)-[:CONTIENE]-(acmet)
MERGE (aACMEDT)-[:CONTIENE]-(acmet)
MERGE (aaACMETT:AreaAsignada {nombre:'Tesorería ACME Tienda', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:4, idPersona:502})
MERGE (aaACMEAT:AreaAsignada {nombre:'Administración ACME Tienda', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:5, idPersona:502})
MERGE (aaACMEDT:AreaAsignada {nombre:'Dirección ACME Tienda', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:6, idPersona:502})

MERGE (aACMEAB:Area {nombre:'Administración ACME Bodega', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:7, idPersona: 502})
MERGE (aACMEDB:Area {nombre:'Dirección ACME Bodega', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:8, idPersona:502})
MERGE (aACMEAB)-[:CONTIENE]-(acmeb)
MERGE (aACMEDB)-[:CONTIENE]-(acmeb)
MERGE (aaACMEAB:AreaAsignada {nombre:'Administración ACME Bodega', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:7, idPersona:502})
MERGE (aaACMEDB:AreaAsignada {nombre:'Dirección ACME Bodega', usuarioModificacion:'START', fechaModificacion:localdatetime(), activo:true, idArea:8, idPersona:502})

/* =====     O T H E R   U S E R S     ===== */
MERGE (uACMET:Usuario {idUsuario:500, nombreUsuario:'tesACME', nombre:'Pato', apellido:'Donald', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uACMETT:Usuario {idUsuario:501, nombreUsuario:'tesACMET', nombre:'Steven', apellido:'Jobs', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uACMETB:Usuario {idUsuario:502, nombreUsuario:'tesACMEB', nombre:'Bill', apellido:'Gates', telefono:"5591495040",
                       mail:"staff@acme.com.mx", interno:false, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uUserNorm:Usuario {idUsuario:503, nombreUsuario:'userNORM', nombre:'John', apellido:'McCarthy', telefono:"5591495040",
                       mail:"staff@legosoft.com.mx", interno:true, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uUserLegal:Usuario {idUsuario:504, nombreUsuario:'userLEGAL', nombre:'Rafael', apellido:'Nadal', telefono:"5591495040",
                       mail:"staff@legosoft.com.mx", interno:true, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})
MERGE (uUserVtas:Usuario {idUsuario:505, nombreUsuario:'userVTAS', nombre:'Roger', apellido:'Federer', telefono:"5591495040",
                       mail:"staff@legosoft.com.mx", interno:true, activo:true, administrador: false,
                       fechaIngreso:date(),
                       zonaHoraria: 'America/Mexico', usuarioModificacion:'START', fechaModificacion:localdatetime()})


MERGE (uACMET)-[:TRABAJA{puesto:'tesorero'}]->(acme)
MERGE (uACMETT)-[:TRABAJA{puesto:'tesorero'}]->(acmet)
MERGE (uACMETB)-[:TRABAJA{puesto:'tesorero'}]->(acmeb)

MERGE (uUserNorm)-[:TRABAJA{puesto:'tesorero'}]->(lmass)
MERGE (uUserLegal)-[:TRABAJA{puesto:'tesorero'}]->(lmass)
MERGE (uUserVtas)-[:TRABAJA{puesto:'tesorero'}]->(aiml)

/* ===== P R O F I L E S  T O  U S E R S ===== */
MERGE (uACMET)-[:TIENE_PERFIL]->(pTesACME)
MERGE (uACMETT)-[:TIENE_PERFIL]->(pTesACME)
MERGE (uACMETB)-[:TIENE_PERFIL]->(pTesACME)

MERGE (uUserNorm)-[:TIENE_PERFIL]->(pNormatividad)
MERGE (uUserLegal)-[:TIENE_PERFIL]->(pAbogado)
MERGE (uUserVtas)-[:TIENE_PERFIL]->(pPromotor)

/* ===== R O L E S  T O  P R O F I L E S ===== */
MERGE (pTesACME)-[:TIENE_ROL]->(rAdminACME)
MERGE (pNormatividad)-[:TIENE_ROL]->(rNormatividad)
MERGE (pNormatividad)-[:TIENE_ROL]->(rRevisionDocs)
MERGE (pNormatividad)-[:TIENE_ROL]->(rExpedientes)
MERGE (pAbogado)-[:TIENE_ROL]->(rLegal)
MERGE (pAbogado)-[:TIENE_ROL]->(rRevisionLegalDocs)
MERGE (pAbogado)-[:TIENE_ROL]->(rExpedientes)
MERGE (pPromotor)-[:TIENE_ROL]->(rNormatividad)
MERGE (pPromotor)-[:TIENE_ROL]->(rExpedientes)
MERGE (pPromotor)-[:TIENE_ROL]->(rOrdenes)


/* ========================================================================== */
/* =====       E J E M P L O   D E  L A  A P P  C  A  R  T  E  R  A     ===== */
/* ========================================================================== */


/* ##       U S U A R I O S  Y  U S U A R I O S  A  P E R F I L E S      */

MERGE (u1_Cartera:Usuario {idUsuario:1000, nombreUsuario:'userCartera', nombre:'Maria', apellido:'Shaparova', telefono:"5591495040",
      mail:"rramirez@legosoft.com.mx", interno:false, activo:true, administrador:false, fechaIngreso:date(),
      zonaHoraria: 'America/Mexico', usuarioModificacion:'TECVAL', fechaModificacion:localdatetime()})
MERGE (u1_Cartera)-[:TIENE_PERFIL]->(pCartera)


/* ##       U S U A R I O S  A  E M P R E S A S      */
MERGE (u1_Cartera)-[:TRABAJA{puesto:'administrador'}]->(lmass)


/* ============================================================ */
/* =====      U D F U I                                   ===== */
/* ============================================================ */

/* ##       F A C U L T A D E S   */
MERGE (f1_UDFUI:Facultad {nombre:'Captura_UDF', descripcion:'Facultad para la captura de UDFs', tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (f2_UDFUI:Facultad {nombre:'Captura_Microservicio', descripcion:'Facultad para la captura de un microservcio en UDFs', tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})
MERGE (f3_UDFUI:Facultad {nombre:'Captura_UDF_Admin', descripcion:'Facultad para la administración de UDFs', tipo:"SIMPLE",usuarioModificacion:'START', fechaModificacion:localdatetime(), activo: true})

/* ##       R O L E S     */
MERGE (r1_UDFUI:Rol {idRol:550, nombre:'adminUDF', activo:true, usuarioModificacion:'TECVAL', fechaModificacion:localdatetime()})
MERGE (r1_UDFUI)-[:TIENE_FACULTAD]->(f1_UDFUI)
MERGE (r1_UDFUI)-[:TIENE_FACULTAD]->(f2_UDFUI)
MERGE (r1_UDFUI)-[:TIENE_FACULTAD]->(f3_UDFUI)

/* No se da de alta un perfil para el rol r1_UDF1 se utiliza el perfile de admin_IAM */
MERGE (pDios)-[:TIENE_ROL]->(r1_UDFUI)
MERGE (pDirLego)-[:TIENE_ROL]->(r1_UDFUI)

/* ################################################### */
/* #####        S U P E R  U S U A R I O S        #### */
/* ################################################### */

;

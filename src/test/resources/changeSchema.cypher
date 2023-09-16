/* ======================================================================== */
/* In order to keep un sync with Companias (i.e. BUP                        */
/* micro.services and authorization is needed so we add a new attribute     */
/* for Companias called 'activo'. This script just updates the              */
/* schema and adds the attribute.                                           */
/*                                                                          */
/* For authorization we need to store the IdPersona and the IdCompania      */
/*                                                                          */
/* Date: January 2022                                                       */
/* ======================================================================== */
MATCH (c:Compania) set c.activo = true RETURN c

MATCH (c:Compania) set c.idParticipante = -1 RETURN c


/* Copyright (c) 2023, LegoSoft Soluciones, S.C.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are not permitted.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *  UsuarioRepository.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.repository

import com.ailegorreta.iamservice.model.Usuario
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Interface de Neo4j repository for Entity User.
 *
 * @see Spring-data for more information
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Repository
interface UsuarioRepository : Neo4jRepository<Usuario, Long> {

    override fun findById(id: Long): Optional<Usuario>

    override fun count(): Long

    override fun findAll(): List<Usuario>

    override fun findAll(pageable: Pageable): Page<Usuario>

    fun findByNombreUsuario(@Param("nombreUsuario")nombre: String): Usuario?

    fun findByIdUsuario(@Param("idUsuario")idUsuario: Long): Usuario?

    fun findByMail(@Param("mail") mail: String): Usuario?

    fun findByInternoIsAndAdministradorIs(@Param("interno")interno: Boolean,
                                          @Param("administrador") administrador: Boolean): Collection<Usuario>

    @Query("""
        MATCH(u:Usuario) 
        where u.activo = ${'$'}activo 
        RETURN count(u)
    """)
    fun countByActivoIs(activo : Boolean): Long

    fun findByActivoIs(@Param("activo")activo: Boolean, pageable: Pageable): Page<Usuario>

    @Query("""
        MATCH(u:Usuario) 
        where u.nombreUsuario CONTAINS ${'$'}nombre 
        RETURN count(u)
    """)
    fun countByNombreUsuarioContains(@Param("nombre")nombre: String): Long

    fun findByNombreUsuarioContains(@Param("nombreUsuario")nombreUsuario: String, pageable: Pageable): Page<Usuario>

    @Query("""
        MATCH(u:Usuario) 
        where u.nombreUsuario CONTAINS ${'$'}nombreUsuario AND u.activo = ${'$'}activo
         RETURN count(u)
     """)
    fun countByNombreUsuarioContainsAndActivoIs(@Param("nombreUsuario")nombreUsuario: String,
                                                @Param("activo")activo: Boolean): Long

    fun findByNombreUsuarioContainsAndActivoIs(@Param("nombreUsuario")nombreUsuario: String,
                                               @Param("activo")activo: Boolean, pageable: Pageable): Page<Usuario>

    fun findByNombreUsuarioContains(@Param("nombreUsuario")nombreUsuario: String): Collection<Usuario>

    /*
     * Same as findById and return a supervisor (avoid circular relationship)
     */
    @Query("""
        MATCH (u:Usuario)-[:SUPERVISOR]->(s:Usuario)
        where ID(u) = ${'$'}id 
        RETURN s
    """)
    fun findByIdSupervisor(id: Long): Optional<Usuario>

    @Query("""
        MATCH (s:Usuario)<-[:SUPERVISOR]-(u:Usuario)
        where s.nombreUsuario = ${'$'}nombre
        RETURN u
    """)
    fun findUsuariosBySupervisor(nombre: String): Collection<Usuario>

    @Query("""
        MATCH (u:Usuario)
        where ID(u) = ${'$'}id
        SET u += {nombre: ${'$'}nombre, nombreUsuario:${'$'}nombreUsuario, telefono:${'$'}telefono, mail:${'$'}mail,
                  interno:${'$'}interno, activo:${'$'}activo, administrador:${'$'}administrador, fechaIngreso:${'$'}fechaIngreso,
                  usuarioModificacion:${'$'}usuarioModificacion, fechaModificacion:${'$'}fechaModificacion
                 }
        RETURN u
    """)
    fun update(id: Long, @Param("nombre")nombre: String, @Param("nombreUsuario")nombreUsuario: String,
               @Param("telefono")telefono: String, @Param("mail")mail: String,
               @Param("interno")interno: Boolean, @Param("activo")activo: Boolean,
               @Param("administrador")administrador: Boolean, @Param("fechaIngreso")fechaIngreso: LocalDate,
               @Param("usuarioModificacion")usuarioModificacion: String, @Param("fechaModificacion")fechaModificacion: LocalDateTime
    ) : Usuario

    @Query("""
        MATCH (u:Usuario)-[rs:SUPERVISOR]->(s:Usuario)
        where ID(u) = ${'$'}id
        DELETE rs
    """)
    fun deleteSupervisor(id: Long) : String?

    @Query("""
        MATCH (u:Usuario), (s:Usuario)
        where ID(u) = ${'$'}id AND ID(s) = ${'$'}idSupervisor 
        CREATE (u)-[rs:SUPERVISOR]->(s)
        RETURN type(rs)
    """)
    fun addSupervisor(id: Long, idSupervisor: Long) : String?

    @Query("""
        MATCH (admin:Usuario)-[m:MIEMBRO]->(g:Grupo)-[p:PERMITE]-(c:Compania)<-[t:TRABAJA]-(u:Usuario)
        where admin.nombreUsuario = ${'$'}nombreAdministrador
              AND NOT exists((g)-[:NO_PERMITE]->(c))
        RETURN u
    """)
    fun findEmpleadosPermiteByAdministrador(@Param("nombreAdministrador")nombreAdministrador: String ) : Collection<Usuario>

    @Query("""
        MATCH (admin:Usuario)-[m:MIEMBRO]->(g:Grupo)-[p:PERMITE]-(c:Compania)<-[s:SUBSIDIARIA]-(sc:Compania)<-[t:TRABAJA]-(u:Usuario)
        where admin.nombreUsuario = ${'$'}nombreAdministrador
              AND NOT exists((g)-[:NO_PERMITE]->(c))
        RETURN u
    """)
    fun findEmpleadosPermiteSubsidiariasByAdministrador(@Param("nombreAdministrador")nombreAdministrador: String ) : Collection<Usuario>

    @Query("""
        MATCH (admin:Usuario)-[m:MIEMBRO]->(g:Grupo)-[p:PERMITE_SIN_HERENCIA]-(c:Compania)<-[t:TRABAJA]-(u:Usuario)
        where admin.nombreUsuario = ${'$'}nombreAdministrador
              AND NOT exists ((g)-[:NO_PERMITE]->(c))
        RETURN u
    """)
    fun findEmpleadosPermiteSinHerenciaByAdministrador(@Param("nombreAdministrador")nombreAdministrador: String ) : Collection<Usuario>

    @Query("""
        MATCH (admin:Usuario)-[:MIEMBRO]->(g:Grupo)<-[:MIEMBRO]-(u:Usuario)
        where admin.nombreUsuario = ${'$'}nombreAdministrador
        RETURN u
    """)
    fun findAdministradoresByAdministrador(@Param("nombreAdministrador")nombreAdministrador: String): Collection<Usuario>

    @Query("""
        MATCH (admin:Usuario)-[:MIEMBRO]->(g:Grupo)-[:PERMITE]-(c:Compania)<-[:TRABAJA]-(u:Usuario)
        where admin.nombreUsuario = ${'$'}nombreAdministrador
        RETURN u
    """)
    fun findEmpleadosPermiteByAdministradorMaestro(@Param("nombreAdministrador")nombreAdministrador: String ) : Collection<Usuario>

    @Query("""
        MATCH (admin:Usuario)-[:MIEMBRO]->(g:Grupo)-[:PERMITE]-(c:Compania)<-[:SUBSIDIARIA]-(ch:Compania)<-[:TRABAJA]-(u:Usuario)
        where admin.nombreUsuario = ${'$'}nombreAdministrador
        RETURN u
    """)
    fun findEmpleadosPermiteSubsidiariasByAdministradorMaestro(@Param("nombreAdministrador")nombreAdministrador: String ): Collection<Usuario>

    @Query("""
        MATCH (admin:Usuario)-[:MIEMBRO]->(g:Grupo)<-[:MIEMBRO]-(u:Usuario)
        where admin.nombreUsuario = ${'$'}nombreAdministrador
        RETURN u
    """)
    fun findAdministradoresByAdministradorMaestro(@Param("nombreAdministrador")nombreAdministrador: String): Collection<Usuario>

    @Query("""
        call { MATCH (c:Compania)<-[:TRABAJA]-(u:Usuario)-[:TIENE_PERFIL]->(p:Perfil)-[:TIENE_ROL]->(r:Rol)-[:TIENE_FACULTAD]->(f:Facultad)
               where u.activo AND
                     c.nombre = ${'$'}nombreCompania AND
                     f.nombre = ${'$'}nombreFacultad AND
                     p.activo AND
                     r.activo AND
                     f.activo AND
               NOT exists((u)-[:SIN_FACULTAD]->(f))
               RETURN u as user
               UNION ALL 
               MATCH (ce:Compania)<-[:TRABAJA]-(ue:Usuario)-[:FACULTAD_EXTRA]->(fe:Facultad)
               where ue.activo AND
                     ce.nombre = ${'$'}nombreCompania AND
                     fe.nombre = ${'$'}nombreFacultad AND
                     fe.activo
               RETURN ue as user
             }
        RETURN user
    """)
    fun findEmpleadosByFacultad(@Param("nombreCompania")nombreCompania: String,
                                @Param("nombreFacultad")nombreFacultad: String): Collection<Usuario>

    @Query("""
        MATCH (u:Usuario)-[rc:TRABAJA]->(c:Compania)
        where c.nombre = ${'$'}nombreCompania
        RETURN u
    """)
    fun findUsuariosByCompania(@Param("nombreCompania") nombreCompania: String): Collection<Usuario>

    @Query("""
        MATCH (u:Usuario)-[r:ASIGNADO]->(a:AreaAsignada)
        where a.idArea = ${'$'}idArea
        RETURN u
    """)
    fun findEmpleadosAssigned(@Param("idArea") idArea: Long): Collection<Usuario>

    @Query("""
        MATCH (u:Usuario)-[r:ASIGNADO]->(a:AreaAsignada)
        where NOT r.activo
        RETURN u
    """)
    fun findSolicitudesAsignacion(): Collection<Usuario>

    @Query("""
        MATCH(u:Usuario)-[r:ASIGNADO]->(a:AreaAsignada)
        where (u.nombreUsuario = ${'$'}nombreUsuario) AND (a.idArea = ${'$'}idArea)
            SET r.activo = ${'$'}approve
        RETURN u.nombreUsuario
    """)
    fun aproveSolicitudAsignacion(@Param("nombreUsuario")nombreUsuario: String,
                                  @Param("idArea") idArea: Long,
                                  @Param("approve") approve: Boolean): String?
    @Query("""
        MATCH (u:Usuario),(a:AreaAsignada)
        where (ID(u) = ${'$'}id) AND (a.idArea = ${'$'}idArea)
        CREATE (u)-[r:ASIGNADO {activo: ${'$'}activo}]->(a)
        RETURN count(r)
    """)
    fun assignArea(@Param("id") id:Long, @Param("idArea")idArea: Long,
                   @Param("activo") activo: Boolean): Long

    @Query("""
        MATCH(u:Usuario)-[r:ASIGNADO]->(a:AreaAsignada)
        where (ID(u) = ${'$'}id) AND (a.idArea = ${'$'}idArea)
        DELETE r
        RETURN count(r)
    """)
    fun unAssignArea(@Param("id") id:Long, @Param("idArea")idArea: Long): Long

    @Query("""
        MATCH (u:Usuario),(c:Compania)
        where (u.idUsuario = ${'$'}idUsuario) AND (c.nombre = ${'$'}nombre)
        CREATE (u)-[r:TRABAJA]->(c)
        RETURN count(r)
    """)
    fun addCompania(@Param("idUsuario") idUsuario:Long, @Param("nombre")nombre: String): Long

    @Query("""
        MATCH (u:Usuario)-[r:TRABAJA]->(c:Compania)
        where (ID(u) = ${'$'}id) AND (c.nombre = ${'$'}nombre)
        DELETE r
        RETURN count(r)
    """)
    fun deleteCompania(@Param("id") id:Long, @Param("nombre")nombre: String): Long

    @Query("""
        MATCH (u:Usuario),(g:Grupo)
        where (u.idUsuario = ${'$'}idUsuario) AND (g.nombre = ${'$'}nombre)
        CREATE (u)-[r:MIEMBRO]->(g)
        RETURN count(r)
    """)
    fun addGrupo(@Param("idUsuario") idUsuario:Long, @Param("nombre")nombre: String): Long

    @Query("""
        MATCH (u:Usuario)-[r:MIEMBRO]->(g:Grupo)
        where (ID(u) = ${'$'}idu) AND (ID(g) = ${'$'}idg)
        DELETE r
        RETURN count(r)
    """)
    fun deleteGrupo(@Param("idu") idu:Long, @Param("idg")idg: Long): Long

    @Query("""
        MATCH (u:Usuario),(p:Perfil)
        where (u.idUsuario = ${'$'}idUsuario) AND (ID(p) = ${'$'}idp)
        CREATE (u)-[r:TIENE_PERFIL]->(p)
        RETURN count(r)
    """)
    fun addPerfil(@Param("idUsuario") idUsuario:Long, @Param("idp")idp: Long): Long

    @Query("""
        MATCH (u:Usuario),(p:Perfil)
        where (u.idUsuario = ${'$'}idUsuario) AND (ID(p) = ${'$'}idp) 
            SET u.fechaModificacion = ${'$'}fechaModificacion
            SET u.usuarioModificacion = ${'$'}usuarioModificacion
        CREATE (u)-[r:TIENE_PERFIL]->(p)
        RETURN count(r)
    """)
    fun assignPerfil(@Param("idUsuario") idUsuario:Long, @Param("idp")idp:Long,
                     @Param("fechaModificacion") fechaModificacion: LocalDateTime,
                     @Param("usuarioModificacion") usuarioModificacion: String): Long

    @Query("""
        MATCH (u:Usuario)-[r:TIENE_PERFIL]->(p:Perfil)
        where (ID(u) = ${'$'}idu) AND (ID(p) = ${'$'}idp)
        DELETE r
        RETURN count(r)
    """)
    fun deletePerfil(@Param("idu") idu:Long, @Param("idp")idp: Long): Long

    @Query("""
        MATCH (u:Usuario)-[r:TIENE_PERFIL]->(p:Perfil)
        where (u.idUsuario = ${'$'}idUsuario) AND (ID(p) = ${'$'}idp)
        DELETE r
        RETURN count(r)
    """)
    fun unAssignPerfil(@Param("idUsuario") idUsuario:Long, @Param("idp")idp:Long): Long

    @Query("""
        MATCH (u:Usuario),(f:Facultad)
        where (u.idUsuario = ${'$'}idUsuario) AND (ID(f) = ${'$'}idf) 
            SET u.fechaModificacion = ${'$'}fechaModificacion
            SET u.usuarioModificacion = ${'$'}usuarioModificacion
        CREATE (u)-[r:FACULTAD_EXTRA]->(f)
        RETURN count(r)
    """)
    fun assignFacultad(@Param("idUsuario") idUsuario:Long, @Param("idf")idf:Long,
                       @Param("fechaModificacion") fechaModificacion: LocalDateTime,
                       @Param("usuarioModificacion") usuarioModificacion: String): Long

    @Query("""
        MATCH (u:Usuario)-[r:FACULTAD_EXTRA]->(f:Facultad)
        where (u.idUsuario = ${'$'}idUsuario) AND (ID(f) = ${'$'}idf)
            SET u.fechaModificacion = ${'$'}fechaModificacion
            SET u.usuarioModificacion = ${'$'}usuarioModificacion
        DELETE r
        RETURN count(r)
    """)
    fun unAssignFacultad(@Param("idUsuario") idUsuario:Long, @Param("idf")idf:Long,
                         @Param("fechaModificacion") fechaModificacion: LocalDateTime,
                         @Param("usuarioModificacion") usuarioModificacion: String): Long

    @Query("""
        MATCH (u:Usuario),(f:Facultad)
        where (u.idUsuario = ${'$'}idUsuario) AND (ID(f) = ${'$'}idf)
            SET u.fechaModificacion = ${'$'}fechaModificacion
            SET u.usuarioModificacion = ${'$'}usuarioModificacion
        CREATE (u)-[r:SIN_FACULTAD]->(f)
        RETURN count(r)
    """)
    fun forbidFacultad(@Param("idUsuario") idUsuario:Long, @Param("idf")idf:Long,
                       @Param("fechaModificacion") fechaModificacion: LocalDateTime,
                       @Param("usuarioModificacion") usuarioModificacion: String): Long

    @Query("""
        MATCH (u:Usuario)-[r:SIN_FACULTAD]->(f:Facultad)
        where (u.idUsuario = ${'$'}idUsuario) AND (ID(f) = ${'$'}idf)
            SET u.fechaModificacion = ${'$'}fechaModificacion
            SET u.usuarioModificacion = ${'$'}usuarioModificacion
        DELETE r
        RETURN count(r)
    """)
    fun unForbidFacultad(@Param("idUsuario") idUsuario:Long, @Param("idf")idf:Long,
                         @Param("fechaModificacion") fechaModificacion: LocalDateTime,
                         @Param("usuarioModificacion") usuarioModificacion: String): Long
}

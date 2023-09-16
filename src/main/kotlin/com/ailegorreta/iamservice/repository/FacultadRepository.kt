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
 *  FacultadRepository.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.repository

import com.ailegorreta.iamservice.model.Facultad
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Interface de Neo4j repository for entity permits.
 *
 * @see Spring-Data for more information
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Repository
interface FacultadRepository : Neo4jRepository<Facultad, Long> {

    override fun findById(id: Long): Optional<Facultad>

    override fun count(): Long

    override fun findAll(pageable: Pageable): Page<Facultad>

    fun findByNombre(@Param("nombre")nombre: String): Optional<Facultad>

    @Query("""
        MATCH(f:Facultad) 
        where f.activo = ${'$'}activo 
        RETURN count(f)
    """)
    fun countByActvoIs(activo : Boolean): Long

    fun findByActivoIs(@Param("activo")activo: Boolean, pageable: Pageable): Page<Facultad>

    @Query("""
        MATCH(f:Facultad) 
        where f.nombre CONTAINS ${'$'}nombre 
        RETURN count(f)
    """)
    fun countByNombreContains(@Param("nombre")nombre: String): Long

    fun findByNombreContains(@Param("nombre")nombre: String, pageable: Pageable): Page<Facultad>

    @Query("""
        MATCH(f:Facultad) 
        where f.nombre CONTAINS ${'$'}nombre AND f.activo = ${'$'}activo 
        RETURN count(f)
    """)
    fun countByNombreContainsAndActivoIs(@Param("nombre")nombre: String,
                                         @Param("activo")activo: Boolean): Long

    fun findByNombreContainsAndActivoIs(@Param("nombre")nombre: String,
                                        @Param("activo")activo: Boolean, pageable: Pageable
    ): Page<Facultad>

    @Query("""
        call { MATCH (u:Usuario)-[:TIENE_PERFIL]->(p:Perfil)-[:TIENE_ROL]->(r:Rol)-[:TIENE_FACULTAD]->(f:Facultad) 
               where u.nombreUsuario = ${'$'}nombreUsuario AND
                     u.activo AND
                     p.activo AND 
                     r.activo AND 
                     f.activo AND 
               NOT exists((u)-[:SIN_FACULTAD]->(f)) 
               RETURN f as ff 
               UNION ALL 
               MATCH (ue:Usuario)-[:FACULTAD_EXTRA]->(fe:Facultad) 
               where ue.nombreUsuario = ${'$'}nombreUsuario AND 
                     ue.activo AND
                     fe.activo AND
               NOT exists((ue)-[:SIN_FACULTAD]->(fe))  
               RETURN fe as ff 
              } 
        RETURN ff 
    """)
    fun findUsuarioFacultades(@Param("nombreUsuario")nombreUsuario: String): Collection<Facultad>

    @Query("""
        MATCH (u:Usuario)-[:FACULTAD_EXTRA]->(f:Facultad) 
         where u.nombreUsuario = ${'$'}nombreUsuario AND 
               u.activo AND 
               f.activo AND 
         NOT exists((u)-[:SIN_FACULTAD]->(f)) 
         RETURN f
    """)
    fun findUsuarioFacultadesExtra(@Param("nombreUsuario")nombreUsuario: String): Collection<Facultad>
}

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
 *  PerfilRepository.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.repository

import com.ailegorreta.iamservice.model.Perfil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Interface de Neo4j repository for entity profiles.
 *
 * @see Spring-data for more information
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Repository
interface PerfilRepository : Neo4jRepository<Perfil, Long> {

    override fun findById(id: Long): Optional<Perfil>

    @Query("""
        MATCH (p:Perfil) 
        where ID(p) = ${'$'}id
        OPTIONAL MATCH (p)-[m:TIENE_ROL]->(r:Rol)-[n:TIENE_FACULTAD]->(f:Facultad)
        RETURN p, collect(m), collect(r), collect(n), collect(f)
    """)
    fun findDepthById(@Param("id")id: Long): Optional<Perfil>

    override fun count(): Long

    override fun findAll(pageable: Pageable): Page<Perfil>

    fun findByNombre(@Param("nombre")nombre: String): Optional<Perfil>

    @Query("""
        MATCH (p:Perfil {nombre:${'$'}nombre})
        OPTIONAL MATCH (p)-[m:TIENE_ROL]->(r:Rol)-[n:TIENE_FACULTAD]->(f:Facultad)
        RETURN p, collect(m), collect(r), collect(n), collect(f)
    """)
    fun findDepthByNombre(@Param("nombre")nombre: String): Optional<Perfil>

    @Query("""
        MATCH(p:Perfil) 
        where p.activo = ${'$'}activo 
        RETURN count(p)
    """)
    fun countByActivoIs(activo : Boolean): Long

    fun findByActivoIs(@Param("activo")activo: Boolean, pageable: Pageable): Page<Perfil>

    @Query("""
        MATCH(p:Perfil) 
        where p.nombre CONTAINS ${'$'}nombre
        RETURN count(p)
    """)
    fun countByNombreContains(@Param("nombre")nombre: String): Long

    fun findByNombreContains(@Param("nombre")nombre: String, pageable: Pageable): Page<Perfil>

    @Query("""
        MATCH(p:Perfil) 
        where p.nombre CONTAINS ${'$'}nombre AND p.activo = ${'$'}activo
        RETURN count(p)
    """)
    fun countByNombreContainsAndActivoIs(@Param("nombre")nombre: String,
                                         @Param("activo")activo: Boolean): Long

    fun findByNombreContainsAndActivoIs(@Param("nombre")nombre: String,
                                        @Param("activo")activo: Boolean, pageable: Pageable): Page<Perfil>

    @Query("""
        MATCH(p:Perfil)-[r:TIENE_ROL]->(ro:Rol)
        where p.nombre CONTAINS ${'$'}nombre AND p.activo = ${'$'}activo
        RETURN p, collect(r), collect(ro)
    """)
    fun findByNombreContainsAndActivoIs_(@Param("nombre")nombre: String,
                                         @Param("activo")activo: Boolean): List<Perfil>

    override fun <S : Perfil> save(s: S): S

    @Query("""
        MATCH(p:Perfil)-[r:TIENE_ROL]->(ro:Rol)
        where (ID(p) = ${'$'}id) AND (ro.idRol = ${'$'}idRol)
        DELETE r
    """)
    fun unAssignRole(@Param("idRol")idRol: Long, @Param("id") id:Long): Long?

    @Query("""
        MATCH (p:Perfil) 
        MATCH (ro:Rol)
        where (ID(p) = ${'$'}id) AND (ID(ro) = ${'$'}idRol)
        MERGE (p)-[r:TIENE_ROL]->(ro)
        RETURN count(r)
    """)
    fun addRol(@Param("id") id:Long, @Param("idRol")idRol: Long): Long

}

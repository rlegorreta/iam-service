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
 *  RolRepository.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.repository

import com.ailegorreta.iamservice.model.Rol
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Interface de Neo4j repository for entity roles.
 *
 * @see Spring-data for more information
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Repository
interface RolRepository : Neo4jRepository<Rol, Long> {

    override fun findById(id: Long): Optional<Rol>

    override fun count(): Long

    override fun findAll(pageable: Pageable): Page<Rol>

    fun findByIdRol(@Param("idRol")idRol : Long): Optional<Rol>

    @Query("""
        MATCH(r:Rol)
        where r.activo = ${'$'}activo
        RETURN count(r)
    """)
    fun countByActivoIs(activo : Boolean): Long

    fun findByActivoIs(@Param("activo")activo: Boolean, pageable: Pageable): Page<Rol>

    @Query("""
        MATCH(r:Rol) 
        where r.nombre CONTAINS ${'$'}nombre
        RETURN count(r)
    """)
    fun countByNombreContains(@Param("nombre")nombre: String): Long

    fun findByNombreContains(@Param("nombre")nombre: String, pageable: Pageable, depth: Int): Page<Rol>

    @Query("""
        MATCH(r:Rol)
        where r.nombre CONTAINS ${'$'}nombre AND r.activo = ${'$'}activo 
        RETURN count(r)
    """)
    fun countByNombreContainsAndActivoIs(@Param("nombre")nombre: String,
                                         @Param("activo")activo: Boolean): Long

    fun findByNombreContainsAndActivoIs(@Param("nombre")nombre: String,
                                        @Param("activo")activo: Boolean, pageable: Pageable): Page<Rol>

    @Query("""
        MATCH(ro:Rol)-[r:TIENE_FACULTAD]->(f:Facultad)
        where ro.nombre CONTAINS ${'$'}nombre AND ro.activo = ${'$'}activo
        RETURN ro, collect(r), collect(f)
    """)
    fun findByNombreContainsAndActivoIs_(@Param("nombre")nombre: String,
                                         @Param("activo")activo: Boolean): List<Rol>

    @Query("""
        MATCH(ro:Rol)-[r:TIENE_FACULTAD]->(f:Facultad)
        where (ID(ro) = ${'$'}id) AND (f.nombre = ${'$'}nombre)
        DELETE r
    """)
    fun unAssignPermit(@Param("nombre")nombre: String, @Param("id") id:Long): String?

    @Query("""
        MATCH (ro:Rol),(f:Facultad)
        where (ID(ro) = ${'$'}id) AND (ID(f) = ${'$'}idFacultad)
        CREATE (ro)-[r:TIENE_FACULTAD]->(f)
        RETURN count(r)
    """)
    fun addPermit(@Param("id") id:Long, @Param("idFacultad")idFacultad: Long): Long
}

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
 *  CompaniaRepository.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.repository

import com.ailegorreta.iamservice.model.Compania
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Interface de Neo4j repository for Entity Company.
 *
 * @see SpringData-Neo4j for more information
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Repository
interface CompaniaRepository : Neo4jRepository<Compania, Long> {

    override fun findById(id: Long): Optional<Compania>

    fun findByNombre(@Param("nombre") nombre: String): Compania?

    override fun findAll(): List<Compania>

    fun findByNegocioIsNotAndActivo(@Param("negocio") negocio: String,
                                    @Param("activo") activo: Boolean,
                                    pageable: Pageable): Page<Compania>

    fun findByIdPersona(@Param("idPersona") idPersona: Long): Optional<Compania>

    @Query("""
        MATCH(c:Compania) 
        WHERE c.negocio <> 'NA' AND c.activo = true 
        RETURN count(c)
    """)
    fun countAllCustomers(): Long

    fun findByNegocioAndActivo(@Param("negocio") negocio: String,
                               @Param("activo") activo: Boolean,
                               pageable: Pageable): Page<Compania>

    @Query("""
        MATCH(c:Compania) 
        WHERE c.negocio = ${'$'}negocio AND c.activo = ${'$'}activo 
        RETURN count(c)
    """)
    fun countByNegocioAndActivo(@Param("negocio") negocio: String, @Param("activo") activo:Boolean): Long

    fun findByNombreContainsAndNegocioIsNotAndActivo(@Param("nombre")nombre: String,
                                                     @Param("negocio")negocio: String,
                                                     @Param("activo")activo: Boolean,
                                                     pageable: Pageable): Page<Compania>

    @Query("""
        MATCH(c:Compania) 
        WHERE c.nombre CONTAINS ${'$'}nombre AND c.negocio <> ${'$'}negocio AND c.activo = ${'$'}activo 
        RETURN count(c)
    """)
    fun countByNombreContainsAndNegocioIsNotAndActivo(@Param("nombre")nombre: String,
                                                      @Param("negocio")negocio: String,
                                                      @Param("activo")activo: Boolean): Long

    fun findByNombreContainsAndNegocioAndActivo(@Param("nombre")nombre: String,
                                                @Param("negocio") negocio: String,
                                                @Param("activo") activo: Boolean,
                                                pageable: Pageable): Page<Compania>

    @Query("""
        MATCH(c:Compania) 
        where c.nombre CONTAINS ${'$'}nombre AND c.negocio = ${'$'}negocio AND c.activo = ${'$'}activo 
        RETURN count(c)
    """)
    fun countByNombreContainsNegocioAndActivo(@Param("nombre")nombre: String,
                                              @Param("negocio") negocio: String,
                                              @Param("activo") activo: Boolean): Long

    @Query("""
        MATCH (sub:Compania)-[:SUBSIDIARIA]->(c:Compania) 
        where c.nombre = ${'$'}nombre 
        RETURN sub
    """)
    fun findSubsidiarias(@Param("nombre")nombre: String ) : Collection<Compania>

    @Query("""
        MATCH (admin:Usuario)-[:MIEMBRO]->(g:Grupo)-[:PERMITE]-(c:Compania) 
        where admin.nombreUsuario = ${'$'}nombreAdministrador 
               AND NOT exists((g)-[:NO_PERMITE]->(c)) 
        RETURN c
    """)
    fun findCompaniasPermiteByAdministrador(@Param("nombreAdministrador")nombreAdministrador: String ) : Collection<Compania>

    @Query("""
        MATCH (admin:Usuario)-[:MIEMBRO]->(g:Grupo)-[:PERMITE]-(c:Compania)<-[:SUBSIDIARIA]-(ch:Compania)
        where admin.nombreUsuario = ${'$'}nombreAdministrador 
                AND NOT exists((g)-[:NO_PERMITE]->(ch))
        RETURN ch
    """)
    fun findCompaniasPermiteSubsidiariasByAdministrador(@Param("nombreAdministrador")nombreAdministrador: String ) : Collection<Compania>

    @Query("""
        MATCH (admin:Usuario)-[:MIEMBRO]->(g:Grupo)-[:PERMITE_SIN_HERENCIA]-(c:Compania) 
        where admin.nombreUsuario = ${'$'}nombreAdministrador 
               AND NOT exists((g)-[:NO_PERMITE]->(c)) 
        RETURN c
    """)
    fun findCompaniasPermiteSinHerenciByAdministrador(@Param("nombreAdministrador")nombreAdministrador: String ) : Collection<Compania>

    @Query("""
        MATCH (c:Compania)<-[:CONTIENE]-(a:Area) 
        WHERE a.nombre = ${'$'}nombreArea 
              AND a.activo = true 
              AND c.activo = true
        RETURN c
    """)
    fun findCompaniaByArea(@Param("nombreArea") nombreArea: String): Compania?

    @Query("""
        MATCH (c:Compania)<-[:TRABAJA]-(u: Usuario) 
             WHERE (ID(u) = ${'$'}id) 
        RETURN c
    """)
    fun findCompaniasByEmpleado(@Param("id") id:Long): Collection<Compania>

    @Query("""
        MATCH (c:Compania {nombre:${'$'}nombreCompania}),(a:Area {nombre:${'$'}nombreArea}) 
        CREATE (c)<-[r:CONTIENE]-(a) 
        RETURN count(r)      
    """)
    fun addArea(@Param("nombreCompania") nombreCompania:String, @Param("nombreArea")nombreArea: String): Long

    @Query("""
        MATCH (c:Compania)<-[r:CONTIENE]-(a:Area) 
        where (c.nombre = ${'$'}nombreCompania) AND (a.nombre = ${'$'}nombreArea)
        DELETE r 
        RETURN count(r)
    """)
    fun deleteArea(@Param("nombreCompania") nombreCompania:String, @Param("nombreArea")nombreArea: String): Long

}


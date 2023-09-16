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
 *  AreasRepository.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.repository

import com.ailegorreta.iamservice.model.Area
import com.ailegorreta.iamservice.model.AreaAsignada
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Interface de Neo4j repository for Entity Area.
 *
 * @see Spring-Data Neo4j for more information
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Repository
interface AreaRepository : Neo4jRepository<Area, Long> {

    override fun findById(id: Long): Optional<Area>

    override fun findAll(): List<Area>

    fun findByNombre(@Param("nombre") nombre: String): Optional<Area>

    fun findByIdArea(@Param( "idArea") idArea: Long): Optional<Area>

}

/**
 * Interface de Neo4j repository for Entity AreaAsignada.
 *
 * This entity is a replica for AreaRepository and is created to avoid
 * circular references and make Spring Data to slow.
 *
 * @see Spring Data Neo4j⚡️RX for more information
 *
 * @author rlh
 * @project : iam-server-repo
 * @date Septiembre 2023
 *
 */
@Repository
interface AreaAsignadaRepository : Neo4jRepository<AreaAsignada, Long> {

    override fun findById(id: Long): Optional<AreaAsignada>

    override fun findAll(): List<AreaAsignada>

    fun findByNombre(@Param("nombre") nombre: String): Optional<AreaAsignada>

    fun findByIdArea(@Param("idArea") idArea: Long): Optional<AreaAsignada>
}


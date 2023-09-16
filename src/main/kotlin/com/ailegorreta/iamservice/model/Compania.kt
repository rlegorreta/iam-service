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
 *  Compania.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.model

import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.neo4j.core.schema.*
import java.util.LinkedHashSet

/**
 * Entity Compania.
 *
 * The Companies must match with the Companies back office system.
 *
 * We use ElementId instead of Long type in order to keep out the Repository warning from SDN Neo4j and for
 * future Neo4j version gor generated values or in a future version generate UIID types
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
@Node("Compania")
data class Compania(@Id  @GeneratedValue var id: Long? = null,
                    @Property(name = "nombre") 	var nombre: String,
                    @Property(name = "padre")	var padre: Boolean = true,
                    @Property(name = "negocio")	var negocio: String,
                    @Property(name = "usuarioModificacion")  var usuarioModificacion: String,
                    @LastModifiedDate
                    @Property(name = "fechaModificacion")    var fechaModificacion: LocalDateTime,
                    @Property(name = "activo")   var activo: Boolean = false,
                    @Property(name = "idPersona")  var idPersona: Long,
    // The relationship SUBSIDIARIA exist in Neo4j database and can be used in the Queries but not
    // directly in Spring Data to avoid cyclic relationships
    // @Relationship(type = "SUBSIDIARIA", direction = Relationship.Direction.INCOMING)
    // var subsidiarias: ArrayList<Compania> = ArrayList()
                    @Relationship(type = "CONTIENE", direction = Relationship.Direction.INCOMING)
                    var areas: LinkedHashSet<Area>? = null
    // The relationship TRABAJA can be used in the Queries but not
    // directly in Spring Data to avoid cyclic relationships
    //@Relationship(type = "TRABAJA", direction = Relationship.Direction.INCOMING)
    //   var empleados: ArrayList<Usuario> = ArrayList()
) {
    fun addArea(area: Area) { if (areas != null) areas!!.add(area) else areas = linkedSetOf(area) }
}

enum class Negocio {
    NA,             /* This case are subsidiaries companies for the system owner (e.g, LMASS) */
    GOBIERNO,
    FINANCIERA,
    INDUSTRIAL,
    PARTICULAR,
    NOT_EXIST;      /* Other business type can be added depending on the system */

    companion object {
        val map = hashMapOf(0 to NA,
            1 to GOBIERNO,          // Mapping value from Siefores and Operadoras business numbers
            2 to FINANCIERA,
            13 to INDUSTRIAL,
            14 to PARTICULAR,
            0 to NOT_EXIST)
        fun getNegocio(tipoParticipacion : Int)=  map[tipoParticipacion] ?: NOT_EXIST
    }
}

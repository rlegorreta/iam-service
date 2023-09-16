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
 *  Grupo.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.model

import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.neo4j.core.schema.*

/**
 * Entity Grupo.
 *
 * This a Group where an Administrator (a special User) belongs to. A Group
 * defines what Companies the Administrator has access (or denies).
 *
 *  We use ElementId instead of Long type in order to keep out the Repository warning from SDN Neo4j and for
 *  future Neo4j version gor generated values or in a future version generate UIID types
 *
 * @author rlh
 * @project : iam-server-repo
 * @date September 2023
 *
 */
@Node("Grupo")
data class Grupo(@Id @GeneratedValue var id: Long? = null,
                 @Property(name = "nombre") 			 var nombre: String,
                 @Property(name = "usuarioModificacion") var usuarioModificacion: String,
                 @LastModifiedDate
                 @Property(name = "fechaModificacion")   var fechaModificacion: LocalDateTime,
                 @Property(name = "activo")
                 @JsonProperty("activo")		     var activo: Boolean,
                 @Relationship(type = "PERMITE", direction = Relationship.Direction.OUTGOING)
                 var permiteCompanias: LinkedHashSet<Compania>? = null,
                 @Relationship(type = "NO_PERMITE", direction = Relationship.Direction.OUTGOING)
                 var noPermiteCompanias: LinkedHashSet<Compania>? = null,
                 @Relationship(type = "PERMITE_SIN_HERENCIA", direction = Relationship.Direction.OUTGOING)
                 var permiteSinHerencia: LinkedHashSet<Compania>? = null) {

    fun addPermiteCompania(compania: Compania) { if (permiteCompanias != null) permiteCompanias!!.add(compania) else permiteCompanias = linkedSetOf(compania) }

    fun removePermiteCompania(compania: Compania) { if (permiteCompanias != null) permiteCompanias!!.remove(compania) else throw Exception("found empty collection") }

    fun addNoPermiteCompania(compania: Compania) { if (noPermiteCompanias != null) noPermiteCompanias!!.add(compania) else noPermiteCompanias = linkedSetOf(compania) }

    fun removeNoPermiteCompania(compania: Compania) { if (noPermiteCompanias != null) noPermiteCompanias!!.remove(compania) else throw Exception("found empty collection") }

    fun addPermiteSinHerencia(compania: Compania) { if (permiteSinHerencia != null) permiteSinHerencia!!.add(compania) else permiteSinHerencia = linkedSetOf(compania) }

    fun removePermiteSinHerencia(compania: Compania) { if (permiteSinHerencia != null) permiteSinHerencia!!.remove(compania) else throw Exception("found empty collection") }

}

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
 *  Perfil.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.model

import com.fasterxml.jackson.annotation.JsonProperty

import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.neo4j.core.schema.*
import java.time.LocalDateTime

/**
 * Profile entity. This is the 'job' for the Employee.
 *
 * One profile can have several roles.
 *
 *  We use ElementId instead of Long type in order to keep out the Repository warning from SDN Neo4j and for
 *  future Neo4j version for generated values or in a future version generate UIID types
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Node("Perfil")
data class Perfil(@Id @GeneratedValue var id: Long? = null,
                  @Property(name = "nombre") 		var nombre: String,
                  @Property(name = "descripcion")   var descripcion: String? = null,
                  @Property(name = "activo")
                  @JsonProperty("activo")     var activo: Boolean = false,
                  @Property(name = "patron")
                  @JsonProperty("patron")     var patron: Boolean = false,
                  @Property(name = "usuarioModificacion")  var usuarioModificacion: String,
                  @LastModifiedDate
                  @Property(name = "fechaModificacion")  var fechaModificacion: LocalDateTime = LocalDateTime.now(),
                  @Relationship(type = "TIENE_ROL", direction = Relationship.Direction.OUTGOING)
                  var roles: LinkedHashSet<Rol>? = null) {

    fun addRol(rol: Rol) { if (roles != null) roles!!.add(rol) else roles = linkedSetOf(rol) }

    fun removeRol(rol: Rol) { if (roles != null) roles!!.remove(rol) else throw Exception("found empty collection") }

}

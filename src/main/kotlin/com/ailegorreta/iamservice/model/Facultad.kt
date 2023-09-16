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
 *  Facultad.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.neo4j.core.schema.*
import java.time.LocalDateTime

/**
 * Entity for simple Permits.
 *
 * Simple permits are boolean capability for a User.
 *
 * Fasterxml is used for serialization and deserialization on the authDB.oauth2_authorization table.
 * This is a GrantedAuthority class
 *
 * important note: The field attribute oauth2_authorization is of type Text (Postgress) so it has a maximum limit
 *                 of characters. If one user will going to have many permits it can be overloaded.
 *                 To estrange things it sends a different error by the jdbc driver:
 *                 java.lang.ClassCastException: class java.lang.String cannot be cast to class org.springframework.security.core.GrantedAuthority
 *                 because the .deserialize from fasterxml.
 *                 To solve the problem (partially) we define attributes with @jsonIgnore and also take care for
 *                 the size in this attribute.
 *
 * We use ElementId instead of Long type in order to keep out the Repository warning from SDN Neo4j and for
 * future Neo4j version for generated values or in a future version generate UIID types.
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@Node("Facultad")
data class Facultad (@Id @GeneratedValue @JsonIgnore var id: Long? = null,
                     @Property(name = "nombre") 			    var nombre: String,
                     @Property(name = "descripcion") 	    	var descripcion: String?,
                     @Property(name = "tipo")			    	var tipo:  String,
                     @Property(name = "usuarioModificacion")    var usuarioModificacion: String,
                     @LastModifiedDate
                     @Property(name = "fechaModificacion")      var fechaModificacion: LocalDateTime = LocalDateTime.now(),
                     @Property(name = "activo")
                     @JsonProperty("activo")					var activo: Boolean = false)

enum class FacultadTipo {
    HORARIO,
    FISICA,
    SISTEMA,
    SIMPLE /* other FACULTAD_TYPES could be added here for future versions */
}




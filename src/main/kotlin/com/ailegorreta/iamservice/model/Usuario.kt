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
 *  Usuario.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.neo4j.core.schema.*
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.oauth2.core.oidc.user.OidcUser
import java.time.*
import java.util.*

/**
 * User entity. This entity must be synchronized with an LDAP or
 * ActiveDirectory.
 *
 * We use ElementId instead of Long type in order to keep out the Repository warning from SDN Neo4j and for
 * future Neo4j version for generated values or in a future version generate UIID types
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 *
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
@Node("Usuario")
data class Usuario(@Id @GeneratedValue                  var id: Long? = null,
                   @Property(name = "idUsuario")		var idUsuario: Long,
                   @Property(name = "nombreUsuario")	var nombreUsuario: String,
                   @Property(name = "nombre") 			var nombre: String,
                   @Property(name = "apellido") 		var apellido: String,
                   @Property(name = "telefono")			var telefono: String,
                   @Property(name = "mail")				var mail: String,
                   @Property(name = "interno")
                   @JsonProperty("interno")				var interno: Boolean,
                   @Property(name = "activo")
                   @JsonProperty("activo")					var activo: Boolean,
                   @Property(name = "administrador")
                   @JsonProperty("administrador")			var administrador: Boolean,
                   @Property(name = "fechaIngreso")      	var fechaIngreso: LocalDate,
                   @Property(name = "zonaHoraria")			var zonaHoraria: String? = null,
                   @Property(name = "usuarioModificacion")	var usuarioModificacion: String,
                   @LastModifiedDate
                   @Property(name = "fechaModificacion")	var fechaModificacion: LocalDateTime,
                   @Relationship(type = "TIENE_PERFIL", direction = Relationship.Direction.OUTGOING)
                   var perfil: Perfil? = null,
    // @Relationship(type = "SUPERVISOR", direction = Relationship.Direction.OUTGOING)
    // For Spring Data performance we use the relationship manually in the @Queries
    // 									var supervisor: Usuario? = null,
    // @Relationship(type = "MIEMBRO", direction = Relationship.Direction.OUTGOING)
    //									    var grupos: LinkedHashSet<Grupo>? = null,
    // @Relationship(type = "TRABAJA", direction = Relationship.Direction.OUTGOING)
    // For Spring Data performance we use the relationship manually in the @Queries
    // var companias: ArrayList<Compania> = ArrayList()
                   @Relationship(type = "ASIGNADO", direction = Relationship.Direction.OUTGOING)
                   var areas: LinkedHashSet<Asignado>? = null,
                   @Relationship(type = "SIN_FACULTAD", direction = Relationship.Direction.OUTGOING)
                   var sinFacultades: LinkedHashSet<Facultad>? = null,
                   @Relationship(type = "FACULTAD_EXTRA", direction = Relationship.Direction.OUTGOING)
                   var extraFacultades: LinkedHashSet<Facultad>? = null) /*: OidcUser */ {

    var nombreCompania = ""

    fun addArea(asignado: Asignado) { if (areas != null) areas!!.add(asignado) else areas = linkedSetOf(asignado) }

    fun removeArea(asignado: Asignado) { if (areas != null) areas!!.remove(asignado) else throw Exception("areas empty collection") }

    fun addExtraFacultad(facultad: Facultad) { if (extraFacultades != null) extraFacultades!!.add(facultad) else extraFacultades = linkedSetOf(facultad) }

    fun removeExtraFacultad(facultad: Facultad) { if (extraFacultades != null) extraFacultades!!.remove(facultad) else throw Exception("found empty collection") }

    fun addSinFacultad(facultad: Facultad) { if (sinFacultades != null) sinFacultades!!.add(facultad) else sinFacultades = linkedSetOf(facultad) }

    fun removeSinFacultad(facultad: Facultad) { if (sinFacultades != null) sinFacultades!!.remove(facultad) else throw Exception("found empty collection") }

    /*
    override fun getName() = nombreUsuario
    override fun getGivenName() = nombre
    override fun getFamilyName() = apellido
    override fun getFullName(): String {
        val firstName: String = nombre
        val lastName: String = apellido
        val sb = StringBuilder()

        if (firstName.isNotBlank()) sb.append(firstName)
        if (lastName.isNotBlank()) {
            if (sb.isNotEmpty()) sb.append(" ")
            sb.append(lastName)
        }
        if (sb.isEmpty()) sb.append(name)

        return sb.toString()
    }
    override fun getPhoneNumber() = telefono
    override fun getPreferredUsername() = nombreUsuario
    override fun getZoneInfo() = zonaHoraria ?: ZoneId.systemDefault().toString()
    override fun getUpdatedAt(): Instant = fechaModificacion.toInstant(ZoneOffset.UTC)
    // Noop fields
    override fun getAttributes() = emptyMap<String, Any>()
    override fun getAuthorities() = emptyList<GrantedAuthority>()
    override fun getClaims() = emptyMap<String, Any>()
    override fun getUserInfo() = null
    override fun getIdToken() = null

     */

}

/**
 * Asignado relationship. The relationship has an attribute that if is activo
 * or not activo. Not activo means that the assignment has not been approved.
 *
 * The mapping is done to AreaAsignada and not to Companias to avoid circular
 * references and make Spring Data to slow.
 *
 * @author rlh
 * @project : iam-service
 * @date June 2023
 *
 */
@RelationshipProperties
data class Asignado (@Id @GeneratedValue var id: Long? = null,
                     @Property(name = "activo")
                     @JsonProperty("activo") var activo: Boolean,
                     @TargetNode val area: AreaAsignada)



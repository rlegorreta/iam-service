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
 *  UsuarioDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import java.time.*
import com.fasterxml.jackson.annotation.*
import com.ailegorreta.commons.dtomappers.EntityDTOMapper
import com.ailegorreta.iamservice.model.Asignado
import com.ailegorreta.iamservice.model.Usuario

//import org.springframework.security.core.GrantedAuthority
// import org.springframework.security.oauth2.core.oidc.user.OidcUser

/**
 * Data class for UsuarioDTO.
 *
 * @author rlh
 * @project : iam-server-repo
 * @date September 2023
 */
@JsonIgnoreProperties(value = ["sinFacultades", "extraFacultades", "perfil"])
data class UsuarioDTO @JvmOverloads constructor (val id : Long? = null,
                                                 val idUsuario: Long,
                                                 val nombreUsuario: String,
                                                 val nombre: String,
                                                 val apellido: String,
                                                 val telefono: String,
                                                 val mail: String,
                                                 @JsonProperty("interno")
                                                 val interno: Boolean,
                                                 @JsonProperty("activo")
                                                 val activo: Boolean,
                                                 @JsonProperty("administrador")
                                                 val administrador: Boolean,
                                                 val fechaIngreso: LocalDate = LocalDate.now(),
                                                 val zonaHoraria: String?,
                                                 val usuarioModificacion: String,
                                                 val fechaModificacion: LocalDateTime = LocalDateTime.now(),
                                                 var supervisor: UsuarioDTO? = null,
    // ^ Since circular relationships are not allowed
    // then we create it manually in the services when needed.
                                                 var grupos: Collection<GrupoDTO> = ArrayList(),
                                                 var companias: Collection<CompaniaDTO> = ArrayList(),
                                                 var areas: Collection<AsignadoDTO> = ArrayList()) /*: OidcUser */ {

    companion object : EntityDTOMapper<Usuario, UsuarioDTO> {
        override var dtos = HashMap<Int, Any>()

        override fun fromEntityRecursive(entity: Usuario): UsuarioDTO {
            val a = dtos[entity.hashCode()]

            if (a != null)
                return a as UsuarioDTO

            val usuarioDTO = UsuarioDTO(id = entity.id,
                                        idUsuario = entity.idUsuario,
                                        nombreUsuario = entity.nombreUsuario,
                                        nombre = entity.nombre,
                                        apellido = entity.apellido,
                                        telefono = entity.telefono,
                                        mail = entity.mail,
                                        activo = entity.activo,
                                        interno = entity.interno,
                                        administrador = entity.administrador,
                                        fechaIngreso = entity.fechaIngreso,
                                        zonaHoraria = entity.zonaHoraria,
                                        usuarioModificacion = entity.usuarioModificacion,
                                        fechaModificacion = LocalDateTime.now())

            dtos[entity.hashCode()] = usuarioDTO

            // note: grupos is left empty since this relationship does not exist in Usuario (for performance reasons)
            //       so if needed in the DTO manually we must initialize this relationship with method findGruposByUsuario
            usuarioDTO.areas = AsignadoDTO.mapFromEntities(entity.areas)

            return usuarioDTO
        }
    }

    /*
    override fun getEmail() = mail
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
 * Relationship ASIGNADO.
 *
 * @author rlh
 * @project : iam-service
 * @date June 2022
 */
data class AsignadoDTO(val activo: Boolean, val area: AreaDTO) {
    companion object : EntityDTOMapper<Asignado, AsignadoDTO> {
        override var dtos = java.util.HashMap<Int, Any>()

        override fun fromEntityRecursive(entity: Asignado): AsignadoDTO {
            val a = dtos.get(entity.hashCode())

            if (a != null)
                return a as AsignadoDTO

            val asignadoDTO =  AsignadoDTO(activo = entity.activo,
                area = AreaDTO.fromEntity(entity.area.toArea()))

            return asignadoDTO
        }
    }
}

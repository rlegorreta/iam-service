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
 *  PerfilDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad.dto

import com.ailegorreta.commons.dtomappers.EntityDTOMapper
import com.ailegorreta.iamservice.model.Perfil
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Data class for PerfilDTO.
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 */
data class PerfilDTO @JvmOverloads constructor(val id : Long? = null,
                                               val nombre: String,
                                               val descripcion: String?,
                                               @JsonProperty("activo")
                                               val activo: Boolean = true,
                                               @JsonProperty("patron")
                                               val patron: Boolean = false,
                                               val usuarioModificacion: String,
                                               val fechaModificacion: LocalDateTime = LocalDateTime.now(),
                                               var roles: Collection<RolDTO> = ArrayList()) {

    companion object : EntityDTOMapper<Perfil, PerfilDTO> {

        override var dtos = HashMap<Int, Any>()

        override fun fromEntityRecursive(entity: Perfil): PerfilDTO {
            val a = dtos[entity.hashCode()]

            if (a != null)
                return a as PerfilDTO

            val perfilDTO =  PerfilDTO(id = entity.id,
                            nombre = entity.nombre,
                            descripcion = entity.descripcion,
                            activo = entity.activo,
                            patron = entity.patron,
                            usuarioModificacion = entity.usuarioModificacion,
                            fechaModificacion = entity.fechaModificacion)

            dtos[entity.hashCode()] = perfilDTO
            entity.roles?.let { perfilDTO.roles =  RolDTO.mapFromEntities(it) }

            return perfilDTO
        }
    }
}

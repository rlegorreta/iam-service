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
 *  RolDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad.dto

import com.ailegorreta.commons.dtomappers.EntityDTOMapper
import com.ailegorreta.iamservice.model.Rol
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Data class for RolDTO.
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 */
data class RolDTO @JvmOverloads constructor(val id : Long? = null,
                                            val idRol: Long,
                                            val nombre: String,
                                            val usuarioModificacion: String,
                                            val fechaModificacion: LocalDateTime = LocalDateTime.now(),
                                            @JsonProperty("activo") val activo: Boolean = true,
                                            var facultades: Collection<FacultadDTO> = ArrayList()) {

    companion object : EntityDTOMapper<Rol, RolDTO> {

        override var dtos = HashMap<Int, Any>()

        override fun fromEntityRecursive(entity: Rol): RolDTO {
            val a = dtos[entity.hashCode()]

            if (a != null)
                return a as RolDTO

            val rolDTO =  RolDTO(id = entity.id,
                                idRol = entity.idRol,
                                nombre = entity.nombre,
                                usuarioModificacion = entity.usuarioModificacion,
                                fechaModificacion = entity.fechaModificacion,
                                activo = entity.activo)

            dtos[entity.hashCode()] = rolDTO
            entity.facultades?.let { rolDTO.facultades =  FacultadDTO.mapFromEntities(it) }

            return rolDTO
        }
    }
}

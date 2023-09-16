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
 *  GrupoDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.*

import com.ailegorreta.commons.dtomappers.EntityDTOMapper
import com.ailegorreta.iamservice.model.Grupo

/**
 * Data class for GroupDTO.
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 */
data class GrupoDTO @JvmOverloads constructor(val id : Long? = null,
                                              val nombre: String,
                                              @JsonProperty("activo") val activo: Boolean = true,
                                              val usuarioModificacion: String,
                                              val fechaModificacion: LocalDateTime = LocalDateTime.now(),
                                              var permiteCompanias: Collection<CompaniaDTO> = ArrayList(),
                                              var noPermiteCompanias: Collection<CompaniaDTO> = ArrayList(),
                                              var permiteSinHerencia: Collection<CompaniaDTO> = ArrayList()) {

    companion object : EntityDTOMapper<Grupo, GrupoDTO> {

        override var dtos = HashMap<Int, Any>()

        override fun fromEntityRecursive(entity: Grupo): GrupoDTO {
            val a = dtos.get(entity.hashCode())

            if (a != null)
                return a as GrupoDTO

            val grupoDTO =  GrupoDTO(id = entity.id,
                                    nombre = entity.nombre,
                                    activo = entity.activo,
                                    usuarioModificacion = entity.usuarioModificacion,
                                    fechaModificacion = entity.fechaModificacion)

            dtos.put(entity.hashCode(), grupoDTO)
            grupoDTO.permiteCompanias = CompaniaDTO.mapFromEntities(entity.permiteCompanias)
            grupoDTO.noPermiteCompanias = CompaniaDTO.mapFromEntities(entity.noPermiteCompanias)
            grupoDTO.permiteSinHerencia = CompaniaDTO.mapFromEntities(entity.permiteSinHerencia)

            return grupoDTO
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GrupoDTO) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString() = "idGrupo = $id" +
            " nombre = $nombre" +
            "\npermiteCompania =" + permiteCompanias.toString() +
            "\nnoPermiteCompanias =" + noPermiteCompanias.toString() +
            "\npermiteSinHerencia =" + permiteSinHerencia.toString()
}

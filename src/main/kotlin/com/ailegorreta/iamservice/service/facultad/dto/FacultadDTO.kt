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
 *  FacultadDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad.dto

import com.ailegorreta.commons.dtomappers.EntityDTOMapper
import com.ailegorreta.iamservice.model.Facultad
import com.ailegorreta.iamservice.model.FacultadTipo
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Data class for FacultadDTO.
 *
 * @author rlh
 * @project : iam-service
 * @date : September 2023
 */
data class FacultadDTO @JvmOverloads constructor(val id : Long? = null,
                                                 val nombre: String,
                                                 val descripcion: String?,
                                                 val tipo: FacultadTipo = FacultadTipo.SIMPLE,
                                                 val usuarioModificacion: String,
                                                 val fechaModificacion: LocalDateTime = LocalDateTime.now(),
                                                 @JsonProperty("activo") val activo: Boolean = false) {

    companion object : EntityDTOMapper<Facultad, FacultadDTO> {

        override var dtos = HashMap<Int, Any>()

        override fun fromEntityRecursive(entity: Facultad): FacultadDTO {
            val a = dtos[entity.hashCode()]

            if (a != null)
                return a as FacultadDTO

            val facultadDTO =  FacultadDTO(id = entity.id,
                                            nombre = entity.nombre,
                                            descripcion = entity.descripcion,
                                            tipo = FacultadTipo.valueOf(entity.tipo),
                                            usuarioModificacion = entity.usuarioModificacion,
                                            fechaModificacion = entity.fechaModificacion,
                                            activo = entity.activo)

            dtos[entity.hashCode()] = facultadDTO

            return facultadDTO
        }
    }
}

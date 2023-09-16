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
 *  NewGrupoDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import com.ailegorreta.commons.dtomappers.IdDTOMapper
import com.ailegorreta.iamservice.model.Grupo

/**
 * Data class for create a New Groups and at least define one Administrator.
 *
 * The Administrator must exists the database.
 * No validation is done if the Administrator has the right privileges. This
 * must be done inside de UI.
 *
 * @author rlh
 * @project : iam-service
 * @date June 2023
 */
data class NewGrupoDTO constructor(
    val grupoDTO: GrupoDTO,
    var nombre: String) {

    companion object : IdDTOMapper<Grupo, String, NewGrupoDTO> {

        override fun fromEntity(id: Grupo, entity: String) = NewGrupoDTO(
            grupoDTO = GrupoDTO.fromEntity(id),
            nombre = entity)
    }
}
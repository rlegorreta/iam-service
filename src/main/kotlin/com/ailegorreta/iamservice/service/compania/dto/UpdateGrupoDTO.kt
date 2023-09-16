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
 *  UpdateGrupoDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import com.ailegorreta.commons.dtomappers.IdDTOMapper
import com.ailegorreta.iamservice.model.Grupo

/**
 * Data class for update all Grupo type links.
 * Erases not existent links and adds new links.
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 */
data class UpdateGrupoDTO(val grupoId: Long,
                            val usuarioModificacion: String? = null,
                            val permiteCompanias: Collection<CompaniaDTO>? = null,
                            val noPermiteCompanias: Collection<CompaniaDTO>? = null,
                            val permiteSinHerencia: Collection<CompaniaDTO>? = null) {

    companion object : IdDTOMapper<Long, Grupo, UpdateGrupoDTO> {

        override fun fromEntity(id: Long, entity: Grupo) =  UpdateGrupoDTO(grupoId = id,
            permiteCompanias = CompaniaDTO.mapFromEntities(entity.permiteCompanias),
            noPermiteCompanias = CompaniaDTO.mapFromEntities(entity.noPermiteCompanias),
            permiteSinHerencia = CompaniaDTO.mapFromEntities(entity.permiteSinHerencia))
    }

}
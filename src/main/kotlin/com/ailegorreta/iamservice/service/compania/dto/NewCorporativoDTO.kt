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
 *  NewCorporativoDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import com.ailegorreta.commons.dtomappers.IdDTOMapper
import com.ailegorreta.iamservice.model.Compania
import com.ailegorreta.iamservice.model.Usuario

/**
 * Data class for create a New Corporation and its Administrator.
 *
 * @author rlh
 * @project : iam-service
 * @date June 2023
 */
data class NewCorporativoDTO constructor(
    val companiaDTO: CompaniaDTO,
    var usuarioDTO: UsuarioDTO) {

    companion object : IdDTOMapper<Compania, Usuario, NewCorporativoDTO> {

        override fun fromEntity(id: Compania, entity: Usuario) = NewCorporativoDTO(
            companiaDTO = CompaniaDTO.fromEntity(id),
            usuarioDTO = UsuarioDTO.fromEntity(entity))
    }
}
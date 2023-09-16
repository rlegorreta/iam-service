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
 *  AssignPerfilDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad.dto

import com.ailegorreta.commons.dtomappers.IdDTOMapper
import com.ailegorreta.iamservice.model.Usuario

/**
 * Data class for assigning a new Profile to an existing user.
 *
 * This DTO is used also to Assign a new Permit to the User (extra Facultad) or
 * is used to deny to the User a specific Permit (prohibe Facultad).
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 */
data class AssignPerfilDTO constructor(
    val nombre: String,
    val usuarioDTO: UsuarioDTO) {

    companion object : IdDTOMapper<String, Usuario, AssignPerfilDTO> {

        override fun fromEntity(id: String, entity: Usuario) =  AssignPerfilDTO(nombre = id,
            usuarioDTO = UsuarioDTO.fromEntity(entity))
    }
}
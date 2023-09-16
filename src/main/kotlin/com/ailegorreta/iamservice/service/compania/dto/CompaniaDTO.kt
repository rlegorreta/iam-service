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
 *  CompaniaDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import com.ailegorreta.iamservice.model.Negocio
import com.ailegorreta.iamservice.model.Compania
import java.util.*

import com.fasterxml.jackson.annotation.*

import com.ailegorreta.commons.dtomappers.EntityDTOMapper
import java.time.LocalDateTime

/**
 * Data class for Company in CompanyService.
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 */
data class CompaniaDTO @JvmOverloads constructor(val id : Long? = null,
                                                 val nombre: String,
                                                 @JsonProperty("padre")
                                                 val padre: Boolean,
                                                 val negocio: Negocio = Negocio.NA,
                                                 val usuarioModificacion: String,
                                                 val fechaModificacion: LocalDateTime = LocalDateTime.now(),
                                                 var activo: Boolean = false,
                                                 val idPersona: Long = 0L,
                                                 var areas: Collection<AreaDTO> = ArrayList(),
                                                 @JsonProperty("subsidiarias")
                                                 var subsidiarias: ArrayList<CompaniaDTO>? = null) { // Since circular relationships are not allowed
    // then we create it manually in the services.

    companion object : EntityDTOMapper<Compania, CompaniaDTO> {

        override var dtos = HashMap<Int, Any>()

        override fun fromEntityRecursive(entity: Compania): CompaniaDTO {
            val a = dtos[entity.hashCode()]

            if (a != null)
                return a as CompaniaDTO

            val companiaDTO =  CompaniaDTO(id = entity.id,
                nombre = entity.nombre,
                padre = entity.padre,
                negocio = Negocio.valueOf(entity.negocio),
                usuarioModificacion = entity.usuarioModificacion,
                fechaModificacion = entity.fechaModificacion,
                activo = entity.activo,
                idPersona = entity.idPersona)

            dtos[entity.hashCode()] = companiaDTO
            companiaDTO.areas = AreaDTO.mapFromEntities(entity.areas)

            return companiaDTO
        }
    }

    fun addSubsidiaria(sub : CompaniaDTO) {
        if (subsidiarias == null)
            subsidiarias = ArrayList()
        subsidiarias!!.add(sub)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompaniaDTO) return false

        return nombre == other.nombre
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString() = "idCompania = $id" +
            " nombre = $nombre" +
            " idPersona = $idPersona" +
            "\nsubsidiarias =" + subsidiarias.toString()
}

/**
 * Data class to receive message for a new Corporate
 */
data class NuevoCorporativoDTO(val corporativo: CompaniaDTO, val administrador: UsuarioDTO)

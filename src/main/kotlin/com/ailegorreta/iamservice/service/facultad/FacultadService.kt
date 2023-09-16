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
 *  FacultadService.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.data.neo4j.service.ServiceUtils
import com.ailegorreta.iamservice.model.Facultad
import com.ailegorreta.iamservice.repository.FacultadRepository
import com.ailegorreta.iamservice.service.event.EventService
import com.ailegorreta.iamservice.service.facultad.dto.FacultadDTO
import com.ailegorreta.iamservice.service.facultad.dto.GraphUsuarioFacultadDTO
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Facultad service that includes all Permit services for
 * Facultades controller.
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 *
 */
@Service
class FacultadService constructor (private val facultadRepository: FacultadRepository,
                                   private val eventService: EventService): HasLogger {

    fun count() = facultadRepository.count()

    fun findAll(page: Int, size: Int, sortStr:String? = null): MutableIterable<Facultad>? {
        return facultadRepository.findAll(PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content
    }

    fun countByActvoIs(activo: Boolean) = facultadRepository.countByActvoIs(activo)

    fun findByActivoIs(activo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        facultadRepository.findByActivoIs(activo, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByNombreContains(nombre: String) = facultadRepository.countByNombreContains(nombre)

    fun findByNombreContains(nombre: String, page: Int, size: Int, sortStr:String? = null) =
        facultadRepository.findByNombreContains(nombre, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByNombreContainsAndActivoIs(nombre: String, activo: Boolean) =
        facultadRepository.countByNombreContainsAndActivoIs(nombre, activo)

    fun findByNombreContainsAndActivoIs(nombre: String, activo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        facultadRepository.findByNombreContainsAndActivoIs(nombre, activo, PageRequest.of(page, size,
            ServiceUtils.sortOrder(sortStr = sortStr))).content


    fun findById(id: Long): Optional<FacultadDTO> {
        val optional = facultadRepository.findById(id)

        if (optional.isPresent())
            return Optional.of(FacultadDTO.fromEntity(optional.get()))

        return Optional.empty()
    }

    fun findByNombre(nombre: String): Optional<FacultadDTO> {
        val facultad = facultadRepository.findByNombre(nombre)

        if (facultad.isPresent)
            return Optional.of(FacultadDTO.fromEntity(facultad.get()))

        return Optional.empty()
    }

    /**
     * Add a new Permit without any roles assigned to it
     */
    @Transactional
    fun add(facultadDTO: FacultadDTO): FacultadDTO {
        logger.info("Alta/modificacion de una facultad:${facultadDTO.nombre}")

        val facultad = Facultad(id = facultadDTO.id,
            nombre = facultadDTO.nombre,
            descripcion = facultadDTO.descripcion,
            tipo = facultadDTO.tipo.toString(),
            activo = facultadDTO.activo,
            usuarioModificacion = facultadDTO.usuarioModificacion)

        facultadRepository.save(facultad)

        val result = FacultadDTO.fromEntity(facultad)

        // send the event for a add/update Facultad
        if (facultadDTO.id == null)
            eventService.sendEvent(userName = facultadDTO.usuarioModificacion, eventName = "ALTA_FACULTAD", value = result)
        else
            eventService.sendEvent(userName = facultadDTO.usuarioModificacion, eventName = "ACTUALIZA_FACULTAD", value = result)

        return result
    }

    /**
     * Read all facultades from a User
     *
     */
    fun findUsuarioFacultades(nombreUsuario: String): Collection<FacultadDTO> {
        val facultades = facultadRepository.findUsuarioFacultades(nombreUsuario)
        val facultadesDTO = mutableSetOf<FacultadDTO>()

        facultades.forEach {
            facultadesDTO.add(FacultadDTO.fromEntity(it))
        }

        return facultadesDTO
    }

    /**
     * Same as previous method but just validates one Permit only.
     * We read all permits from the user since we believe it is more
     * efficient to do it in just two queries query and leave the work to Chyper.
     * Future knowledge of Chyper can optimize this query
     */
    fun hasUsuarioFacultad(nombreUsuario: String, nombreFacultad: String): Boolean {
        var facultades = findUsuarioFacultades(nombreUsuario)

        facultades.forEach {
            if (nombreFacultad.equals(it.nombre))
                return true
        }

        return false
    }

    /**
     * Same as findUsuarioFacultades method but as a graph of Users andPermits only
     */
    fun graphUsuarioFacultades(nombreUsuario: String) = GraphUsuarioFacultadDTO.mapFromEntity(findUsuarioFacultades(nombreUsuario), nombreUsuario)

}

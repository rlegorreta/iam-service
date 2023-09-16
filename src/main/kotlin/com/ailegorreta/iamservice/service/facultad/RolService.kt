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
 *  RolService.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.data.neo4j.service.ServiceUtils
import com.ailegorreta.iamservice.exception.IAMException
import com.ailegorreta.iamservice.model.Rol
import com.ailegorreta.iamservice.repository.FacultadRepository
import com.ailegorreta.iamservice.repository.RolRepository
import com.ailegorreta.iamservice.service.event.EventService
import com.ailegorreta.iamservice.service.facultad.dto.AssignFacultadDTO
import com.ailegorreta.iamservice.service.facultad.dto.RolDTO
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Rol service that includes all Role services for
 * Facultades controller.
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 *
 */
@Service
class RolService constructor (private val rolRepository: RolRepository,
                              private val facultadRepository: FacultadRepository,
                              private val eventService: EventService): HasLogger {

    fun count() = rolRepository.count()

    fun findAll(page: Int, size: Int, sortStr:String? = null) =
        rolRepository.findAll(PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByActvoIs(activo : Boolean) = rolRepository.countByActivoIs(activo)

    fun findByActivoIs(activo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        rolRepository.findByActivoIs(activo, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByNombreContains(nombre: String) = rolRepository.countByNombreContains(nombre)

    fun findByNombreContains(nombre: String, page: Int, size: Int, sortStr:String? = null) =
        rolRepository.findByNombreContains(nombre, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr)),0).content

    fun countByNombreContainsAndActivoIs(nombre: String, activo: Boolean) =
        rolRepository.countByNombreContainsAndActivoIs(nombre, activo)

    fun findByNombreContainsAndActivoIs(nombre: String, activo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        rolRepository.findByNombreContainsAndActivoIs(nombre, activo, PageRequest.of(page, size,
            ServiceUtils.sortOrder(sortStr = sortStr))).content

    fun findById(id: Long): Optional<RolDTO> {
        val optional = rolRepository.findById(id)

        if (optional.isPresent)
            return Optional.of(RolDTO.fromEntity(optional.get()))

        return Optional.empty()
    }

    fun findByIdRol(idRol: Long): Optional<RolDTO> {
        val optional = rolRepository.findByIdRol(idRol)

        if (optional.isPresent)
            return Optional.of(RolDTO.fromEntity(optional.get()))

        return Optional.empty()
    }

    /**
     * Add/update a Rol without any permit assigned
     */
    @Transactional
    fun add(rolDTO: RolDTO): RolDTO {
        val optional = rolRepository.findByIdRol(rolDTO.idRol)
        val rol: Rol

        if (optional.isPresent) {
            logger.info("Modificacion de un nuevo rol:${rolDTO.nombre}")
            rol = optional.get()
            rol.idRol = rolDTO.idRol
            rol.nombre = rolDTO.nombre
            rol.activo = rolDTO.activo
            rol.usuarioModificacion = rolDTO.usuarioModificacion
            // note: since we re-read the Role we respect the TIENE_FACULTAD relationship from the existing role.
            //       if the rolDTO came with new Factultades they are IGNORED
        } else {
            logger.info("Alta de un nuevo rol:${rolDTO.nombre}")
            rol = Rol(id = rolDTO.id,
                idRol = rolDTO.idRol,
                nombre = rolDTO.nombre,
                activo = rolDTO.activo,
                usuarioModificacion = rolDTO.usuarioModificacion)
        }
        rolRepository.save(rol)

        val result = RolDTO.fromEntity(rol)

        // Send the event for a add/upate Rol
        if (rolDTO.id == null)
            eventService.sendEvent(userName = rolDTO.usuarioModificacion, eventName = "NUEVO_ROL", value = result)
        else
            eventService.sendEvent(userName = rolDTO.usuarioModificacion, eventName = "ACTUALIZA_ROL", value = result)

        return result
    }

    /**
     * Assign a new Permit for a existing (or new) Role.
     *
     */
    @Transactional
    fun assignPermit(nombre:String, rolDTO: RolDTO) : AssignFacultadDTO {
        var newRol: Rol

        if (rolDTO.id != null) {  // Existing Role
            val optRol = rolRepository.findById(rolDTO.id)

            if (!optRol.isPresent())
                throw IAMException("No existe el rol:" + rolDTO.idRol + " en la base de datos(" + rolDTO.id + ")")

            newRol = optRol.get()
        } else {                    // new role
            newRol = Rol(
                idRol = rolDTO.idRol,
                nombre = rolDTO.nombre,
                activo = rolDTO.activo,
                usuarioModificacion = rolDTO.usuarioModificacion
            )

            rolRepository.save(newRol)
        }

        val facultad = facultadRepository.findByNombre(nombre)

        if (!facultad.isPresent)
            throw IAMException("No existe la facultad:" + nombre +  " en la base de datos")

        logger.info("Se asignó la facultad:${facultad.get().nombre} al rol:${rolDTO.nombre}")
        newRol.addFacultad(facultad.get())
        rolRepository.addPermit(newRol.id!!, facultad.get().id!!)

        val result = AssignFacultadDTO(nombre,RolDTO.fromEntity(newRol))

        eventService.sendEvent(userName = newRol.usuarioModificacion, eventName = "ASIGNACION_FACULTAD", value = result)

        return result
    }

    /**
     * Unassigned an existing Permit for a existing Role.
     * This is equivalent to delete the link to Role
     *
     */
    @Transactional
    fun unAssignPermit(nombre: String, rolDTO: RolDTO) : AssignFacultadDTO {
        if (rolDTO.id == null)
            throw IAMException("No existe el rol:" + rolDTO.nombre + " en la base de datos")

        var deleted = false

        rolDTO.facultades
            .filter{ nombre.equals(it.nombre)}
            .forEach {
                deleted = true
                var result = rolRepository.unAssignPermit(nombre, rolDTO.id)

                if (result != null)
                    throw IAMException("Error en el cypher query:" + result)

                return@forEach
            }
        if (!deleted)
            throw IAMException("No esta asignado la facultad:" + nombre + " al rol:" + rolDTO.nombre)

        logger.info("Se desasignó la facultad:$nombre al rol:${rolDTO.nombre}")

        val optRol = rolRepository.findById(rolDTO.id)

        if (!optRol.isPresent())
            throw IAMException("No existe el rol:" + rolDTO.id + " en la base de datos")

        val result = AssignFacultadDTO(nombre,RolDTO.fromEntity(optRol.get()))

        // send the event for an update Grupo
        eventService.sendEvent(userName = optRol.get().usuarioModificacion, eventName = "DES_ASIGNACION_FACULTAD", value = result)

        return result
    }

}

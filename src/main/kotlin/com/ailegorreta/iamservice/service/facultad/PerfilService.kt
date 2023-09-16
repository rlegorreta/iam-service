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
 *  PerfilService.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.data.neo4j.service.ServiceUtils
import com.ailegorreta.iamservice.exception.IAMException
import com.ailegorreta.iamservice.model.Perfil
import com.ailegorreta.iamservice.repository.PerfilRepository
import com.ailegorreta.iamservice.repository.RolRepository
import com.ailegorreta.iamservice.service.event.EventService
import com.ailegorreta.iamservice.service.facultad.dto.AssignRolDTO
import com.ailegorreta.iamservice.service.facultad.dto.PerfilDTO
import com.ailegorreta.iamservice.service.facultad.dto.FacultadDTO
import com.ailegorreta.iamservice.service.facultad.dto.GraphPerfilRolFacultadDTO
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Perfil service that includes all Profile services for
 * Facultades controller.
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 *
 */
@Service
class PerfilService constructor (private val perfilRepository: PerfilRepository,
                                 private val rolRepository: RolRepository,
                                 private val eventService: EventService): HasLogger {

    fun count() = perfilRepository.count()

    fun findAll(page: Int, size: Int, sortStr:String? = null) =
        perfilRepository.findAll(PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).getContent()

    fun countByActvoIs(isActivo: Boolean) = perfilRepository.countByActivoIs(isActivo)

    fun findByActivoIs(activo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        perfilRepository.findByActivoIs(activo, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByNombreContains(nombre: String) = perfilRepository.countByNombreContains(nombre)

    fun findByNombreContains(nombre: String, page: Int, size: Int, sortStr:String? = null) =
        perfilRepository.findByNombreContains(nombre, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByNombreContainsAndActivoIs(nombre: String, isActivo: Boolean) = perfilRepository.countByNombreContainsAndActivoIs(nombre, isActivo)

    fun findByNombreContainsAndActivoIs(nombre: String, isActivo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        perfilRepository.findByNombreContainsAndActivoIs(nombre, isActivo, PageRequest.of(page, size,
            ServiceUtils.sortOrder(sortStr = sortStr))).content

    fun findById(id: Long, depth: Boolean = true): Optional<PerfilDTO> {
        val optional = if (depth) perfilRepository.findDepthById(id)
        else perfilRepository.findById(id)

        if (optional.isPresent)
            return Optional.of(PerfilDTO.fromEntity(optional.get()))

        return Optional.empty()
    }

    /*
      * This is necessary because derived attributes in Noe4j Spring Data
      * does not work with depth parameter, just read it by Id
      */
    fun findByNombre(name: String, depth: Boolean = true): Optional<PerfilDTO> {
        val optional = if (depth) perfilRepository.findDepthByNombre(name)
        else perfilRepository.findByNombre(name)

        if (optional.isPresent)
            return Optional.of(PerfilDTO.fromEntity(optional.get()))

        return Optional.empty()
    }

    /**
     * Add a new Profile without eny roles assigned
     */
    @Transactional
    fun add(perfilDTO: PerfilDTO): PerfilDTO {
        val optional = perfilRepository.findByNombre(perfilDTO.nombre)
        val perfil: Perfil

        if (optional.isPresent) {
            logger.info("Modificacion de un nuevo perfil:${perfilDTO.nombre}")
            perfil = optional.get()
            perfil.nombre = perfilDTO.nombre
            perfil.descripcion = perfilDTO.descripcion
            perfil.activo = perfilDTO.activo
            perfil.patron = perfilDTO.patron
            perfil.usuarioModificacion = perfilDTO.usuarioModificacion
        } else {
            logger.info("Alta de un perfil:${perfilDTO.nombre}")
            perfil = Perfil(id = perfilDTO.id,
                nombre = perfilDTO.nombre,
                descripcion = perfilDTO.descripcion,
                activo = perfilDTO.activo,
                patron = perfilDTO.patron,
                usuarioModificacion = perfilDTO.usuarioModificacion)
        }
        perfilRepository.save(perfil)

        val result = PerfilDTO.fromEntity(perfil)

        // send the event for a add/ update Profile
        if (perfilDTO.id == null)
            eventService.sendEvent(userName = perfilDTO.usuarioModificacion, eventName = "ALTA_PERFIL", value = result)
        else
            eventService.sendEvent(userName = perfilDTO.usuarioModificacion, eventName = "UPDATE_PERFIL", value = result)

        return result
    }

    /**
     * Assign a new Role for a existing (or new) Profile.
     *
     */
    @Transactional
    fun assignRole(idRol: Long, perfilDTO: PerfilDTO) : AssignRolDTO {
        val newPerfil: Perfil

        if (perfilDTO.id != null) {  // Existing Perfil
            val optPerfil = perfilRepository.findById(perfilDTO.id)

            if (!optPerfil.isPresent)
                throw IAMException("No existe el perfil:" + perfilDTO.id + " en la base de datos")

            newPerfil = optPerfil.get()
        } else {                    // new Perfil
            newPerfil = Perfil(
                nombre = perfilDTO.nombre,
                descripcion = perfilDTO.descripcion,
                activo = perfilDTO.activo,
                patron = perfilDTO.patron,
                usuarioModificacion = perfilDTO.usuarioModificacion
            )
            perfilRepository.save(newPerfil)
        }

        val rol = rolRepository.findByIdRol(idRol)

        if (!rol.isPresent)
            throw IAMException("No existe el rol:" + idRol + " en la base de datos")

        logger.info("Se asignó el rol:${rol.get().nombre} al perfil:${perfilDTO.nombre}")
        newPerfil.addRol(rol.get())
        perfilRepository.addRol(newPerfil.id!!, rol.get().id!!)

        val result = AssignRolDTO(idRol, PerfilDTO.fromEntity(newPerfil))

        // send the event for a update Grupo
        eventService.sendEvent(userName = newPerfil.usuarioModificacion, eventName = "ASIGNACION_ROL", value = result)

        return result
    }

    /**
     * Unassigned an existing Role for a existing Profile.
     * This is equivalent to delete the link to Profile
     *
     */
    @Transactional
    fun unAssignRole(idRol: Long, perfilDTO: PerfilDTO) : AssignRolDTO {
        if (perfilDTO.id == null)
            throw IAMException("No existe el perfil:" + perfilDTO.nombre + " en la base de datos")

        var deleted = false

        perfilDTO.roles
            .filter{ idRol == it.idRol}
            .forEach {
                deleted = true
                val result = perfilRepository.unAssignRole(idRol, perfilDTO.id)

                if (result != null)
                    throw IAMException("Error en el cypher query:" + result)

                return@forEach
            }
        if (!deleted)
            throw IAMException("No esta asignado el rol:" + idRol + " al perfil:" + perfilDTO.nombre)

        logger.info("Se desasignó el rol: $idRol  al perfil:${perfilDTO.nombre}")

        val optPerfil = perfilRepository.findById(perfilDTO.id)

        if (!optPerfil.isPresent)
            throw IAMException("No existe el perfil:" + perfilDTO.id + " en la base de datos")

        val result = AssignRolDTO(idRol, PerfilDTO.fromEntity(optPerfil.get()))

        // send the event for a update Grupo
        eventService.sendEvent(userName = optPerfil.get().usuarioModificacion, eventName = "DES_ASIGNACION_ROL", value = result)

        return result
    }

    /**
     * Reads all facultades that are in the same Perfil.
     * If some facultad is repeated (i.e., exist for more than one Role)
     * is is listed once
     */
    fun getPerfilFacultades(nombrePerfil : String): Collection<FacultadDTO> {
        val perfil = perfilRepository.findDepthByNombre(nombrePerfil)
        val facultades = mutableSetOf<FacultadDTO>()

        if (!perfil.isPresent)
            throw IAMException("Perfil no existe en la base de datos")

        perfil.get().roles?.let {
            it.forEach {
                it.facultades?.let {
                    it.forEach {
                        facultades.add(FacultadDTO.fromEntity(it))
                    }
                }
            }
        }

        return facultades
    }

    /**
     * Same as previous method but a graph of Profiles, Roles and Permits
     * is displayed.
     */
    fun graphPerfilFacultades(nombrePerfil : String) = GraphPerfilRolFacultadDTO.mapFromEntity(perfilRepository.findDepthByNombre(nombrePerfil).get())

}

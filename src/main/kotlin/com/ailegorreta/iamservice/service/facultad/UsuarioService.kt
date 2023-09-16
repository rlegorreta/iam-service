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
 *  UsuarioService.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.data.neo4j.service.ServiceUtils
import com.ailegorreta.iamservice.exception.IAMException
import com.ailegorreta.iamservice.model.Usuario
import com.ailegorreta.iamservice.repository.CompaniaRepository
import com.ailegorreta.iamservice.repository.FacultadRepository
import com.ailegorreta.iamservice.repository.PerfilRepository
import com.ailegorreta.iamservice.repository.UsuarioRepository
import com.ailegorreta.iamservice.service.event.EventService
import com.ailegorreta.iamservice.service.facultad.dto.AssignPerfilDTO
import com.ailegorreta.iamservice.service.facultad.dto.UsuarioDTO
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.ArrayList

/**
 * Usuario service that includes all User services for
 * Facultades controller.
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 *
 */
@Service
class UsuarioService constructor (private val usuarioRepository: UsuarioRepository,
                                  private val perfilRepository: PerfilRepository,
                                  private val facultadRepository: FacultadRepository,
                                  private val companiaRepository: CompaniaRepository,
                                  private val eventService : EventService): HasLogger {

    fun findAll() = usuarioRepository.findAll()

    fun findAll(page: Int, size: Int, sortStr:String? = null) =
        usuarioRepository.findAll(PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).getContent()

    fun countByActvoIs(isActivo: Boolean) = usuarioRepository.countByActivoIs(isActivo)

    fun findByActivoIs(activo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        usuarioRepository.findByActivoIs(activo, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByNombreUsuarioContains(nombre: String) = usuarioRepository.countByNombreUsuarioContains(nombre)

    fun findByNombreUsuarioContains(nombre: String, page: Int, size: Int, sortStr:String? = null) =
        usuarioRepository.findByNombreUsuarioContains(nombre, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

    fun countByNombreUsuarioContainsAndActivoIs(nombre: String, isActivo: Boolean) = usuarioRepository.countByNombreUsuarioContainsAndActivoIs(nombre, isActivo)

    fun findByNombreUsuarioContainsAndActivoIs(nombre: String, isActivo: Boolean, page: Int, size: Int, sortStr:String? = null) =
        usuarioRepository.findByNombreUsuarioContainsAndActivoIs(nombre, isActivo, PageRequest.of(page, size,
            ServiceUtils.sortOrder(sortStr = sortStr))).content
    fun findByNombreUsuarioContains(nombreUsuario: String) =
        usuarioRepository.findByNombreUsuarioContains(nombreUsuario)
    /**
     * Return all Usuario data (no matter is Administrador or Operation)
     */
    fun findById(id: Long): Optional<UsuarioDTO> {
        val optional = usuarioRepository.findById(id)

        if (optional.isPresent())
            return Optional.of(UsuarioDTO.fromEntity(optional.get()))

        return Optional.empty()
    }

    /**
     * Return just the Usuario operational data
     */
    fun findByIdUsuario(id: Long): Optional<UsuarioDTO> {
        var usuario =  usuarioRepository.findByIdUsuario(id)

        if (usuario != null)
            return Optional.of(UsuarioDTO.fromEntity(usuario))

        return Optional.empty()
    }

    /**
     * Return just the Usuario operational data.
     *
     * Also initialize the nombreCompania relationship
     */
    fun findByNombreUsuario(nombre: String): Optional<UsuarioDTO> {
        var usuario = usuarioRepository.findByNombreUsuario(nombre)

        if (usuario != null) {
            // read the virtual link to companias
            val companias = companiaRepository.findCompaniasByEmpleado(usuario.id!!)

            if (companias.isNotEmpty())
                usuario.nombreCompania = companias.first().nombre

            return Optional.of(UsuarioDTO.fromEntity(usuario))
        }

        return Optional.empty()
    }

    /**
     * Assign a new Profile for a existing User. Is a profile exist
     * then the user changes Profile
     *
     * If the Profile does not exist a new Profile is created
     */
    @Transactional
    fun assignProfile(assignPerfilDTO: AssignPerfilDTO) : AssignPerfilDTO {
        var newUser: Usuario
        val userDTO = assignPerfilDTO.usuarioDTO

        if (userDTO.id != null) {  // Existing user
            val optUser = usuarioRepository.findById(userDTO.id)

            if (!optUser.isPresent())
                throw IAMException("No existe el usuario:" + userDTO.id + " en la base de datos")

            newUser = optUser.get()
        } else {                    // new user
            newUser = Usuario(
                idUsuario = userDTO.idUsuario,
                nombreUsuario = userDTO.nombreUsuario,
                nombre = userDTO.nombre,
                apellido = userDTO.apellido,
                telefono = userDTO.telefono,
                mail = userDTO.mail,
                interno = userDTO.interno,
                activo = userDTO.activo,
                administrador = userDTO.administrador,
                fechaIngreso = userDTO.fechaIngreso,
                zonaHoraria = userDTO.zonaHoraria,
                usuarioModificacion = userDTO.usuarioModificacion,
                fechaModificacion = userDTO.fechaModificacion
            )
            usuarioRepository.save(newUser)
        }

        val perfil = perfilRepository.findByNombre(assignPerfilDTO.nombre)

        if (!perfil.isPresent)
            throw IAMException("No existe el perfil:" + assignPerfilDTO.nombre + " en la base de datos")

        if (newUser.perfil != null) {
            logger.info("Se des asignó al usuario:${newUser.nombre} el perfil:${newUser.perfil!!.nombre}")
            usuarioRepository.unAssignPerfil(newUser.idUsuario, newUser.perfil!!.id!!)
        }
        logger.info("Se asignó al usuario:${newUser.nombre} el perfil:${perfil.get().nombre}")
        newUser.perfil = perfil.get()
        newUser.fechaModificacion = assignPerfilDTO.usuarioDTO.fechaModificacion
        newUser.usuarioModificacion = assignPerfilDTO.usuarioDTO.usuarioModificacion
        usuarioRepository.assignPerfil(newUser.idUsuario, perfil.get().id!!, newUser.fechaModificacion, newUser.usuarioModificacion)

        val result = AssignPerfilDTO(assignPerfilDTO.nombre, UsuarioDTO.fromEntity(newUser))
        // send the event to inform other micro services that the user receives a new Profile
        eventService.sendEvent(userName = userDTO.usuarioModificacion, eventName = "ASIGNACION_PERFIL_USUARIO", value = result)

        return result
    }

    /**
     * Assign a new extra Permit for a existing User.
     *
     */
    @Transactional
    fun assignFacultad(assignFacultad: AssignPerfilDTO): AssignPerfilDTO {
        val usuarioDTO = assignFacultad.usuarioDTO

        if (usuarioDTO.id == null)
            throw IAMException("El usuario:" + usuarioDTO.nombre + " debe existir en la base de datos")

        val optUsuario = usuarioRepository.findById(usuarioDTO.id)

        if (!optUsuario.isPresent())
            throw IAMException("No existe el usuario:" + usuarioDTO.nombre + " en la base de datos")

        val usuario = optUsuario.get()
        val facultad = facultadRepository.findByNombre(assignFacultad.nombre)

        if (!facultad.isPresent)
            throw IAMException("No existe la facultad:" + assignFacultad.nombre + " en la base de datos")

        logger.info("Se asignó al usuario:${usuario.nombre} la facultad extra: ${facultad.get().nombre}")
        usuario.addExtraFacultad(facultad.get())
        usuario.fechaModificacion = usuarioDTO.fechaModificacion
        usuario.usuarioModificacion = usuarioDTO.usuarioModificacion
        usuarioRepository.assignFacultad(usuario.idUsuario, facultad.get().id!!, usuarioDTO.fechaModificacion, usuarioDTO.usuarioModificacion)

        var result = AssignPerfilDTO(assignFacultad.nombre, UsuarioDTO.fromEntity(usuario))

        // send the event to inform other micro services that the user was assigned for a new permit
        eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName = "ASIGNACION_FACULTAD_EXTRA_USUARIO", value = result)

        return result

    }

    /**
     * Unassigned a new Permit for a existing User.
     *
     */
    @Transactional
    fun unAssignFacultad(assignFacultad: AssignPerfilDTO): AssignPerfilDTO {
        val usuarioDTO = assignFacultad.usuarioDTO

        if (usuarioDTO.id == null)
            throw IAMException("El usuario:" + usuarioDTO.nombre + " debe existir en la base de datos")

        val optUsuario = usuarioRepository.findById(usuarioDTO.id)

        if (!optUsuario.isPresent())
            throw IAMException("No existe el usuario:" + usuarioDTO.nombre + " en la base de datos")

        val usuario = optUsuario.get()
        val facultad = facultadRepository.findByNombre(assignFacultad.nombre)

        if (!facultad.isPresent)
            throw IAMException("No existe la facultad:" + assignFacultad.nombre + " en la base de datos")

        logger.info("Se desasignó al usuario:${usuario.nombre} la facultad extra:${facultad.get().nombre}")
        usuario.fechaModificacion = usuarioDTO.fechaModificacion
        usuario.usuarioModificacion = usuarioDTO.usuarioModificacion
        usuario.removeExtraFacultad(facultad.get())
        usuarioRepository.unAssignFacultad(usuario.idUsuario, facultad.get().id!!, usuarioDTO.fechaModificacion, usuarioDTO.usuarioModificacion)

        var result = AssignPerfilDTO(assignFacultad.nombre, UsuarioDTO.fromEntity(usuario))

        // send the event to inform other microservices that the user was un assigned a permit
        eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName = "DES_ASIGNACION_FACULTAD_EXTRA_USUARIO", value = result)

        return result
    }

    /**
     * Assign a new forbidden Permit for a existing User.
     *
     */
    @Transactional
    fun forbidFacultad(forbidFacultad: AssignPerfilDTO): AssignPerfilDTO {
        val usuarioDTO = forbidFacultad.usuarioDTO

        if (usuarioDTO.id == null)
            throw IAMException("El usuario:" + usuarioDTO.nombre + " debe existir en la base de datos")

        val optUsuario = usuarioRepository.findById(usuarioDTO.id)

        if (!optUsuario.isPresent())
            throw IAMException("No existe el usuario:" + usuarioDTO.nombre + " en la base de datos")

        val usuario = optUsuario.get()
        val facultad = facultadRepository.findByNombre(forbidFacultad.nombre)

        if (!facultad.isPresent)
            throw IAMException("No existe la facultad:" + forbidFacultad.nombre + " en la base de datos")

        logger.info("Se quitó al usuario:${usuario.nombre} la facultad individual:${facultad.get().nombre}")
        usuario.fechaModificacion = usuarioDTO.fechaModificacion
        usuario.usuarioModificacion = usuarioDTO.usuarioModificacion
        usuario.addSinFacultad(facultad.get())
        usuarioRepository.forbidFacultad(usuario.idUsuario, facultad.get().id!!, usuarioDTO.fechaModificacion, usuarioDTO.usuarioModificacion)

        var result = AssignPerfilDTO(forbidFacultad.nombre, UsuarioDTO.fromEntity(usuario))

        // send the event to inform other microservices that the user was forbidden a permit
        eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName = "PROHIBICION_DE_UNA_FACULTAD_AL_USUARIO", value = result)

        return result
    }

    /**
     * Unassigned a new forbidden Permit for a existing User.
     *
     */
    @Transactional
    fun unForbidFacultad(forbidFacultad: AssignPerfilDTO): AssignPerfilDTO {
        val usuarioDTO = forbidFacultad.usuarioDTO

        if (usuarioDTO.id == null)
            throw IAMException("El usuario:" + usuarioDTO.nombre + " debe existir en la base de datos")

        val optUsuario = usuarioRepository.findById(usuarioDTO.id)

        if (!optUsuario.isPresent())
            throw IAMException("No existe el usuario:" + usuarioDTO.nombre + " en la base de datos")

        val usuario = optUsuario.get()
        val facultad = facultadRepository.findByNombre(forbidFacultad.nombre)

        if (!facultad.isPresent)
            throw IAMException("No existe la facultad:" + forbidFacultad.nombre + " en la base de datos")

        logger.info("Se eliminó la facultad al usuario:${usuario.nombre} la facultad individual:${facultad.get().nombre}")
        usuario.fechaModificacion = usuarioDTO.fechaModificacion
        usuario.usuarioModificacion = usuarioDTO.usuarioModificacion
        usuario.removeSinFacultad(facultad.get())
        usuarioRepository.unForbidFacultad(usuario.idUsuario, facultad.get().id!!, usuarioDTO.fechaModificacion, usuarioDTO.usuarioModificacion)

        var result = AssignPerfilDTO(forbidFacultad.nombre, UsuarioDTO.fromEntity(usuario))

        // send the event to inform other micro services that the user was erased a forbidden permit
        eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName = "BORRADO_DE_UNA_PROHIBICION_DE_UNA_FACULTAD_AL_USUARIO", value = result)
        return result
    }

    /**
     * Get all users for the same Company that has de faculty name
     */
    fun findEmpleadosByFacultad(nombreCompania: String, nombreFacultad: String) = usuarioRepository.findEmpleadosByFacultad(nombreCompania, nombreFacultad)

    /**
     * Get all users for the same Companay
     */
    fun findUsuariosByCompania(nombreCompania: String): Collection<UsuarioDTO> {
        val usuarios = usuarioRepository.findUsuariosByCompania(nombreCompania) ?: return ArrayList()

        var result: ArrayList<UsuarioDTO> = ArrayList()

        usuarios.forEach {
            result.add(UsuarioDTO.fromEntity(usuarioRepository.findById(it.id!!).get()))
        }

        return result
    }

}

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
 *  UsuarioCompaniaService.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania

import java.util.*
import java.time.LocalDate

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.ailegorreta.iamservice.model.*
import com.ailegorreta.iamservice.service.compania.dto.*
import com.ailegorreta.iamservice.exception.IAMException
import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.repository.*
import com.ailegorreta.iamservice.service.event.EventService
import java.time.LocalDateTime
import kotlin.collections.ArrayList

/**
 * Usuario service that includes all User services for
 * Company operations
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 *
 */
@Service
class UsuarioCompaniaService constructor (private val usuarioRepository: UsuarioRepository,
										  private val grupoRepository: GrupoRepository,
										  private val grupoService: GrupoService,
										  private val perfilRepository: PerfilRepository,
										  private val areaAsignadaRepository: AreaAsignadaRepository,
										  private val companiaService: CompaniaService,
										  private val companiaRepository: CompaniaRepository,
										  private val eventService : EventService): HasLogger {

	companion object {
		val PERFIL_NAME = "Administrador"            // Administrator Profile
	}

	fun findAll() = usuarioRepository.findAll()

	/*
     * Return all Usuarios data (no matter is Administration or Operation)
     */
	fun findById(id: Long, depth: Boolean = false): Optional<UsuarioDTO> {
		val optional = usuarioRepository.findById(id)

		if (optional.isPresent) {
			val result = Optional.of(UsuarioDTO.fromEntity(optional.get()))

			if (depth) {   // initialize the companias relationship
				val companias = ArrayList<CompaniaDTO>()

				companiaRepository.findCompaniasByEmpleado(result.get().id!!).forEach {
					companias.add(CompaniaDTO.fromEntity(it))
				}
				result.get().companias = companias
			}

			return result
		}

		return Optional.empty()
	}

	/*
     * Return just the UsuarioDTO data for Administration
     */
	fun findByIdUsuario(id: Long, depth: Boolean = false): Optional<UsuarioDTO> {
		val usuario = usuarioRepository.findByIdUsuario(id)

		if (usuario != null) {
			val result =  Optional.of(UsuarioDTO.fromEntity(usuario))

			if (depth) {   // initialize the companias relationship
				val companias = ArrayList<CompaniaDTO>()

				companiaRepository.findCompaniasByEmpleado(result.get().id!!).forEach {
					companias.add(CompaniaDTO.fromEntity(it))
				}
				result.get().companias = companias
			}

			return result
		}

		return 	Optional.empty()
	}

	/*
     * Return just the UsuarioDTO data for Administration
     *
     */
	fun findByNombreUsuario(nombre: String, depth: Boolean = false): Optional<UsuarioDTO> {
		val usuario = usuarioRepository.findByNombreUsuario(nombre)

		if (usuario != null) {
			val result =  Optional.of(UsuarioDTO.fromEntity(usuario))

			if (depth) {   // initialize the companias relationship
				val companias = ArrayList<CompaniaDTO>()

				companiaRepository.findCompaniasByEmpleado(result.get().id!!).forEach {
					companias.add(CompaniaDTO.fromEntity(it))
				}
				result.get().companias = companias
			}

			return result
		}

		return 	Optional.empty()
	}

	/*
 	 * This method is used when a newCorporativo is added in the
 	 * event listener
 	*/
	fun findByMail(mail : String, depth: Boolean = false) : Optional<UsuarioDTO> {
		val usuario = usuarioRepository.findByMail(mail)

		if (usuario != null) {
			val result = Optional.of(UsuarioDTO.fromEntity(usuario))

			if (depth) {   // initialize the companias relationship
				val companias = ArrayList<CompaniaDTO>()

				companiaRepository.findCompaniasByEmpleado(result.get().id!!).forEach {
					companias.add(CompaniaDTO.fromEntity(it))
				}
				result.get().companias = companias
			}

			return result
		}

		return Optional.empty()
	}

	/*
	 * Read all agents by Supervisor.
	 * note:The relationship SUPERVISOR just exist in Neo4j and
	 *      not in SDN Rx because it is a circular relationship.
	 */
	fun findUsuariosBySupervisor(nombre: String): Collection<UsuarioDTO> {
		val result = ArrayList<UsuarioDTO>()
		val agentes = usuarioRepository.findUsuariosBySupervisor(nombre)

		agentes.forEach {
			result.add(UsuarioDTO.fromEntity(it))
		}

		return result
	}

	/*
	 * Reads a Usuario (agent) and its supervisor link
	 *
	 * Depth indicates the virtual relationshps are fetched
	 *
	 * note:The relationship SUPERVISOR just exist in Neo4j and
	 *      not in SDN Rx because it is a circular relationship.
	 */
	fun findByIdWithSupervisor(id: Long, depth: Boolean = false): Optional<UsuarioDTO> {
		val optional = usuarioRepository.findById(id)
		val usuario: Usuario

		if (!optional.isPresent)
			return Optional.empty()
		else
			usuario = optional.get()

		val result = Optional.of(UsuarioDTO.fromEntity(usuario))

		val supervisor = usuarioRepository.findByIdSupervisor(id)

		if (supervisor.isPresent)
			result.get().supervisor = UsuarioDTO.fromEntity(supervisor.get())
		if (depth) {   // initialize the companias  and grupos relationship
			val companias = ArrayList<CompaniaDTO>()

			companiaRepository.findCompaniasByEmpleado(usuario.id!!).forEach {
				companias.add(CompaniaDTO.fromEntity(it))
			}
			result.get().companias = companias

			val grupos = ArrayList<GrupoDTO>()

			grupoRepository.findGruposByEmpleado(usuario.id!!).forEach {
				grupos.add(GrupoDTO.fromEntity(it))
			}
			result.get().grupos = grupos
		}

		return result
	}

	/*
 	 * Reads a Usuario (agent) and its supervisor link usign the usuario nombre
	 *
 	 * note:The relationship SUPERVISOR just exist in Neo4j and
 	 *      not in SDN Rx because it is a circular relationship.
 	*/
	fun findByNombreUsuarioWithSupervisor(nombre: String,  depth: Boolean = false): Optional<UsuarioDTO> {
		val usuario = usuarioRepository.findByNombreUsuario(nombre)
		val result: Optional<UsuarioDTO>

		if (usuario == null)
			return Optional.empty()
		else
			result = Optional.of(UsuarioDTO.fromEntity(usuario))

		val supervisor = usuarioRepository.findByIdSupervisor(usuario.id!!)

		if (supervisor.isPresent())
			result.get().supervisor = UsuarioDTO.fromEntity(supervisor.get())
		if (depth) {   // initialize the companias relationship
			val companias = ArrayList<CompaniaDTO>()

			companiaRepository.findCompaniasByEmpleado(usuario.id!!).forEach {
				companias.add(CompaniaDTO.fromEntity(it))
			}
			result.get().companias = companias
		}

		return result
	}

	fun findByInternoIsAndAdministradorIs(interno: Boolean, administrador: Boolean): Collection<UsuarioDTO> {
		val res = ArrayList<UsuarioDTO>()

		usuarioRepository.findByInternoIsAndAdministradorIs(interno, administrador).forEach {
			res.add(UsuarioDTO.fromEntity(it))
		}

		return res
	}

	/*
	 * Assign a new Administrator member for a designated Group.
	 *
	 * If the Administrator does not exist a new User is created
	 */
	@Transactional
	fun newMember(grupoId: Long, member:UsuarioDTO) : UsuarioDTO {
		logger.info("Nuevo miembro ${member.nombre} del grupo:$grupoId")
		val optional = grupoRepository.findById(grupoId)

		if (!optional.isPresent)
			throw IAMException("No existe el grupo:" + grupoId + " en la base de datos")

		val grupo = optional.get()
		val newAdmin: Usuario

		logger.info("Alta de un nuevo administrador:" + member.nombre + " al grupo:" + grupo.nombre)
		if (member.id != null) {  // Existing user
			val optUser = usuarioRepository.findById(member.id)

			if (!optUser.isPresent)
				throw IAMException("No existe el usuario:" + member.id + " en la base de datos")

			newAdmin = optUser.get()
			if ((newAdmin.perfil != null) && (!newAdmin.perfil!!.patron))
				throw IAMException("El usuario que se quiere asingar NO tiene el perfil de administrador")

		} else {					// new user
			newAdmin =  Usuario(idUsuario = member.idUsuario,
				nombreUsuario = member.nombreUsuario,
				nombre = member.nombre,
				apellido = member.apellido,
				telefono = member.telefono,
				mail = member.mail,
				interno = member.interno,
				activo = member.activo,
				administrador = true,
				fechaIngreso = LocalDate.now(),
				zonaHoraria = member.zonaHoraria,
				usuarioModificacion = member.usuarioModificacion,
				fechaModificacion = LocalDateTime.now())

			usuarioRepository.save(newAdmin)

			val perfil = perfilRepository.findByNombre(PERFIL_NAME)

			if (!perfil.isPresent)
				throw IAMException("No existe el perfil de Administradores de las empresas en la base de datos")

			usuarioRepository.addPerfil(member.idUsuario, perfil.get().id!!)

			newAdmin.perfil = perfil.get()
		}
		usuarioRepository.addGrupo(newAdmin.idUsuario, grupo.nombre)

		val result = UsuarioDTO.fromEntity(newAdmin)

		result.grupos = grupoService.findGruposByEmpleado(newAdmin.id!!)

		// send the event for a new member
		eventService.sendEvent(userName = member.usuarioModificacion, eventName = "NUEVO_MIEMBRO_GRUPO", value = result)

		return result
	}

	/*
     * Add or update a user.
     *
     */
	@Transactional
	fun addUser(usuarioDTO: UsuarioDTO): UsuarioDTO {
		logger.info("Alta/actualización de un empleado o usuario:" + usuarioDTO.nombre)
		if (usuarioDTO.companias.isEmpty() && (!usuarioDTO.administrador))
			throw IAMException("El usuario debe de estar asignado al menos a una compañía")

		val usuario:Usuario
		val oldCompanias: Collection<Compania>
		val oldGrupos: Collection<Grupo>

		try {
			if (usuarioDTO.id == null) {
				usuario = Usuario(id = usuarioDTO.id,
					idUsuario = usuarioDTO.idUsuario,
					nombreUsuario = usuarioDTO.nombreUsuario,
					nombre = usuarioDTO.nombre,
					apellido =usuarioDTO.apellido,
					telefono = usuarioDTO.telefono,
					mail = usuarioDTO.mail,
					interno = usuarioDTO.interno,
					activo = usuarioDTO.activo,
					administrador = usuarioDTO.administrador,
					usuarioModificacion = usuarioDTO.usuarioModificacion,
					zonaHoraria = usuarioDTO.zonaHoraria,
					fechaModificacion = LocalDateTime.now(),
					fechaIngreso = usuarioDTO.fechaIngreso)
				usuarioRepository.save(usuario)
				oldCompanias = ArrayList()
				oldGrupos = ArrayList()
			} else {
				val optional = usuarioRepository.findById(usuarioDTO.id)

				if (!optional.isPresent)
					throw IAMException("No existe el usuario:${usuarioDTO.id} en la base de datos")

				usuario = optional.get()
				// update all fields and companias relationships
				usuario.idUsuario = usuarioDTO.idUsuario
				usuario.nombreUsuario = usuarioDTO.nombreUsuario
				usuario.nombre = usuarioDTO.nombre
				usuario.apellido = usuarioDTO.apellido
				usuario.telefono = usuarioDTO.telefono
				usuario.mail = usuarioDTO.mail
				usuario.interno = usuarioDTO.interno
				usuario.activo = usuarioDTO.activo
				usuario.administrador = usuarioDTO.administrador
				usuario.usuarioModificacion = usuarioDTO.usuarioModificacion
				usuario.zonaHoraria = usuarioDTO.zonaHoraria
				usuario.fechaIngreso = usuarioDTO.fechaIngreso

				oldCompanias = companiaRepository.findCompaniasByEmpleado(usuarioDTO.id)
				oldGrupos = grupoRepository.findGruposByEmpleado(usuarioDTO.id)
				// Update just fields in order not to alter othe relationshisp lis TIENE_PERFIL, SIN_FACUlTAD, etc
				usuarioRepository.update(usuario.id!!, usuario.nombre, usuario.nombreUsuario,usuario.telefono,
					usuario.mail, usuario.interno, usuario.activo, usuario.administrador,
					usuario.fechaIngreso, usuario.usuarioModificacion, LocalDateTime.now() )
				// Update the Supervisor link
				usuarioRepository.deleteSupervisor(usuario.id!!)
			}

			usuario.administrador = !usuarioDTO.grupos.isEmpty() // check is the User is still and Administrator

			// Update the virtual company links
			oldCompanias.forEach {
				usuarioRepository.deleteCompania(usuario.id!!, it.nombre)
			}
			usuarioDTO.companias.forEach {
				usuarioRepository.addCompania(usuario.idUsuario, it.nombre)
			}
			// Update the virtual grupos links
			oldGrupos.forEach {
				usuarioRepository.deleteGrupo( usuario.id!!, it.id!!)
			}
			usuarioDTO.grupos.forEach {
				usuarioRepository.addGrupo( usuario.idUsuario, it.nombre)
			}
			if (usuarioDTO.supervisor != null)
				usuarioRepository.addSupervisor(usuario.id!!, usuarioDTO.supervisor!!.id!!)
		} catch (e: Exception) {
			logger.error("Existió algún error al almacenar al administrador ${e.message}")
			throw(e)
		}

		val result = UsuarioDTO.fromEntity(usuario)

		// send the event to inform other microservices
		if (usuarioDTO.id == null)
			if (usuarioDTO.grupos.isEmpty())
				eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName= "NUEVO_USUARIO", value = result)
			else
				eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName = "NUEVO_ADMINISTRADOR", value = result)
		else
			if (usuarioDTO.grupos.isEmpty())
				eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName= "ACTUALIZACION_USUARIO", value = result)
			else
				eventService.sendEvent(userName = usuarioDTO.usuarioModificacion, eventName = "ACTUALIZACION_ADMINISTRADOR", value = result)

		return 	result
	}

	/*
     * Retrieves the employees that some Administrator has access to
     *
     * note: The Employees has access to the Companies if we need that information
     */
	fun findEmpleadosByAdministrador(nombreAdministrador: String): Collection<UsuarioDTO> {
		val empleados = usuarioRepository.findEmpleadosPermiteByAdministrador(nombreAdministrador)
			.plus(usuarioRepository.findEmpleadosPermiteSubsidiariasByAdministrador(nombreAdministrador))
			.plus(usuarioRepository.findEmpleadosPermiteSinHerenciaByAdministrador(nombreAdministrador))
			.plus(usuarioRepository.findAdministradoresByAdministradorMaestro(nombreAdministrador))

		val empleadosDTO = mutableSetOf<UsuarioDTO>()

		empleados.forEach {
			empleadosDTO.add(UsuarioDTO.fromEntity(it))
		}

		return empleadosDTO
	}

	/*
	 * This is because we need to re-read each Usuari in order to get all its relationships
	 * because SDN RX does no bring all relationships
	 *
	 * Include Administrators that are not employees and are linked by Group
	 */
	private fun findEmpleadosByAdministradorDeep(nombreAdministrador: String): Collection<UsuarioDTO> {
		val empleados = usuarioRepository.findEmpleadosPermiteByAdministrador(nombreAdministrador)
			.plus(usuarioRepository.findEmpleadosPermiteSubsidiariasByAdministrador(nombreAdministrador))
			.plus(usuarioRepository.findEmpleadosPermiteSinHerenciaByAdministrador(nombreAdministrador))
			.plus(usuarioRepository.findAdministradoresByAdministrador(nombreAdministrador))

		val empleadosDTO = mutableSetOf<UsuarioDTO>()

		empleados.forEach {
			val empleadoDTO = UsuarioDTO.fromEntity(usuarioRepository.findById(it.id!!).get())

			// Include virtual relationship with companias
			empleadoDTO.companias = findCompaniasByEmpleado(empleadoDTO.id!!)

			empleadosDTO.add(empleadoDTO)
			// Include administrators that maybe are not employees
			if (empleadoDTO.grupos.count() > 0) {
				val administradores = usuarioRepository.findAdministradoresByAdministrador(empleadoDTO.nombre)

				administradores.forEach{
					empleadosDTO.add(UsuarioDTO.fromEntity(usuarioRepository.findById(it.id!!).get()))
				}
			}

		}

		return empleadosDTO
	}

	/*
	 * Because the relationship to companias is virtual for SpringData perfomance issues, this method returns the
	 * collection of companies where the employee works
	 */
	private fun findCompaniasByEmpleado(id: Long): Collection<CompaniaDTO> {
		val companias = companiaRepository.findCompaniasByEmpleado(id)
		val result: ArrayList<CompaniaDTO> = ArrayList()

		companias.forEach { result.add(CompaniaDTO.fromEntity(it)) }

		return result
	}

	/*
     * Same as previous method but as a graph
     */
	fun graphEmpleadosByAdministrador(nombreAdministrador: String): GraphEmpleadosAdministradorDTO {
		val empleadosDTO = findEmpleadosByAdministradorDeep(nombreAdministrador)

		// Fill manually the subsidiaria link
		empleadosDTO.forEach {
			it.companias.forEach { cia ->
				if (cia.subsidiarias == null)
					companiaService.findSubsidiarias(cia.nombre).forEach {
						cia.addSubsidiaria(CompaniaDTO.fromEntity(it))
					}
			}
		}

		return GraphEmpleadosAdministradorDTO.mapFromEntity(empleadosDTO)
	}

	/*
     * Retrieves the employees that the Master Administrator has access to
     */
	fun findEmpleadosByAdministradorMaestro(nombreAdministrador: String): Collection<UsuarioDTO> {
		val empleados = usuarioRepository.findEmpleadosPermiteByAdministradorMaestro(nombreAdministrador)
			.plus(usuarioRepository.findEmpleadosPermiteSubsidiariasByAdministradorMaestro(nombreAdministrador))

		val empleadosDTO = mutableSetOf<UsuarioDTO>()

		empleados.forEach {
			empleadosDTO.add(UsuarioDTO.fromEntity(it))
		}

		return empleadosDTO
	}

	/*
 	 * This is because we need to re-read each Usuario in order to get all its relationships
 	 * because SDN RX does no bring all relationships.
 	 * Include Administrators that are not employees and are linked by Group
 	*/
	private fun findEmpleadosByAdministradorMaestroDeep(nombreAdministrador: String): Collection<UsuarioDTO> {
		val empleados = usuarioRepository.findEmpleadosPermiteByAdministradorMaestro(nombreAdministrador)
			.plus(usuarioRepository.findEmpleadosPermiteSubsidiariasByAdministradorMaestro(nombreAdministrador))

		val empleadosDTO = mutableSetOf<UsuarioDTO>()

		empleados.forEach {
			val empleadoDTO = UsuarioDTO.fromEntity(it)

			// Include virtual relationship with companias
			empleadoDTO.companias = findCompaniasByEmpleado(empleadoDTO.id!!)
			// Include virtual relationship with grupos
			empleadoDTO.grupos = grupoService.findGruposByEmpleado(empleadoDTO.id)
			empleadosDTO.add(empleadoDTO)
			// Include administrators that maybe are not employees
			if (empleadoDTO.grupos.isNotEmpty()) {

				val administradores = usuarioRepository.findAdministradoresByAdministradorMaestro(empleadoDTO.nombre)

				administradores.forEach{
					empleadosDTO.add(UsuarioDTO.fromEntity(it))   // usuarioRepository.findById(it.id!!).get()))
				}
			}
		}

		return empleadosDTO
	}

	/*
     * Same as previous method but as a graph
     */
	fun graphEmpleadosByAdministradorMaestro(nombreAdministrador: String): GraphEmpleadosAdministradorDTO {
		val empleadosDTO = findEmpleadosByAdministradorMaestroDeep(nombreAdministrador)

		// Fill manually the subsidiary link
		empleadosDTO.forEach { it ->
			it.companias.forEach { cia ->
				if (cia.subsidiarias == null)
					companiaService.findSubsidiarias(cia.nombre).forEach {
						cia.addSubsidiaria(CompaniaDTO.fromEntity(it))
					}
			}
		}

		return  GraphEmpleadosAdministradorDTO.mapFromEntity(empleadosDTO)
	}

	/*
 	 * Assign a new Area for an existing User.
 	 *
 	 */
	@Transactional
	fun assignArea(idArea:Long, usuarioDTO: UsuarioDTO) : AsignaAreaDTO {
		val usuario: Usuario

		if (usuarioDTO.id != null) {  // Existing User
			val optUser = usuarioRepository.findById(usuarioDTO.id)

			if (!optUser.isPresent())
				throw IAMException("No existe el usuario:${usuarioDTO.nombre} en la base de datos(${usuarioDTO.id})")

			usuario = optUser.get()
		} else
			throw IAMException("No existe el usuario:${usuarioDTO.nombre} en la base de datos")

		val area = areaAsignadaRepository.findByIdArea(idArea)

		if (!area.isPresent)
			throw IAMException("No existe el área compania:$idArea en la base de datos")

		logger.info("Se asignó el área:${area.get().nombre} al usuario:${usuarioDTO.nombre}")
		usuario.addArea(Asignado(0L, false, area.get()))

		usuarioRepository.assignArea(usuario.id!!, area.get().idArea, false)

		val result = AsignaAreaDTO(idArea, UsuarioDTO.fromEntity(usuario))

		// send the event for a update Grupo
		eventService.sendEvent(userName = usuario.usuarioModificacion, eventName = "ASIGNACION_AREA", value = result)

		return result
	}

	/*
 	 * Un assigns an existing Area for an existing User.
 	 * This is equivalent to delete the link to ASIGNADO:
 	 *
  	 */
	@Transactional
	fun unassignArea(idArea: Long, usuarioDTO: UsuarioDTO) : AsignaAreaDTO {
		if (usuarioDTO.id == null)
			throw IAMException("No existe el usuario:${usuarioDTO.nombre} en la base de datos")

		var deleted = false

		usuarioDTO.areas
			.filter{ idArea.equals(it.area.idArea)}
			.forEach {
				deleted = true
				val result = usuarioRepository.unAssignArea( usuarioDTO.id, idArea)

				if (result != null && result.equals(0))
					throw IAMException("Error en el cypher query:$result")

				return@forEach
			}
		if (!deleted)
			throw IAMException("No esta asignada el área $idArea al usuario:${usuarioDTO.nombre}")

		logger.info("Se desasignó el área :$idArea al usuario:${usuarioDTO.nombre}")

		val optRol = usuarioRepository.findById(usuarioDTO.id)

		if (!optRol.isPresent)
			throw IAMException("No existe el usuario:${usuarioDTO.id} en la base de datos")

		val result = AsignaAreaDTO(idArea, UsuarioDTO.fromEntity(optRol.get()))

		// send the event for unassigned the Company
		eventService.sendEvent(userName = optRol.get().usuarioModificacion, eventName = "DES_ASIGNACION_AREA", value = result)

		return result
	}

	/*
 	 * Retrieves the employees that are assigned to a Company
 	 */
	fun findEmpleadosAssigned(idArea: Long): Collection<UsuarioDTO> {
		val agentes = usuarioRepository.findEmpleadosAssigned(idArea)

		val agentesDTO = mutableSetOf<UsuarioDTO>()

		if (agentes != null)
			agentes.forEach {
				agentesDTO.add(UsuarioDTO.fromEntity(usuarioRepository.findById(it.id!!).get()))
			}

		return agentesDTO
	}

	/*
	 * Retrieves the pendent approval for assignments between Companias and Users
	 */
	fun findSolicitudesAsignacion(): Collection<UsuarioDTO> {
		val agentes = usuarioRepository.findSolicitudesAsignacion()

		val agentesDTO = ArrayList<UsuarioDTO>()
		val avoidDuplicates = mutableSetOf<Long>()

		if (agentes != null)
			agentes.forEach {
				if (!avoidDuplicates.contains(it.id!!)) {
					avoidDuplicates.add(it.id!!)
					agentesDTO.add(UsuarioDTO.fromEntity(usuarioRepository.findById(it.id!!).get()))
				}
			}

		return agentesDTO
	}

	/*
	 * Approves the assignment between Company and User
	 */
	@Transactional
	fun approveSolicitudAsignacion(approve: AproveAsingacionDTO): AproveAsingacionDTO {
		usuarioRepository.aproveSolicitudAsignacion(approve.nombreUsuario, approve.idArea, approve.approve)

		// send the event for the approval
		eventService.sendEvent(userName = approve.nombreUsuario, eventName = "AREA", value = approve)

		return approve
	}
}

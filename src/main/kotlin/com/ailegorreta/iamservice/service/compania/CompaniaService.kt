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
 *  CompaniaService.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania

import java.util.Optional
import java.time.LocalDate

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.ailegorreta.iamservice.model.*
import com.ailegorreta.iamservice.service.compania.dto.*
import com.ailegorreta.iamservice.exception.IAMException
import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.repository.*
import com.ailegorreta.data.neo4j.service.ServiceUtils
import com.ailegorreta.iamservice.service.event.EventService
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

/**
 * Company service that includes all Corporate services.
 *  
 * @author rlh
 * @project : iam-service
 * @date September 2023
 * 
 */
@Service
class CompaniaService (private val companiaRepository: CompaniaRepository,
					   private val grupoRepository: GrupoRepository,
					   private val areaRepository: AreaRepository,
					   private val areaAsignadaRepository: AreaAsignadaRepository,
					   private val perfilRepository: PerfilRepository,
					   private val usuarioRepository: UsuarioRepository,
					   private val eventService: EventService): HasLogger {

	companion object  {
		const val GRUPO_ADMIN_NAME = "Administrador del Corporativo:"  // Initial Group to be created for the Administrator
		const val PERFIL_NAME = "Administrador Maestro"                // Corporate administrator Profile
    }

	fun findAll() = companiaRepository.findAll()

	fun findAllCustomers(page: Int, size: Int, sortStr:String? = null) =
			companiaRepository.findByNegocioIsNotAndActivo("NA", true, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

	fun countAllCustomers() = companiaRepository.countAllCustomers()   // solo las companias donde Negocio no sea NA

	fun findAllCompaniesChkAgente(usuario: String, page: Int, size: Int, sortStr:String? = null): MutableList<Compania>  {
		if (usuario == "*")
			return companiaRepository.findByNegocioIsNotAndActivo("NA", true, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

		// We need to check what companies the salesman is assigned to, so we start looking from Usuario node instead from
		// Companias
		val salesman: Usuario? = usuarioRepository.findByNombreUsuario(usuario) ?: throw IAMException("El agente $usuario no existe en la base de datos")
		val result = HashMap<String, Compania>()

		salesman!!.areas?.forEach {
			if (it.activo) {
				val compania = companiaRepository.findCompaniaByArea(it.area.nombre)
				var prevCompania = result[compania!!.nombre]

				if (prevCompania == null) {
					result[compania.nombre] = compania
					prevCompania = compania
				}
				prevCompania.addArea(it.area.toArea())
			}
		}
		// now check the possible page
		val end = if ((page + size) > result.size) result.size else (page + size)

		return result.values.toMutableList().subList(page, end)
	}

	fun countAllCompaniesChkAgente(usuario: String): Long {
		if (usuario == "*")
			return companiaRepository.countAllCustomers()

		// We need to check what funds the agent is assigned to, so we start looking from Usuario node instead from
		// Companias or funds
		val salesman: Usuario? = usuarioRepository.findByNombreUsuario(usuario) ?: throw IAMException("El agente $usuario no existe en la base de datos")

		val result = HashMap<String, Compania>()

		salesman!!.areas?.forEach {
			if (it.activo) {
				val compania = companiaRepository.findCompaniaByArea(it.area.nombre)
				var prevCompania = result.get(compania!!.nombre)

				if (prevCompania == null) {
					result[compania.nombre] = compania
					prevCompania = compania
				}
				prevCompania.addArea(it.area.toArea())
			}
		}

		return result.size.toLong()
	}

	fun findByNegocio(negocio: String, page: Int, size: Int, sortStr:String? = null) =
			companiaRepository.findByNegocioAndActivo(negocio, true, PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

	fun countByNegocio(negocio: String, activo: Boolean) = companiaRepository.countByNegocioAndActivo(negocio, activo)

	fun findByNombreContainsAndNegocioIsNot(nombre: String, negocio: String, page: Int, size: Int, sortStr:String? = null) =
			companiaRepository.findByNombreContainsAndNegocioIsNotAndActivo(nombre, negocio, true,
																			PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

	fun countByNombreContainsAndNegocioIsNot(nombre: String, negocio: String) =
			companiaRepository.countByNombreContainsAndNegocioIsNotAndActivo(nombre, negocio, true)

	fun findByNombreContainsAndNegocio(nombre: String, negocio: String, page: Int, size: Int, sortStr:String? = null) =
			companiaRepository.findByNombreContainsAndNegocioAndActivo(nombre,  negocio, true,
																	   PageRequest.of(page, size, ServiceUtils.sortOrder(sortStr))).content

	fun countByNombreContainsAndNegocio(nombre: String, negocio: String) = companiaRepository.countByNombreContainsNegocioAndActivo(nombre, negocio, true)

	fun findById(id: Long):Optional<CompaniaDTO> {
		val optional = companiaRepository.findById(id)
		
		if (optional.isPresent)
			return Optional.of(CompaniaDTO.fromEntity(optional.get()))
		
		return Optional.empty()
	}

	fun findByIdPersona(idPersona: Long):Optional<CompaniaDTO> {
		if (idPersona <= 0L)
			return Optional.empty()			// avoid query Companies that does not exist in DB

		val optional = companiaRepository.findByIdPersona(idPersona)

		if (optional.isPresent)
			return Optional.of(CompaniaDTO.fromEntity(optional.get()))

		return Optional.empty()
	}
	
	fun findByNombre(nombre: String):Optional<CompaniaDTO> {
		val compania = companiaRepository.findByNombre(nombre)

		if (compania != null)
			return Optional.of(CompaniaDTO.fromEntity(compania))
		
		return Optional.empty()		
	}

	fun findSubsidiarias(nombre: String) = companiaRepository.findSubsidiarias(nombre)

	/*
     * Creates a new Corporate with its Administrator (must exist at least one
     * administrator per Corporate) and a Group that belongs to it.
     *
     * The new Administrator is a a new user (cannot be an existing one) with
     * a Profile that has at least one permit that is the ADMINISTRATOR permit.
     */
	@Transactional
	fun newCorporate(corpDTO: CompaniaDTO, adminDTO: UsuarioDTO): CompaniaDTO {
		logger.info("Alta de un nueva Afore|Operadora:${corpDTO.nombre} con el administrador:${adminDTO.nombre}")

		val corp = Compania(nombre = corpDTO.nombre,
                            padre = true,
						    usuarioModificacion = corpDTO.usuarioModificacion,
							fechaModificacion = LocalDateTime.now(),
							negocio = corpDTO.negocio.toString(),
							idPersona = corpDTO.idPersona,
							activo = corpDTO.activo)
		val grupo = Grupo(nombre = GRUPO_ADMIN_NAME + corpDTO.nombre,
			              activo = true,
						  usuarioModificacion = corpDTO.usuarioModificacion,
						  fechaModificacion = LocalDateTime.now())
		val profile = perfilRepository.findByNombre(PERFIL_NAME)
		val admin = Usuario(idUsuario = adminDTO.idUsuario,
							nombreUsuario = adminDTO.nombreUsuario,
						    nombre = adminDTO.nombre,
							apellido = adminDTO.apellido,
							fechaModificacion = LocalDateTime.now(),
							telefono = adminDTO.telefono,
                            mail = adminDTO.mail,
                            interno = false,
                            activo = true,
                            administrador = true,
                            perfil = profile.get(),
							fechaIngreso = LocalDate.now(),
							zonaHoraria = adminDTO.zonaHoraria,
							usuarioModificacion = corpDTO.usuarioModificacion)	  // Assign the Administrator Profile

		grupo.addPermiteCompania(corp)

		try {
			companiaRepository.save(corp)
			grupoRepository.save(grupo)
			usuarioRepository.save(admin)

			usuarioRepository.addCompania(admin.idUsuario, corp.nombre)
			usuarioRepository.addGrupo(admin.idUsuario, grupo.nombre)
			// Send auditory event
			val result = CompaniaDTO.fromEntity(corp)

			eventService.sendEvent(userName = corpDTO.usuarioModificacion, eventName = "NUEVO_CORPORATIVO", value = result)

			return result
		} catch (e: Exception) {
			logger.error("Existió algún error al crear un nuevo corporativo ${e.message}")
			throw(e)
		}
	}

	/**
	 *   Creates a new Area for a company..
	 *
	 *   The Operator must exist.
	 *   The Fund is a new Fund and must nos exists.
	 */
	@Transactional
	fun newArea(corporate: String, areaDTO: AreaDTO): AreaDTO {
		logger.info("Alta de una nueva Area de la compañía:$corporate area:${areaDTO.nombre}")

		val corp = companiaRepository.findByNombre(corporate) ?: throw IAMException("No existe la compañía:$corporate")
		val sub = companiaRepository.findByNombre(areaDTO.nombre)

		if (sub != null)
			throw IAMException("Ya existe una área con el nombre:" + areaDTO.nombre)

		val area = Area(nombre = areaDTO.nombre,
						usuarioModificacion = areaDTO.usuarioModificacion,
						fechaModificacion = LocalDateTime.now(),
						activo = areaDTO.activo,
						idArea = areaDTO.idArea,
					    idPersona = areaDTO.idPersona)

		try {
			areaRepository.save(area)
			corp.addArea(area)
			companiaRepository.addArea(corp.nombre, area.nombre)
			// Keep sync with AreaAsignada
			areaAsignadaRepository.save(
				AreaAsignada(
					nombre = areaDTO.nombre,
					usuarioModificacion = areaDTO.usuarioModificacion,
					fechaModificacion = LocalDateTime.now(),
					activo = areaDTO.activo,
					idArea = areaDTO.idArea,
					idPersona = areaDTO.idPersona
				)
			)
			val result = AreaDTO.fromEntity(area)
			eventService.sendEvent(userName = areaDTO.usuarioModificacion, eventName = "NUEVA_AREA", value = result)

			return result
		} catch (e: Exception) {
			logger.error("Existió algún error al crear una nueva área  ${e.message}")
			throw(e)
		}
	}

	/*
     * Retrieves the Companies that some Administrator has access to
     *
     */
	fun findCompaniasByAdministrador(nombreAdministrador: String): Collection<CompaniaDTO> {
		val companias = companiaRepository.findCompaniasPermiteByAdministrador(nombreAdministrador)
				.plus(companiaRepository.findCompaniasPermiteSubsidiariasByAdministrador(nombreAdministrador))
				.plus(companiaRepository.findCompaniasPermiteSinHerenciByAdministrador(nombreAdministrador))

		val companiasDTO = mutableSetOf<CompaniaDTO>()
		
		companias.forEach {
			companiasDTO.add(CompaniaDTO.fromEntity(it))
		}

		return companiasDTO		
	}

	/*
     * Same as previous method but as a graph 
     */
	fun graphCompaniasByAdministrador(nombreAdministrador: String): GraphCompaniasAdministradorDTO {
		val companiasDTO = findCompaniasByAdministrador(nombreAdministrador)

		// Fill manually the subsidiary link
		companiasDTO.forEach { cia ->
			if (cia.subsidiarias == null)
				findSubsidiarias(cia.nombre).forEach {
					cia.addSubsidiaria(CompaniaDTO.fromEntity(it))
				}
		}

		return GraphCompaniasAdministradorDTO.mapFromEntity(companiasDTO)
	}

	/*
  	* Activates or des-activates a new Company
 	*/
	@Transactional
	fun activateCompany(corpDTO: CompaniaDTO, activo: Boolean) {
		logger.info("Activation or desactivation of a new company:${corpDTO.nombre}")

		val corp = Compania(id = corpDTO.id,
							nombre = corpDTO.nombre,
						    padre = true,
							usuarioModificacion = corpDTO.usuarioModificacion,
							fechaModificacion = LocalDateTime.now(),
							negocio = corpDTO.negocio.toString(),
							idPersona = corpDTO.idPersona,
							activo = activo)

		companiaRepository.save(corp)
		eventService.sendEvent(userName = corpDTO.usuarioModificacion, eventName = "ACTIVAR_COMPANIA", value = corpDTO)
	}

}

/* Copyright (c) 2022, LMASS Desarrolladores, S.C.
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
*  GrupoService.kt   
*
*  Developed 2022 by LMASS Desarrolladores, S.C. www.legosoft.com.mx
*/
package com.ailegorreta.iamservice.service.compania

import java.util.Optional
import java.time.LocalDateTime

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.ailegorreta.iamservice.repository.CompaniaRepository
import com.ailegorreta.iamservice.repository.GrupoRepository
import com.ailegorreta.iamservice.repository.UsuarioRepository
import com.ailegorreta.iamservice.model.*
import com.ailegorreta.iamservice.service.compania.dto.*
import com.ailegorreta.iamservice.exception.IAMException
import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.service.event.EventService

/**
 * Group service that includes all Group services.
 *  
 * @author rlh
 * @project : iam-service
 * @date July 2023
 * 
 */
@Service
class GrupoService constructor (private val companiaRepository: CompaniaRepository,
								private val grupoRepository: GrupoRepository,
								private val usuarioRepository: UsuarioRepository,
								private val eventService : EventService): HasLogger {
	
	fun findAll() = grupoRepository.findAll()

	fun findById(id: Long): Optional<GrupoDTO> {
		val optional = grupoRepository.findById(id)
		
		if (optional.isPresent)
			return Optional.of(GrupoDTO.fromEntity(optional.get()))
					
		return Optional.empty()
	}

	fun findByNombre(nombre: String):Optional<GrupoDTO> {
		val grupo = grupoRepository.findByNombre(nombre)

		if (grupo != null)
			return Optional.of(GrupoDTO.fromEntity(grupo))

		return Optional.empty()
	}

	/**
	 *  Creates a new Group and at least one Administrator is linked
	 *  with the Group.
	 *
	 *  The user Administrator must exists previously in the DB
	 */
	@Transactional
	fun newGroup(grupoDTO: GrupoDTO, adminName: String): GrupoDTO {
		logger.info("Alta de un nuevo grupo:${grupoDTO.nombre} con el Administrador:$adminName")

		val grupo:Grupo

		if (grupoDTO.id == null)
			grupo = Grupo(nombre = grupoDTO.nombre,
						  activo = true,
						  usuarioModificacion = grupoDTO.usuarioModificacion,
						  fechaModificacion = LocalDateTime.now())
		else 
			throw IAMException("En este servicio no se puede actualizar un grupo, solo es de alta.")

		val admin:Usuario? = usuarioRepository.findByNombreUsuario(adminName) ?: throw IAMException("El usuario: $adminName no existe en la base de datos")

		// note: In this service it is not checked that the user has the ADMIN_EMP permit.
		//       this permit has to be assigned manually.			

		grupoDTO.permiteCompanias.forEach{
			val cia = companiaRepository.findByNombre(it.nombre) ?: throw IAMException("No existe la compania:" + it.nombre)

			grupo.addPermiteCompania(cia)
		}
		grupoDTO.noPermiteCompanias.forEach{
			val cia = companiaRepository.findByNombre(it.nombre) ?: throw IAMException("No existe la compania:" + it.nombre)

			grupo.addNoPermiteCompania(cia)
		}
		grupoDTO.permiteSinHerencia.forEach{
			val cia = companiaRepository.findByNombre(it.nombre) ?: throw IAMException("No existe la compania:" + it.nombre)

			grupo.addPermiteSinHerencia(cia)					
		}

		val newGrupo = grupoRepository.save(grupo)

		usuarioRepository.addGrupo(admin!!.idUsuario, newGrupo.nombre)
		admin.administrador = true  // if not is an Administrator make it
		usuarioRepository.save(admin)

		val result = GrupoDTO.fromEntity(newGrupo)

		// send the event for a new group
		eventService.sendEvent(userName = grupoDTO.usuarioModificacion, eventName = "NUEVO_GRUPO", value = result)

		return 	result
	}
	
	/**
	 *  Updates a Group.
	 *
	 */
	@Transactional
	fun updateGroup(grupoDTO: GrupoDTO): GrupoDTO {
		logger.info("Update de un grupo existente:${grupoDTO.nombre}")
		
		var grupo:Grupo
		
		if (grupoDTO.id == null)
			throw IAMException("En este servicio no se puede dar de alta un grupo, solo actualizar.")
		else {
			val optional = grupoRepository.findById(grupoDTO.id)

			if (optional.isPresent)
				grupo =  optional.get()
			else
				throw IAMException("No existe el grupo:" + grupoDTO.id + " en la base de datos")
		
			// update all fields and relationships
			grupo.nombre = grupoDTO.nombre
			grupo.activo = grupoDTO.activo
			grupo.usuarioModificacion = grupoDTO.usuarioModificacion
			grupo.permiteCompanias?.clear() ?: run { grupo.permiteCompanias = linkedSetOf() }
			grupo.noPermiteCompanias?.clear() ?: run { grupo.noPermiteCompanias = linkedSetOf() }
			grupo.permiteSinHerencia?.clear() ?: run { grupo.noPermiteCompanias = linkedSetOf() }
		}
		
		grupoDTO.permiteCompanias.forEach {
			val cia =
				companiaRepository.findByNombre(it.nombre) ?: throw IAMException("No existe la compania:" + it.nombre)

			grupo.addPermiteCompania(cia)
		}

		grupoDTO.noPermiteCompanias.forEach {
			val cia = companiaRepository.findByNombre(it.nombre)
				?: throw IAMException("No existe la compania:" + it.nombre)

			grupo.addNoPermiteCompania(cia)
		}
		grupoDTO.permiteSinHerencia.forEach {
			val cia = companiaRepository.findByNombre(it.nombre)
				?: throw IAMException("No existe la compania:" + it.nombre)

			grupo.addPermiteSinHerencia(cia)
		}
		grupoRepository.save(grupo)

		var result = GrupoDTO.fromEntity(grupo)

		// send the event for update the group
		eventService.sendEvent(userName = grupoDTO.usuarioModificacion, eventName = "ACTUALIZA_GRUPO", value = result)

		return 	result	}
	
	/**
	 * Updates the four links for Grupo: PERMITE, OPERADO_POR, NO_PERMITE and PERMITE_SIN_HERENCA
	 *
	 */
	@Transactional
    fun updateLinksGrupo(grupoId: Long,
						 usuarioModificacion: String,
						 permiteCompanias: Collection<CompaniaDTO>? = null,
                         noPermiteCompanias: Collection<CompaniaDTO>? = null,
                         permiteSinHerencia: Collection<CompaniaDTO>? = null): GrupoDTO {
		val optional = grupoRepository.findById(grupoId)
		val grupo: Grupo

		if (optional.isPresent)
			grupo = optional.get()
		else
			throw IAMException("No existe el grupo:" + grupoId + " en la base de datos")

		logger.info("Se actualizaron los permisos de un grupo:${grupo.nombre}")
		if (grupo.nombre.startsWith(CompaniaService.GRUPO_ADMIN_NAME))
			throw IAMException("No se pueden modificar los permisos del grupo del usuario maestro:"+  grupo.nombre)

		grupo.usuarioModificacion = usuarioModificacion
		if ((permiteCompanias != null) && (permiteCompanias.isEmpty()) &&
			(permiteSinHerencia != null) && (permiteSinHerencia.isEmpty()))
			throw IAMException("El grupo debe de tener al menos una compania asignada")
		if (permiteCompanias != null) {
			grupo.permiteCompanias = linkedSetOf()
			permiteCompanias.forEach {
				val cia = companiaRepository.findById(it.id!!)
				
				if (!cia.isPresent)
					throw IAMException("No existe la compania:" + it.id + " " + it.nombre + " en la base de datos")
				grupo.addPermiteCompania(cia.get())
			}			
		}
		if (noPermiteCompanias != null) {
			grupo.noPermiteCompanias = linkedSetOf()
			noPermiteCompanias.forEach {
				val cia = companiaRepository.findById(it.id!!)
				
				if (!cia.isPresent)
					throw IAMException("No existe la compania:" + it.id + " " + it.nombre + " enla base de datos")
				grupo.addNoPermiteCompania(cia.get())
			}			
		}
		if (permiteSinHerencia != null) {
			grupo.permiteSinHerencia = linkedSetOf()
			permiteSinHerencia.forEach {
				val cia = companiaRepository.findById(it.id!!)
				
				if (!cia.isPresent)
					throw IAMException("No existe la compania:" + it.id + " " + it.nombre + " enla base de datos")
				grupo.addPermiteSinHerencia(cia.get())
			}			
		}
		grupoRepository.save(grupo)

		val result = GrupoDTO.fromEntity(grupo)

		// send the event for the links update Grupo
		eventService.sendEvent(userName = usuarioModificacion, eventName = "ACTUALIZA_GRUPO", value = result)

		return result
	}
	
	/*
     * Count hor many members (Administrator) does a Group has
     */
	fun countMiembros(id: Long) = grupoRepository.countMiembros(id)

	/*
	 * Find the Grupos from a Usuario
	 */
	fun findGruposByEmpleado(id: Long): Collection<GrupoDTO> {
		val grupos = grupoRepository.findGruposByEmpleado(id)
		val result = ArrayList<GrupoDTO>()

		grupos.forEach { result.add(GrupoDTO.fromEntity(it)) }

		return result
	}

}

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
 *  CompaniaController.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.controller

import com.ailegorreta.iamservice.service.compania.CompaniaService
import com.ailegorreta.iamservice.service.compania.GrupoService
import com.ailegorreta.iamservice.service.compania.UsuarioCompaniaService
import org.springframework.web.bind.annotation.*

import com.ailegorreta.iamservice.model.*
import com.ailegorreta.iamservice.service.compania.*
import com.ailegorreta.iamservice.service.compania.dto.*

/**
 * Controller for all REST services for the administrative operation (not for
 * daily operations for the IAM
 *
 * @author rlh
 * @project : iam-service
 * @date June 2023
 *
 */
// @CrossOrigin
@RestController
@RequestMapping("/iam/compania")
class CompaniaController (val companiaService: CompaniaService,
                          val grupoService: GrupoService,
                          val usuarioCompaniaService: UsuarioCompaniaService
) {

    /**
     *  Companias
     */
    @GetMapping("/companias")
    fun findAllCompanias() = companiaService.findAll()

    @GetMapping("/compania/by/id")
    fun findCompaniaById(@RequestParam(required = true) id: Long) =  companiaService.findById(id)

    @GetMapping("/compania/by/nombre")
    fun findCompaniaByNombre(@RequestParam(required = true) nombre: String) = companiaService.findByNombre(nombre)

    @GetMapping("/compania/by/idPersona")
    fun findCompaniaByIdPersona(@RequestParam(required = true) idPersona: Long) =  companiaService.findByIdPersona(idPersona)

    @PostMapping("/compania/nuevo/corporativo")
    fun newCorporate(@RequestBody newCorporativo: NewCorporativoDTO) = companiaService.newCorporate(newCorporativo.companiaDTO, newCorporativo.usuarioDTO)

    @PostMapping("/compania/nuevo/area")
    fun newArea(@RequestBody newArea: NewAreaDTO) = companiaService.newArea(newArea.compania, newArea.areaDTO)

    @GetMapping("/compania/admin/companias")
    fun getAdminstratoCompanies(@RequestParam(required = true) nombreAdministrador: String) = companiaService.findCompaniasByAdministrador(nombreAdministrador)

    @GetMapping("/compania/admin/grafo/companias")
    fun graphCompaniasByAdministrador(@RequestParam(required = true) nombreAdministrador: String) = companiaService.graphCompaniasByAdministrador(nombreAdministrador)

    @GetMapping("/companias/clientes")
    fun findAllCustomers(@RequestParam(required = true) page: Int,
                         @RequestParam(required = true) size: Int,
                         @RequestParam(required = false) sort: String? = null) = companiaService.findAllCustomers(page, size, sort)

    @GetMapping("/companias/clientes/count")
    fun countAllCustomers() = companiaService.countAllCustomers()

    @GetMapping("/companias/clientes/chkagente")
    fun findAllCompaniesChkAgente(@RequestParam(required=true) usuario: String,
                                  @RequestParam(required = true) page: Int,
                                  @RequestParam(required = true) size: Int,
                                  @RequestParam(required = false) sort: String? = null) =
        companiaService.findAllCompaniesChkAgente(usuario, page, size, sort)

    @GetMapping("/companias/clientes/chkagente/count")
    fun countAllCompaniesChkAgente(@RequestParam(required=true) usuario: String) =
        companiaService.countAllCompaniesChkAgente(usuario)

    @GetMapping("/companias/negocio")
    fun findCompaniasByNegocio(@RequestParam(required = true) negocio: String,
                               @RequestParam(required = true) page: Int,
                               @RequestParam(required = true) size: Int,
                               @RequestParam(required = false) sort: String? = null) = companiaService.findByNegocio(negocio, page, size, sort)

    @GetMapping("/companias/negocio/count")
    fun countByNegocio(@RequestParam(required = true) negocio: String,
                       @RequestParam(required = false, defaultValue = "true") activo: Boolean) = companiaService.countByNegocio(negocio, activo)

    @GetMapping("/companias/nombre")
    fun findByNombreContaining(@RequestParam(required = true) nombre: String,
                               @RequestParam(required = true) page: Int,
                               @RequestParam(required = true) size: Int,
                               @RequestParam(required = false) sort: String? = null) =
        companiaService.findByNombreContainsAndNegocioIsNot(nombre, Negocio.NA.toString(),page, size, sort)

    @GetMapping("/companias/nombre/count")
    fun countByNombreContaining(@RequestParam(required = true) nombre: String) =
        companiaService.countByNombreContainsAndNegocioIsNot(nombre, Negocio.NA.toString())

    @GetMapping("/companias/nombre/negocio")
    fun findNegocioByNombreContains(@RequestParam(required = true) nombre: String,
                                    @RequestParam(required = true) negocio: String,
                                    @RequestParam(required = true) page: Int,
                                    @RequestParam(required = true) size: Int,
                                    @RequestParam(required = false) sort: String? = null) = companiaService.findByNombreContainsAndNegocio(nombre, negocio, page, size, sort)

    @GetMapping("/companias/nombre/negocio/count")
    fun countByNombreContainsAndNegocio(@RequestParam(required = true) nombre: String,
                                        @RequestParam(required = true) negocio: String) = companiaService.countByNombreContainsAndNegocio(nombre, negocio)


    /**
     *  Grupos
     */
    @GetMapping("/grupos")
    fun findAllGrupos() = grupoService.findAll()

    @GetMapping("/grupo/by/id")
    fun findGrupoById(@RequestParam(required = true) id: Long) =  grupoService.findById(id)

    @PostMapping("/grupo/add")
    fun newGroup(@RequestBody newGrupo: NewGrupoDTO) =  grupoService.newGroup(newGrupo.grupoDTO, newGrupo.nombre)

    @PostMapping("/grupo/update")
    fun updateGroup(@RequestBody newGrupo: GrupoDTO) =  grupoService.updateGroup(newGrupo)

    @PostMapping("/grupo/actualiza")
    fun updateGroup(@RequestBody updateGrupo: UpdateGrupoDTO) =  grupoService.updateLinksGrupo(updateGrupo.grupoId,
        updateGrupo.usuarioModificacion!!,
        updateGrupo.permiteCompanias,
        updateGrupo.noPermiteCompanias,
        updateGrupo.permiteSinHerencia)

    @GetMapping("/grupo/miembros/count")
    fun countMiembros(@RequestParam(required = true) id: Long) =  grupoService.countMiembros(id)

    /**
     *  Usuarios
     */
    @GetMapping("/usuarios/admin")
    fun findAllUsuarios() = usuarioCompaniaService.findAll()

    @GetMapping("/usuario/admin/by/id")
    fun findUsuarioById(@RequestParam(required = true) id: Long) = usuarioCompaniaService.findById(id)

    @GetMapping("/usuario/admin/withsupervisor/by/id")
    fun findUsuarioByIdWithSupervisor(@RequestParam(required = true) id: Long,
                                      @RequestParam(required = false, defaultValue = "true") depth: Boolean) =
        usuarioCompaniaService.findByIdWithSupervisor(id, depth)

    @GetMapping("/usuario/admin/withsupervisor/by/nombreusuario")
    fun findUsuarioByNombreWithSupervisor(@RequestParam(required = true) nombreUsuario: String) = usuarioCompaniaService.findByNombreUsuarioWithSupervisor(nombreUsuario)

    @GetMapping("/usuario/admin/by/idUsuario")
    fun findByIdUsuario(@RequestParam(required = true) idUsuario: Long) = usuarioCompaniaService.findByIdUsuario(idUsuario)

    @GetMapping("/usuario/admin/by/nombreUsuario")
    fun findByNombreUsuario(@RequestParam(required = true) nombre: String) = usuarioCompaniaService.findByNombreUsuario(nombre)

    @GetMapping("/usuarios/admin/by/interno")
    fun findByInternoIsAndAdministradorIs(@RequestParam(required = true) interno: Int,
                                          @RequestParam(required = true) administrador: Int) =
        usuarioCompaniaService.findByInternoIsAndAdministradorIs(interno.equals(1), administrador.equals(1))

    @GetMapping("/usuarios/admin/by/supervisor")
    fun findUsuariosByNSupervisor(@RequestParam(required = true) nombreSupervisor: String) = usuarioCompaniaService.findUsuariosBySupervisor(nombreSupervisor)

    @PostMapping("/usuario/admin/nuevo/admin")
    fun newAdmin(@RequestBody newAdmin: NewAdminDTO) =  usuarioCompaniaService.newMember(newAdmin.idGrupo,newAdmin.admin)

    @PostMapping("/usuario/admin/add")
    fun addUser(@RequestBody newUser: UsuarioDTO) =  usuarioCompaniaService.addUser(newUser)

    @GetMapping("/usuario/admin/companias")
    fun getAdminCompanies(@RequestParam(required = true) nombreAdministrador: String) = companiaService.findCompaniasByAdministrador(nombreAdministrador)

    @GetMapping("/usuario/admin/empleados")
    fun findEmpleadosByAdministrador(@RequestParam(required = true) nombreAdministrador: String) = usuarioCompaniaService.findEmpleadosByAdministrador(nombreAdministrador)

    @GetMapping("/usuario/admin/grafo/empleados")
    fun graphEmpleadosByAdministrador(@RequestParam(required = true) nombreAdministrador: String) = usuarioCompaniaService.graphEmpleadosByAdministrador(nombreAdministrador)

    @GetMapping("/usuario/admin/maestro/empleados")
    fun getEmpleadosByAdministradorMaestro(@RequestParam(required = true) nombreAdministrador: String) = usuarioCompaniaService.findEmpleadosByAdministradorMaestro(nombreAdministrador)

    @GetMapping("/usuario/admin/maestro/grafo/empleados")
    fun graphEmpleadosByAdministradorMaestro(@RequestParam(required = true) nombreAdministrador: String) = usuarioCompaniaService.graphEmpleadosByAdministradorMaestro(nombreAdministrador)

    @PostMapping("usuario/admin/add/area")
    fun assignArea(@RequestBody assignArea: AsignaAreaDTO) =
        usuarioCompaniaService.assignArea(assignArea.idArea, assignArea.usuarioDTO)

    @PostMapping("usuario/admin/delete/area")
    fun unAssignArea(@RequestBody assignCompania: AsignaAreaDTO) =
        usuarioCompaniaService.unassignArea(assignCompania.idArea, assignCompania.usuarioDTO)

    @GetMapping("/usuarios/admin/asignados/area")
    fun findEmpleadosAssigned(@RequestParam(required = true) idArea: Long) =
        usuarioCompaniaService.findEmpleadosAssigned(idArea)

    @GetMapping("/usuarios/admin/solicitudes/asignacion")
    fun findSolicitudesAsignacion() = usuarioCompaniaService.findSolicitudesAsignacion()

    @PostMapping("/usuario/admin/solicitud/aprobar")
    fun aproveSolicitudAsignacion(@RequestBody approve: AproveAsingacionDTO) =
        usuarioCompaniaService.approveSolicitudAsignacion(approve)
}

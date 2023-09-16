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
 *  StatService
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.iamservice.repository.FacultadRepository
import com.ailegorreta.iamservice.repository.PerfilRepository
import com.ailegorreta.iamservice.repository.RolRepository
import com.ailegorreta.iamservice.repository.UsuarioRepository
import org.springframework.stereotype.Service

/**
 * Statistics service that includes to handle all dashboard queries.
 *
 * This is just a small set of queries since de IAM UI has not implement
 * but a few dashboards
 *
 * @author rlh
 * @project : iam-service
 * @date July 202
 */
@Service
class StatService constructor (private val usuarioRepository: UsuarioRepository,
                               private val perfilRepository: PerfilRepository,
                               private val rolRepository: RolRepository,
                               private val facultadRepository: FacultadRepository
) {

    fun porcFacultadesInactivas(): Int {
        val count = facultadRepository.count().toDouble()

        return if (count > 0)
            (((facultadRepository.countByActvoIs(false).toDouble() / count) * 100.0) + 0.5).toInt()
        else 0
    }

    fun porcRolesInactivos(): Int {
        val count = rolRepository.count().toDouble()

        return if (count > 0)
            (((rolRepository.countByActivoIs(false).toDouble() / count) * 100.0) + 0.5).toInt()
        else 0
    }

    fun porcPerfilesInactivos(): Int {
        val count = perfilRepository.count().toDouble()

        return if (count > 0)
            (((perfilRepository.countByActivoIs(false).toDouble() / count) * 100.0) + 0.5).toInt()
        else 0
    }

    fun porcUsuariosInactivos(): Int {
        val count = usuarioRepository.count().toDouble()

        return if (count > 0)
            (((usuarioRepository.countByActivoIs(false).toDouble() / count) * 100.0) + 0.5).toInt()
        else 0
    }
}

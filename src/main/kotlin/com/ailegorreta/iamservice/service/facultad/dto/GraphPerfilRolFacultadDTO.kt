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
 *  GraphPerfilRolFacultadDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad.dto

import com.ailegorreta.commons.dtomappers.LinkDTO
import com.ailegorreta.commons.dtomappers.NodeDTO
import com.ailegorreta.iamservice.model.Perfil
import java.util.ArrayList
import java.util.HashMap

/**
 * Data class to generate a Graph for Profiles, Roles and Permits
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 */
data class GraphPerfilRolFacultadDTO(val nodes: List<NodeDTO>,
                                     val edges: List<LinkDTO>) {

    companion object {
        fun mapFromEntity(perfil : Perfil?) : GraphPerfilRolFacultadDTO {
            val nodes = ArrayList<NodeDTO>()
            val edges = ArrayList<LinkDTO>()
            val roles = HashMap<Int, NodeDTO>()
            val facultades = HashMap<Int, NodeDTO>()
            var nodeId = 1;
            val perfilNodeId = nodeId

            if (perfil == null)
                return GraphPerfilRolFacultadDTO(nodes, edges)

            val nodePerfil = NodeDTO(id = nodeId, idNeo4j = perfil.id!!,
                caption = perfil.nombre, type = "perfil",
                subType = perfil.activo)

            nodes.add(nodePerfil)
            perfil.roles?.let {
                it.forEach {
                    val nodeRol: NodeDTO

                    nodeId++
                    nodeRol = NodeDTO(id = nodeId, idNeo4j = it.id!!,
                        caption = it.nombre, type = "rol",
                        subType = it.activo)
                    nodes.add(nodeRol)
                    roles.put(nodeRol.id, nodeRol)
                    edges.add(LinkDTO(source = perfilNodeId, target = nodeId, caption = "rol"))
                    it.facultades?.let {
                        it.forEach {
                            val nodeFacultad: NodeDTO

                            nodeId++
                            nodeFacultad = NodeDTO(caption = it.nombre, type = "facultad",
                                id = nodeId, idNeo4j = it.id!!,
                                subType = it.activo)
                            nodes.add(nodeFacultad)
                            facultades.put(nodeFacultad.id, nodeFacultad)
                            edges.add(LinkDTO(source = nodeRol.id, target = nodeFacultad.id, caption = "facultad"))
                        }
                    }
                }
            }

            return GraphPerfilRolFacultadDTO(nodes, edges)
        }
    }
}

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
 *  GraphUsuarioFacultadDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad.dto

import com.ailegorreta.commons.dtomappers.LinkDTO
import com.ailegorreta.commons.dtomappers.NodeDTO
import java.util.ArrayList

/**
 * Data class to generate the User Permits graph.
 *
 * note: This graph does NO correspond to the Neo4j model. It is a graph
 *       that includes just Users and Permits
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 */
data class GraphUsuarioFacultadDTO(val nodes: List<NodeDTO>,
                                   val edges: List<LinkDTO>) {

    companion object {
        fun mapFromEntity(facultadesDTO : Collection<FacultadDTO>, nombreUsuario: String?) : GraphUsuarioFacultadDTO {
            val nodes = ArrayList<NodeDTO>()
            val edges = ArrayList<LinkDTO>()
            var nodeId = 1;
            val usuarioNodeId = nodeId

            if (nombreUsuario == null)
                return GraphUsuarioFacultadDTO(nodes, edges)

            val nodeUsuario = NodeDTO(caption = nombreUsuario, type = "usuario",
                subType = true,
                id= nodeId, idNeo4j = nodeId.toLong() /* not need the id from Neoj */)

            nodes.add(nodeUsuario)
            facultadesDTO.forEach {
                nodeId++
                val nodeFacultad = NodeDTO(caption = it.nombre, type = "facultad",
                    subType = it.activo,
                    id= nodeId, idNeo4j = it.id!!)

                nodes.add(nodeFacultad)
                edges.add(LinkDTO(source =  usuarioNodeId, target = nodeId, caption = "permiso"))
            }

            return GraphUsuarioFacultadDTO(nodes, edges)
        }
    }
}

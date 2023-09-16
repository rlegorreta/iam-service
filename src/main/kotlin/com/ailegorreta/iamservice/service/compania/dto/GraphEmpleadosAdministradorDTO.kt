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
 *  GraphEmpleadosAdministradorDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import java.util.*

import com.ailegorreta.commons.dtomappers.NodeDTO
import com.ailegorreta.commons.dtomappers.LinkDTO

/**
 * Data class to generate the Employees that an Administrator can have
 * access graph. For d3.js
 *
 * @author rlh
 * @project : iam-service
 * @date June 2023
 */
data class GraphEmpleadosAdministradorDTO(val nodes: List<NodeDTO>,
                                          val edges: List<LinkDTO>) {

    companion object {
        fun mapFromEntity(usuarios : Collection<UsuarioDTO>?) : GraphEmpleadosAdministradorDTO {
            val nodes = ArrayList<NodeDTO>()
            val edges = ArrayList<LinkDTO>()
            val companies = HashMap<Long, NodeDTO>()
            val groups = HashMap<Long, NodeDTO>()
            var nodeId = 0;

            if (usuarios == null)
                return GraphEmpleadosAdministradorDTO(nodes, edges)

            usuarios.forEach {
                nodeId++;
                val nodeUsuario = NodeDTO(caption = it.nombreUsuario, type = "usuario",
                    id = nodeId, idNeo4j = it.id!!,
                    subType = it.activo,
                    subTypeVal = if (it.administrador) 1 else 0 )

                nodes.add(nodeUsuario)
                it.companias.forEach {		// companies where the Employee works
                    val company = companies.get(it.id)
                    var nodeCompania:NodeDTO

                    if (company != null)
                        nodeCompania = company
                    else {
                        nodeId++
                        nodeCompania = 	NodeDTO(caption = it.nombre, type = "compania",
                            id = nodeId, idNeo4j = it.id!!)
                        nodes.add(nodeCompania)
                        companies.put(nodeCompania.idNeo4j, nodeCompania)
                        if (it.subsidiarias != null)
                            it.subsidiarias!!.forEach { // links to subsidiarias
                                val subsidiaria = companies.get(it.id)
                                var nodeSubsidiaria: NodeDTO

                                if (subsidiaria != null)
                                    nodeSubsidiaria = subsidiaria
                                else {
                                    nodeId++
                                    nodeSubsidiaria = NodeDTO(caption = it.nombre, type = "compania",
                                        id = nodeId, idNeo4j = it.id!!)
                                    nodes.add(nodeSubsidiaria)
                                    companies.put(nodeSubsidiaria.idNeo4j, nodeSubsidiaria)
                                }
                                edges.add(LinkDTO(source = nodeSubsidiaria.id, target = nodeCompania.id, caption = "subsidiaria"))
                            }
                    }
                    edges.add(LinkDTO(source =  nodeUsuario.id, target = nodeCompania.id, caption = "trabaja"))
                }

                it.grupos.forEach { // companies where the Administrator belongs to a group
                    val grupo = groups.get(it.id)
                    val nodeGrupo: NodeDTO

                    if (grupo != null)
                        nodeGrupo = grupo
                    else {
                        nodeId++;
                        nodeGrupo = NodeDTO(caption = it.nombre, type = "grupo",
                            id = nodeId, idNeo4j = it.id!!, subType = it.activo)
                        nodes.add(nodeGrupo)
                        groups.put(nodeGrupo.idNeo4j, nodeGrupo)
                    }
                    edges.add(LinkDTO(source =  nodeUsuario.id, target = nodeGrupo.id, caption = "miembro"))
                    it.permiteCompanias.forEach {
                        val company = companies.get(it.id)
                        var nodeCompania:NodeDTO

                        if (company != null)
                            nodeCompania = company
                        else {
                            nodeId++
                            nodeCompania = 	NodeDTO(caption = it.nombre, type = "compania",
                                id = nodeId, idNeo4j = it.id!!)
                            nodes.add(nodeCompania)
                            companies.put(nodeCompania.idNeo4j, nodeCompania)
                        }
                        edges.add(LinkDTO(source =  nodeGrupo.id, target = nodeCompania.id, caption = "permite"))
                    }
                    it.noPermiteCompanias.forEach {
                        val company = companies.get(it.id)
                        var nodeCompania:NodeDTO

                        if (company != null)
                            nodeCompania = company
                        else {
                            nodeId++
                            nodeCompania = 	NodeDTO(caption = it.nombre, type = "compania",
                                id = nodeId, idNeo4j = it.id!!)
                            nodes.add(nodeCompania)
                            companies.put(nodeCompania.idNeo4j, nodeCompania)
                        }
                        edges.add(LinkDTO(source =  nodeGrupo.id, target = nodeCompania.id, caption = "no permite"))
                    }
                    it.permiteSinHerencia.forEach {
                        val company = companies.get(it.id)
                        var nodeCompania:NodeDTO

                        if (company != null)
                            nodeCompania = company
                        else {
                            nodeId++
                            nodeCompania = 	NodeDTO(caption = it.nombre, type = "compania",
                                id = nodeId, idNeo4j = it.id!!)
                            nodes.add(nodeCompania)
                            companies.put(nodeCompania.idNeo4j, nodeCompania)
                        }
                        edges.add(LinkDTO(source =  nodeGrupo.id, target = nodeCompania.id, caption = "permite sin herencia"))
                    }
                }
            }

            return GraphEmpleadosAdministradorDTO(nodes, edges)
        }
    }
}


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
 *  GraphCompaniasAdministradorDTO.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.compania.dto

import java.util.*

import com.ailegorreta.commons.dtomappers.NodeDTO
import com.ailegorreta.commons.dtomappers.LinkDTO

/**
 * Data class to generate the Companies that an Administrator can have
 * access graph for d2.js.
 *
 * @author rlh
 * @project : iam-service
 * @date June 2023
 */
data class GraphCompaniasAdministradorDTO(val nodes: List<NodeDTO>,
                                          val edges: List<LinkDTO>) {

    companion object {
        fun mapFromEntity(companias : Collection<CompaniaDTO>?) : GraphCompaniasAdministradorDTO {
            val nodes = ArrayList<NodeDTO>()
            val edges = ArrayList<LinkDTO>()
            val companies = HashMap<Long, NodeDTO>()
            var nodeId = 0;

            if (companias == null)
                return GraphCompaniasAdministradorDTO(nodes, edges)

            companias.forEach {
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
                if (it.subsidiarias != null)
                    it.subsidiarias!!.forEach { // links to subsidiarias (cannot be repeated)
                        val subsidiary = companies.get(it.id)
                        var nodeSubsidiaria:NodeDTO

                        if (subsidiary != null)
                            nodeSubsidiaria = subsidiary
                        else {
                            nodeId++
                            nodeSubsidiaria = 	NodeDTO(caption = it.nombre, type = "compania",
                                id = nodeId, idNeo4j = it.id!!)
                            nodes.add(nodeSubsidiaria)
                            companies.put(nodeSubsidiaria.idNeo4j, nodeSubsidiaria)
                        }
                        edges.add(LinkDTO(source =  nodeSubsidiaria.id, target = nodeCompania.id, caption = "subsidiaria"))
                    }
            }

            return GraphCompaniasAdministradorDTO(nodes, edges)
        }
    }
}

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
 *  GrupoServiceTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */

package com.ailegorreta.iamservice.service.compania

import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.service.AbstractServiceTest
import com.ailegorreta.iamservice.service.compania.dto.CompaniaDTO
import com.ailegorreta.iamservice.service.compania.dto.GrupoDTO
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.util.*
import java.util.function.Consumer

/**
 * Test for the GrupoService.
 *
 * @author rlh
 * @project iam-service
 * @date September 2023
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTestContainers
/* ^ This is a custom annotation to load the containers */
@ExtendWith(MockitoExtension::class)
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
/* ^ this is because: https://blog.mimacom.com/embeddedkafka-kafka-auto-configure-springboottest-bootstrapserversproperty/ */
@ActiveProfiles("integration-tests")
class GrupoServiceTests: AbstractServiceTest() {

    @Test
    fun `Add a new group`(@Autowired companiaService: CompaniaService,
                          @Autowired grupoService: GrupoService) {
        val nombre = "new Group"
        val grupoDTO = GrupoDTO(null, nombre, true, "TEST")
        val corp = companiaService.findByNombre("ACME SA de CV")

        var cias: ArrayList<CompaniaDTO> = ArrayList()

        assertThat(corp).isPresent
        cias.add(corp.get())
        grupoDTO.permiteCompanias = cias

        // permiteCompanias and PermitSinHerencia are the same. Keep them empty
        assertThat(grupoService.newGroup(grupoDTO, "adminACME"))
                               .satisfies(Consumer{ grupo -> Assertions.assertThat(grupo?.nombre).isEqualTo(nombre) })
    }

    @Test
    fun `Update a new group`(@Autowired companiaService: CompaniaService,
                             @Autowired grupoService: GrupoService) {
        var nombre = "Admin AI/ML"
        val group = grupoService.findByNombre(nombre)
        val company = companiaService.findByNombre("ACME SA de CV")

        assertThat(group).isPresent
        assertThat(company).isPresent

        group.get().permiteCompanias = arrayListOf()
        group.get().permiteSinHerencia = arrayListOf(company.get())

        // Erase companies in permit companies, erase all previous companies and add company without inheritance
        assertThat(grupoService.updateGroup(group.get()))
                               .satisfies(Consumer{ gr -> assertThat(gr?.nombre).isEqualTo(nombre) })
    }

    @Test
    fun `Update a group link only`(@Autowired companiaService: CompaniaService,
                                   @Autowired grupoService: GrupoService) {
        var nombre = "Admin AI/ML"
        val group = grupoService.findByNombre(nombre)
        val company = companiaService.findByNombre("ACME SA de CV")

        assertThat(group).isPresent
        assertThat(company).isPresent

        assertThat(grupoService.updateLinksGrupo(grupoId = group.get().id!!,
                                                 usuarioModificacion = "TEST",
                                                 permiteSinHerencia = arrayListOf(company.get())))
                  .satisfies(Consumer{ gr -> assertThat(gr?.nombre).isEqualTo(nombre) })
    }

}

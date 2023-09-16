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
 *  CompaniaJsonTests.java
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.web;

import com.ailegorreta.iamservice.model.Negocio;
import com.ailegorreta.iamservice.service.compania.dto.CompaniaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example for a JsonTest for some Neo4j DTOs, many others can be added
 *
 * In this case we check that CompaniaDTO is serializable correctly
 *
 * @project iam-service
 * @author rlh
 * @date September 2023
 */
@JsonTest
@ContextConfiguration(classes = CompaniaJsonTests.class)
@ActiveProfiles("integration-tests")
public class CompaniaJsonTests {
    @Autowired
    public JacksonTester<CompaniaDTO> json;

    @Test
    void testSerialize() throws Exception {
        var companiaDTO = new CompaniaDTO(100L, "Compania TEST", true, Negocio.GOBIERNO,
                                        "TEST");
        var jsonContent = json.write(companiaDTO);

        assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                .isEqualTo(companiaDTO.getId().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("@.nombre")
                .isEqualTo(companiaDTO.getNombre().toString());
        assertThat(jsonContent).extractingJsonPathBooleanValue("@.padre")
                .isEqualTo(companiaDTO.getPadre());
        assertThat(jsonContent).extractingJsonPathStringValue("@.negocio")
                .isEqualTo(companiaDTO.getNegocio().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.usuarioModificacion")
                .isEqualTo(companiaDTO.getUsuarioModificacion());
    }

    @Test
    void testDeserialize() throws Exception {
        var companiaDTO = new CompaniaDTO(100L, "Compania TEST", true, Negocio.GOBIERNO,
                "TEST");
        var content = """
                {
                    "id": 
                    """ + "\"" + companiaDTO.getId() + "\"," + """
                    "nombre":
                    """ + "\"" + companiaDTO.getNombre() + "\"," + """              
                "padre": "true",
                "negocio": "GOBIERNO",
                "fechaModificacion" : 
                    """ + "\"" + companiaDTO.getFechaModificacion() + "\"," + """
                "usuarioModificacion" : 
                """ + "\"" + companiaDTO.getUsuarioModificacion() + "\"" + """
                }
                """;
        System.out.println(">>>" + content);
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(companiaDTO);
    }
}


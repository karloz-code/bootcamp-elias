package com.neurokore.bootcamp.elias.payment1.controller;

import com.neurokore.bootcamp.elias.payment1.dto.TipoPago1DTO;
import com.neurokore.bootcamp.elias.payment1.service.TipoPago1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Pruebas unitarias de {@link TipoPago1Controller#crearPago(TipoPago1DTO)}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TipoPago1Controller - crearPago")
class TipoPago1ControllerTest {

    private static final Long ID_PAGO = 42L;

    @Mock
    private TipoPago1 service;

    @InjectMocks
    private TipoPago1Controller sut;

    /**
     * Crea un pago de prueba con datos estables.
     *
     * @return {@link TipoPago1DTO} de prueba
     */
    private TipoPago1DTO createPagoDto() {
        TipoPago1DTO dto = new TipoPago1DTO();
        dto.setClaveRastreo("CLAVE-42");
        dto.setCuentaOrdenante("012345678901234567");
        dto.setCuentaBeneficiario("076543210987654321");
        dto.setReferenciaNumerica("1234567");
        dto.setMonto(new BigDecimal("100.50"));
        dto.setConceptoPago("Pago de prueba");
        return dto;
    }

    @Test
    @DisplayName("Debe devolver 201 con el pago creado cuando el servicio registra el pago")
    void givenPagoValido_whenCrearPago_thenRetorna201ConPagoCreado() {

        // 1. GIVEN
        TipoPago1DTO entrada = createPagoDto();
        TipoPago1DTO creado = createPagoDto();
        creado.setId(ID_PAGO);
        given(service.crearPago(entrada)).willReturn(creado);

        // 2. WHEN
        ResponseEntity<TipoPago1DTO> respuesta = sut.crearPago(entrada);

        // 3. THEN
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isEqualTo(creado);
        then(service).should().crearPago(entrada);
    }


    @Test
    @DisplayName("Debe devolver 201 con el pago creado cuando el servicio registra el pago")
    void givenPagoInValido_whenCrearPago_thenRetorna201ConPagoCreado() {

        // 1. GIVEN
        TipoPago1DTO entrada = createPagoDto();
        entrada.setClaveRastreo("");
        TipoPago1DTO creado = createPagoDto();
        creado.setId(ID_PAGO);
        given(service.crearPago(entrada)).willReturn(creado);

        // 2. WHEN
        ResponseEntity<TipoPago1DTO> respuesta = sut.crearPago(entrada);

        // 3. THEN
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isEqualTo(creado);
        then(service).should().crearPago(entrada);
    }

}

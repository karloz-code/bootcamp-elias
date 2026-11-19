package com.neurokore.bootcamp.elias.payment1.service;

import com.neurokore.bootcamp.elias.payment1.dto.TipoPago1DTO;
import com.neurokore.bootcamp.elias.payment1.dto.TipoPago1Request;
import com.neurokore.bootcamp.elias.payment1.entity.TipoPago1Entity;
import com.neurokore.bootcamp.elias.payment1.repository.TipoPago1Repository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Pruebas unitarias de {@link TipoPago1Service}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TipoPago1Service")
class TipoPago1ServiceTest {

    private static final Long ID = 42L;
    private static final Instant TS = Instant.parse("2026-01-01T00:00:00Z");

    @Mock
    private TipoPago1Repository repository;

    @InjectMocks
    private TipoPago1Service sut;

    @Captor
    private ArgumentCaptor<TipoPago1Entity> entityCaptor;

    private TipoPago1DTO createDto(Long id) {
        TipoPago1DTO dto = new TipoPago1DTO();
        dto.setId(id);
        dto.setClaveRastreo("CR100");
        dto.setCuentaOrdenante("CO100");
        dto.setCuentaBeneficiario("CB100");
        dto.setReferenciaNumerica("RN100");
        dto.setMonto(new BigDecimal("100.50"));
        dto.setTsLiquidacion(TS);
        dto.setConceptoPago("CP100");
        dto.setEstado("LIQUIDADO");
        return dto;
    }

    private TipoPago1Request createRequest(Long id) {
        return new TipoPago1Request(id, "CR100", "CO100", "CB100", "RN100",
                new BigDecimal("100.50"), TS, "CP100", "LIQUIDADO");
    }

    private TipoPago1Entity createEntity(Long id) {
        TipoPago1Entity entity = new TipoPago1Entity();
        entity.setId(id);
        entity.setClaveRastreo("CR100");
        entity.setCuentaOrdenante("CO100");
        entity.setCuentaBeneficiario("CB100");
        entity.setReferenciaNumerica("RN100");
        entity.setMonto(new BigDecimal("100.50"));
        entity.setTsLiquidacion(TS);
        entity.setConceptoPago("CP100");
        entity.setEstado("LIQUIDADO");
        return entity;
    }

    @Test
    @DisplayName("Debe guardar entidad sin id y devolver DTO con id cuando el DTO es válido")
    void givenValidDto_whenCrearPago_thenSavesEntityWithoutIdAndReturnsDto() {
        // 1. GIVEN
        given(repository.save(any(TipoPago1Entity.class))).willReturn(createEntity(ID));

        // 2. WHEN
        TipoPago1DTO result = sut.crearPago(createDto(7L));

        // 3. THEN
        then(repository).should().save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isNull();
        assertThat(entityCaptor.getValue()).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(createEntity(null));
        assertThat(result).isEqualTo(createDto(ID));
    }

    @Test
    @DisplayName("Debe propagar la excepción cuando el repositorio falla al guardar un DTO")
    void givenRepositoryFails_whenCrearPagoDto_thenPropagatesException() {
        // 1. GIVEN
        given(repository.save(any(TipoPago1Entity.class))).willThrow(new IllegalStateException("db"));
        TipoPago1DTO dto = createDto(null);

        // 2. WHEN / 3. THEN
        assertThatThrownBy(() -> sut.crearPago(dto))
                .isInstanceOf(IllegalStateException.class).hasMessage("db");
    }

    @Test
    @DisplayName("Debe guardar entidad sin id y devolver DTO con id cuando el request es válido")
    void givenValidRequest_whenCrearPago_thenSavesEntityWithoutIdAndReturnsDto() {
        // 1. GIVEN
        given(repository.save(any(TipoPago1Entity.class))).willReturn(createEntity(ID));

        // 2. WHEN
        TipoPago1DTO result = sut.crearPago(createRequest(7L));

        // 3. THEN
        then(repository).should().save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isNull();
        assertThat(entityCaptor.getValue()).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(createEntity(null));
        assertThat(result).isEqualTo(createDto(ID));
    }

    @Test
    @DisplayName("Debe devolver el pago cuando el id existe")
    void givenExistingId_whenLeerPago_thenReturnsDto() {
        // 1. GIVEN
        given(repository.findById(ID)).willReturn(Optional.of(createEntity(ID)));

        // 2. WHEN
        TipoPago1DTO result = sut.leerPago(ID);

        // 3. THEN
        assertThat(result).isEqualTo(createDto(ID));
        then(repository).should().findById(ID);
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el id no existe")
    void givenUnknownId_whenLeerPago_thenThrowsNoSuchElement() {
        // 1. GIVEN
        given(repository.findById(ID)).willReturn(Optional.empty());

        // 2. WHEN / 3. THEN
        assertThatThrownBy(() -> sut.leerPago(ID)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Debe devolver todos los pagos cuando existen registros")
    void givenStoredPayments_whenLeerTodos_thenReturnsAllDtos() {
        // 1. GIVEN
        given(repository.findAll()).willReturn(List.of(createEntity(ID), createEntity(100L)));

        // 2. WHEN
        List<TipoPago1DTO> result = sut.leerTodos();

        // 3. THEN
        assertThat(result).containsExactly(createDto(ID), createDto(100L));
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando no hay registros")
    void givenNoPayments_whenLeerTodos_thenReturnsEmptyList() {
        // 1. GIVEN
        given(repository.findAll()).willReturn(List.of());

        // 2. WHEN
        List<TipoPago1DTO> result = sut.leerTodos();

        // 3. THEN
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe actualizar todos los campos y conservar el id cuando el pago existe")
    void givenExistingId_whenActualizarPago_thenUpdatesFieldsAndReturnsDto() {
        // 1. GIVEN
        TipoPago1Entity existing = createEntity(ID);
        existing.setClaveRastreo("OLD");
        existing.setMonto(BigDecimal.ONE);
        given(repository.findById(ID)).willReturn(Optional.of(existing));
        given(repository.save(existing)).willReturn(existing);

        // 2. WHEN
        TipoPago1DTO result = sut.actualizarPago(ID, createDto(7L));

        // 3. THEN
        then(repository).should().save(entityCaptor.capture());
        assertThat(entityCaptor.getValue()).usingRecursiveComparison().isEqualTo(createEntity(ID));
        assertThat(result).isEqualTo(createDto(ID));
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException y no guardar cuando el pago no existe")
    void givenUnknownId_whenActualizarPago_thenThrowsAndDoesNotSave() {
        // 1. GIVEN
        given(repository.findById(ID)).willReturn(Optional.empty());
        TipoPago1DTO dto = createDto(null);

        // 2. WHEN / 3. THEN
        assertThatThrownBy(() -> sut.actualizarPago(ID, dto)).isInstanceOf(NoSuchElementException.class);
        then(repository).should(never()).save(any(TipoPago1Entity.class));
        verifyNoMoreInteractions(repository);
    }
}

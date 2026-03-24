package com.iprody.leonidm.paymentserviceapp.controller;

import com.iprody.leonidm.paymentserviceapp.AbstractPostgresIntegrationTest;
import com.iprody.leonidm.paymentserviceapp.async.AsyncSender;
import com.iprody.leonidm.paymentserviceapp.async.XPaymentAdapterRequestMessage;
import com.iprody.leonidm.paymentserviceapp.config.PaymentControllerTestConfig;
import com.iprody.leonidm.paymentserviceapp.dto.PaymentDto;
import com.iprody.leonidm.paymentserviceapp.dto.RequestCreatePaymentDto;
import com.iprody.leonidm.paymentserviceapp.dto.RequestUpdateNotePaymentDto;
import com.iprody.leonidm.paymentserviceapp.dto.RequestUpdatePaymentDto;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;
import com.iprody.leonidm.paymentserviceapp.persistence.repository.PaymentRepository;
import com.iprody.leonidm.paymentserviceapp.util.TestJwtFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.iprody.leonidm.paymentserviceapp.controller.PaymentController.*;
import static com.iprody.leonidm.paymentserviceapp.service.PaymentService.*;
import static com.iprody.leonidm.paymentserviceapp.util.RoleConstants.ADMIN_ROLE;
import static com.iprody.leonidm.paymentserviceapp.util.RoleConstants.READER_ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@Import(PaymentControllerTestConfig.class)
public class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {
    private static final String INVALID_ROLE = "user";
    private static final String TEST_USERNAME = "test-user";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AsyncSender<XPaymentAdapterRequestMessage> sender;

    static String[] accessRoleProvider() {
        return new String[]{READER_ROLE, ADMIN_ROLE};
    }

    @ParameterizedTest
    @MethodSource("accessRoleProvider")
    void shouldReturnListPayments_isOk(String role) throws Exception {
        mockMvc.perform(get(GET_PAYMENTS_API)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, role))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
                .andExpect(jsonPath("[?(@.guid=='00000000-0000-0000-0000-000000000003')]").exists());
    }

    @Test
    void shouldReturnListPayments_isForbidden() throws Exception {
        mockMvc.perform(get(GET_PAYMENTS_API)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, INVALID_ROLE))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("accessRoleProvider")
    void shouldReturnPagePayments_isOk(String role) throws Exception {
        mockMvc.perform(get(GET_PAGE_PAYMENTS_BY_FILTER_API)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, role))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000003')]").exists());
    }

    @Test
    void shouldReturnPagePayments_isForbidden() throws Exception {
        mockMvc.perform(get(GET_PAGE_PAYMENTS_BY_FILTER_API)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, INVALID_ROLE))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("accessRoleProvider")
    void shouldReturnPaymentById_isOk(String role) throws Exception {
        UUID guid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(get(GET_PAYMENT_API, guid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, role))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(guid.toString()))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void shouldReturnPaymentById_isForbidden() throws Exception {
        UUID guid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(get(GET_PAYMENT_API, guid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, INVALID_ROLE))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404ForNonExistentPayment() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(get(GET_PAYMENT_API, nonexistentId)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, READER_ROLE))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(PAYMENT_NOT_FOUND))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value(FIND_BY_ID_PAYMENT_OPERATION))
                .andExpect(jsonPath("$.guid").value(nonexistentId.toString()));
    }

    @Test
    void shouldCreatePayment_isOk() throws Exception {
        RequestCreatePaymentDto dto = new RequestCreatePaymentDto(new BigDecimal("123.45"), UUID.randomUUID(), "EUR", PaymentStatus.PENDING);
        String json = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(post(POST_CREATE_PAYMENT_API)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(created.guid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
    }

    @Test
    void shouldCreatePayment_isForbidden() throws Exception {
        RequestCreatePaymentDto dto = new RequestCreatePaymentDto(new BigDecimal("123.45"), UUID.randomUUID(), "EUR", PaymentStatus.PENDING);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post(POST_CREATE_PAYMENT_API)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, READER_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdatePayment_isOk() throws Exception {
        UUID guid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID inquiryRefId = UUID.fromString("10000000-0000-0000-0000-000000000002");
        RequestUpdatePaymentDto dto = new RequestUpdatePaymentDto(new BigDecimal("50.00"), "EUR", inquiryRefId, PaymentStatus.APPROVED, "Test payment 2 update", Instant.parse("2025-01-02T10:00:00Z"));
        String json = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(put(PUT_UPDATE_PAYMENT_API, guid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(created.guid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("50.00");
        assertThat(saved.get().getInquiryRefId()).isEqualTo(inquiryRefId);
        assertThat(saved.get().getStatus().toString()).isEqualTo(PaymentStatus.APPROVED.toString());
        assertThat(saved.get().getNote()).isEqualTo("Test payment 2 update");
    }

    @Test
    void shouldUpdatePayment_isNotFound() throws Exception {
        UUID nonExistUuid = UUID.randomUUID();
        UUID inquiryRefId = UUID.fromString("10000000-0000-0000-0000-000000000002");
        RequestUpdatePaymentDto dto = new RequestUpdatePaymentDto(new BigDecimal("50.00"), "EUR", inquiryRefId, PaymentStatus.APPROVED, "Test payment 2 update", Instant.parse("2025-01-02T10:00:00Z"));
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put(PUT_UPDATE_PAYMENT_API, nonExistUuid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(PAYMENT_NOT_FOUND))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value(UPDATE_PAYMENT_OPERATION))
                .andExpect(jsonPath("$.guid").value(nonExistUuid.toString()));
        ;
    }

    @Test
    void shouldUpdatePayment_isForbidden() throws Exception {
        UUID guid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID inquiryRefId = UUID.fromString("10000000-0000-0000-0000-000000000002");
        RequestUpdatePaymentDto dto = new RequestUpdatePaymentDto(new BigDecimal("50.00"), "EUR", inquiryRefId, PaymentStatus.APPROVED, "Test payment 2 update", Instant.parse("2025-01-02T10:00:00Z"));
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put(PUT_UPDATE_PAYMENT_API, guid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, READER_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeletePayment_isOk() throws Exception {
        UUID inquiryRefId = UUID.fromString("10000000-0000-0000-0000-000000000002");
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("50.00"));
        payment.setCurrency("EUR");
        payment.setInquiryRefId(inquiryRefId);
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setNote("Test payment 2 delete");

        Payment savedPayment = paymentRepository.save(payment);
        UUID savedGuid = savedPayment.getGuid();

        mockMvc.perform(delete(DELETE_DELETE_PAYMENT_API, savedGuid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<Payment> saved = paymentRepository.findById(savedGuid);
        assertThat(saved).isEmpty();
    }

    @Test
    void shouldDeletePayment_isNotFound() throws Exception {
        UUID nonExistUuid = UUID.randomUUID();

        mockMvc.perform(delete(DELETE_DELETE_PAYMENT_API, nonExistUuid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(PAYMENT_NOT_FOUND))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value(DELETE_PAYMENT_OPERATION))
                .andExpect(jsonPath("$.guid").value(nonExistUuid.toString()));
        ;
    }

    @Test
    void shouldDeletePayment_isForbidden() throws Exception {
        UUID guid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(delete(DELETE_DELETE_PAYMENT_API, guid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, READER_ROLE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateNotePayment_isOk() throws Exception {
        UUID guid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final String newNote = "new note";
        RequestUpdateNotePaymentDto dto = new RequestUpdateNotePaymentDto(newNote);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch(PATCH_UPDATE_NOTE_PAYMENT_API, guid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Optional<Payment> saved = paymentRepository.findById(guid);
        assertThat(saved).isPresent();
        assertThat(saved.get().getNote()).isEqualTo(newNote);
    }

    @Test
    void shouldUpdateNotePayment_isNotFound() throws Exception {
        UUID nonExistUuid = UUID.randomUUID();
        final String newNote = "new note";
        RequestUpdateNotePaymentDto dto = new RequestUpdateNotePaymentDto(newNote);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch(PATCH_UPDATE_NOTE_PAYMENT_API, nonExistUuid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, ADMIN_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(PAYMENT_NOT_FOUND))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value(UPDATE_NOTE_PAYMENT_OPERATION))
                .andExpect(jsonPath("$.guid").value(nonExistUuid.toString()));
        ;
    }

    @Test
    void shouldUpdateNotePayment_isForbidden() throws Exception {
        UUID guid = UUID.randomUUID();
        final String newNote = "new note";
        RequestUpdateNotePaymentDto dto = new RequestUpdateNotePaymentDto(newNote);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch(PATCH_UPDATE_NOTE_PAYMENT_API, guid)
                        .with(TestJwtFactory.jwtWithRole(TEST_USERNAME, READER_ROLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}

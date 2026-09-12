package com.aether.ms_cloudinary.cloudinary;

import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.services.CloudinaryService;
import com.aether.ms_cloudinary.shared.enums.EmployeeStatusEnum;
import com.aether.ms_cloudinary.shared.persistence.postgres.entities.EmployeeEntity;
import com.aether.ms_cloudinary.shared.persistence.postgres.repositories.EmployeeRepository;
import com.aether.ms_cloudinary.shared.security.jwt.JwtTokenProvider;
import com.aether.ms_cloudinary.shared.services.MessageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("CloudinaryController Tests")
class CloudinaryControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;
  @MockitoBean
  private MessageService messageService;
  @MockitoBean
  private CloudinaryService cloudinaryService;

  private final List<Integer> ids = new ArrayList<>();

  private EmployeeEntity employeeActive;

  @BeforeEach
  void setup() {

    employeeActive = new EmployeeEntity(
        "12345678902",
        "Teste",
        "test@test.com",
        "senha123",
        "11977394517",
        EmployeeStatusEnum.ACTIVE,
        List.of()
    );

    employeeActive = employeeRepository.save(employeeActive);

    ids.add(employeeActive.getId());
  }

  @AfterEach
  void cleanup() {
    employeeRepository.deleteAllByIdInBatch(ids);
    ids.clear();
  }

  private String generateJwt(EmployeeEntity employee) {
    return jwtTokenProvider
        .createAccessToken(
            employee.getEmail(),
            List.of()
        )
        .accessToken();
  }

  @Test
  @DisplayName("Should return 200 and upload signature")
  void generateUploadSignatureSuccess() throws Exception {

    String token = generateJwt(employeeActive);

    var output = new GenerateUploadSignatureOutputDTO(
        "signature-test",
        1788097873L,
        "api-key-test",
        "cloud-name-test",
        "images",
        "image"
    );

    when(cloudinaryService.generateUploadSignature(any()))
        .thenReturn(output);

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.signature")
                .value("signature-test")
        )
        .andExpect(
            jsonPath("$.timestamp")
                .value(1788097873L)
        )
        .andExpect(
            jsonPath("$.apiKey")
                .value("api-key-test")
        )
        .andExpect(
            jsonPath("$.cloudName")
                .value("cloud-name-test")
        )
        .andExpect(
            jsonPath("$.cloudinaryFolder")
                .value("images")
        )
        .andExpect(
            jsonPath("$.cloudinaryResourceType")
                .value("image")
        );

    verify(cloudinaryService)
        .generateUploadSignature(any());
  }

  @Test
  @DisplayName("Should return 200 when file type is XLSX")
  void generateUploadSignatureXlsx() throws Exception {

    String token = generateJwt(employeeActive);

    var output = new GenerateUploadSignatureOutputDTO(
        "signature-xlsx",
        1788097873L,
        "api-key-test",
        "cloud-name-test",
        "spreadsheets",
        "raw"
    );

    when(cloudinaryService.generateUploadSignature(any()))
        .thenReturn(output);

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "XLSX")
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.signature")
                .value("signature-xlsx")
        )
        .andExpect(
            jsonPath("$.cloudinaryFolder")
                .value("spreadsheets")
        )
        .andExpect(
            jsonPath("$.cloudinaryResourceType")
                .value("raw")
        );

    verify(cloudinaryService)
        .generateUploadSignature(any());
  }

  @Test
  @DisplayName("Should return 400 when file type is invalid")
  void generateUploadSignatureInvalidFileType() throws Exception {

    String token = generateJwt(employeeActive);

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAG")
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isBadRequest());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 400 when file type is not provided")
  void generateUploadSignatureWithoutFileType() throws Exception {

    String token = generateJwt(employeeActive);

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isBadRequest());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 401 when JWT is not provided")
  void generateUploadSignatureWithoutAuthentication()
      throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 401 when JWT is invalid")
  void generateUploadSignatureWithInvalidToken()
      throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header(
                    "Authorization",
                    "Bearer token-invalido"
                )
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }
}
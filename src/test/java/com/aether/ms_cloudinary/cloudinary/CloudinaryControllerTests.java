package com.aether.ms_cloudinary.cloudinary;

import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.services.CloudinaryService;
import com.aether.ms_cloudinary.shared.services.MessageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

  @Value("${security.internal-authentication-key}")
  private String authenticationKey;

  @MockitoBean
  private MessageService messageService;
  @MockitoBean
  private CloudinaryService cloudinaryService;

  @Test
  @DisplayName("Should return 200 and upload signature")
  void generateUploadSignatureSuccess() throws Exception {

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
                .header("authentication-key", authenticationKey)
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
                .header("authentication-key", authenticationKey)
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

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAG")
                .header("authentication-key", authenticationKey)
        )
        .andExpect(status().isBadRequest());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 400 when file type is not provided")
  void generateUploadSignatureWithoutFileType() throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .header("authentication-key", authenticationKey)
        )
        .andExpect(status().isBadRequest());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 401 when authentication key is not provided")
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
  @DisplayName("Should return 401 when authentication key is invalid")
  void generateUploadSignatureWithInvalidAuthenticationKey()
      throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header(
                    "authentication-key",
                    "chave-invalida"
                )
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }
}
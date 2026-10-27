package com.aether.ms_cloudinary.cloudinary;

import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.services.CloudinaryService;
import com.aether.ms_cloudinary.shared.services.MessageService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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

  @Value("${security.jwt.secret-key}")
  private String jwtSecretKey;

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

  @Test
  @DisplayName("Should return 200 when a valid JWT is provided without authentication key")
  void generateUploadSignatureWithValidJwt() throws Exception {

    when(cloudinaryService.generateUploadSignature(any()))
        .thenReturn(new GenerateUploadSignatureOutputDTO(
            "signature-jwt",
            1788097873L,
            "api-key-test",
            "cloud-name-test",
            "images",
            "image"
        ));

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header("Authorization", "Bearer " + accessToken(jwtSecretKey, Instant.now().plusSeconds(3600), null))
        )
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.signature")
                .value("signature-jwt")
        );

    verify(cloudinaryService)
        .generateUploadSignature(any());
  }

  @Test
  @DisplayName("Should return 401 when JWT is expired")
  void generateUploadSignatureWithExpiredJwt() throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header("Authorization", "Bearer " + accessToken(jwtSecretKey, Instant.now().minusSeconds(60), null))
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 401 when JWT is signed with another secret")
  void generateUploadSignatureWithJwtSignedByAnotherSecret() throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header("Authorization", "Bearer " + accessToken("another-secret", Instant.now().plusSeconds(3600), null))
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 401 when a refresh token is used as access token")
  void generateUploadSignatureWithRefreshToken() throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header("Authorization", "Bearer " + accessToken(jwtSecretKey, Instant.now().plusSeconds(3600), UUID.randomUUID().toString()))
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 401 when JWT is malformed")
  void generateUploadSignatureWithMalformedJwt() throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header("Authorization", "Bearer token-invalido")
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }

  @Test
  @DisplayName("Should return 401 when authentication key is invalid even with a valid JWT")
  void generateUploadSignatureWithInvalidAuthenticationKeyAndValidJwt() throws Exception {

    mockMvc.perform(
            get("/api/cloudinary/upload-signature")
                .param("fileType", "IMAGE")
                .header("authentication-key", "chave-invalida")
                .header("Authorization", "Bearer " + accessToken(jwtSecretKey, Instant.now().plusSeconds(3600), null))
        )
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(cloudinaryService);
  }

  // Mirrors how ms-auth signs its tokens.
  private String accessToken(String secretKey, Instant expiresAt, String tokenId) {
    String encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));

    return JWT.create()
        .withJWTId(tokenId)
        .withClaim("roles", List.of("ROLE_USER"))
        .withIssuedAt(Instant.now().minusSeconds(120))
        .withExpiresAt(expiresAt)
        .withSubject("user@aether.com")
        .sign(Algorithm.HMAC256(encodedSecretKey.getBytes(StandardCharsets.UTF_8)));
  }
}

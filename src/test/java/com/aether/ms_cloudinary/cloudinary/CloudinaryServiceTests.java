package com.aether.ms_cloudinary.cloudinary;

import com.aether.ms_cloudinary.cloudinary.dto.input.GenerateUploadSignatureInputDTO;
import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.services.CloudinaryService;
import com.aether.ms_cloudinary.shared.enums.StorageFileTypeEnum;
import com.aether.ms_cloudinary.shared.services.CloudinarySharedService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("CloudinaryService Tests")
public class CloudinaryServiceTests {

  @Mock
  private CloudinarySharedService cloudinarySharedService;

  @InjectMocks
  private CloudinaryService cloudinaryService;

  @Test
  @DisplayName("Should generate an upload signature")
  void generateUploadSignature() {

    GenerateUploadSignatureInputDTO input =
        new GenerateUploadSignatureInputDTO(
            StorageFileTypeEnum.IMAGE
        );

    CloudinarySharedService.CloudinarySignatureInfos infos =
        new CloudinarySharedService.CloudinarySignatureInfos(
            "signature-test",
            1788097873L,
            "api-key-test",
            "cloud-name-test",
            "images",
            "image"
        );

    GenerateUploadSignatureOutputDTO expectedOutput =
        new GenerateUploadSignatureOutputDTO(
            "signature-test",
            1788097873L,
            "api-key-test",
            "cloud-name-test",
            "images",
            "image"
        );

    when(
        cloudinarySharedService.generateUploadSignature(
            StorageFileTypeEnum.IMAGE
        )
    ).thenReturn(infos);

    GenerateUploadSignatureOutputDTO output =
        cloudinaryService.generateUploadSignature(input);

    assertNotNull(output);

    assertEquals(
        "signature-test",
        output.signature()
    );

    assertEquals(
        1788097873L,
        output.timestamp()
    );

    assertEquals(
        "api-key-test",
        output.apiKey()
    );

    assertEquals(
        "cloud-name-test",
        output.cloudName()
    );

    assertEquals(
        "images",
        output.cloudinaryFolder()
    );

    assertEquals(
        "image",
        output.cloudinaryResourceType()
    );

    verify(cloudinarySharedService)
        .generateUploadSignature(
            StorageFileTypeEnum.IMAGE
        );
  }
}
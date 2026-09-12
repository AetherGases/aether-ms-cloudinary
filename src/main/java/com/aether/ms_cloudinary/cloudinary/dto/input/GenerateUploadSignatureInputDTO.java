package com.aether.ms_cloudinary.cloudinary.dto.input;

import com.aether.ms_cloudinary.shared.enums.StorageFileTypeEnum;

public record GenerateUploadSignatureInputDTO(
    StorageFileTypeEnum fileType
) {
}

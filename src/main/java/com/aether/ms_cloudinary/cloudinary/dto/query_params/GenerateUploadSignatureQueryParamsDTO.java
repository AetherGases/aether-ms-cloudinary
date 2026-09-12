package com.aether.ms_cloudinary.cloudinary.dto.query_params;

import com.aether.ms_cloudinary.shared.enums.StorageFileTypeEnum;
import jakarta.validation.constraints.NotNull;

public record GenerateUploadSignatureQueryParamsDTO(
    @NotNull(message = "{validation.file-type.required}")
    StorageFileTypeEnum fileType
) {
}

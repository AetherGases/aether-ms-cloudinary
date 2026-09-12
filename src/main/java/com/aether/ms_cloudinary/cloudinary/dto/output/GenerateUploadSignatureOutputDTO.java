package com.aether.ms_cloudinary.cloudinary.dto.output;

public record GenerateUploadSignatureOutputDTO(
    String signature,
    long timestamp,
    String apiKey,
    String cloudName,
    String cloudinaryFolder,
    String cloudinaryResourceType
) {
}

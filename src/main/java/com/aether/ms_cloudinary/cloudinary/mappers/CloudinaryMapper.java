package com.aether.ms_cloudinary.cloudinary.mappers;

import com.aether.ms_cloudinary.cloudinary.dto.input.GenerateUploadSignatureInputDTO;
import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.dto.query_params.GenerateUploadSignatureQueryParamsDTO;
import com.aether.ms_cloudinary.shared.services.CloudinarySharedService;
public class CloudinaryMapper {
  public static GenerateUploadSignatureOutputDTO generateSignatureInfosToOutput(CloudinarySharedService.CloudinarySignatureInfos infos){
    return new GenerateUploadSignatureOutputDTO(
        infos.signature(),
        infos.timestamp(),
        infos.apiKey(),
        infos.cloudName(),
        infos.cloudinaryFolder(),
        infos.cloudinaryResourceType()
    );
  };

  public static GenerateUploadSignatureInputDTO generateUploadSignatureQueryParamsToInput(GenerateUploadSignatureQueryParamsDTO query){
    return new GenerateUploadSignatureInputDTO(
        query.fileType()
    );
  };
}

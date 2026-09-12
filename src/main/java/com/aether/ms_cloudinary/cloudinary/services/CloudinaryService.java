package com.aether.ms_cloudinary.cloudinary.services;

import com.aether.ms_cloudinary.cloudinary.dto.input.GenerateUploadSignatureInputDTO;
import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.mappers.CloudinaryMapper;
import com.aether.ms_cloudinary.shared.services.CloudinarySharedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("cloudinaryAPIService")
@RequiredArgsConstructor
public class CloudinaryService {
  private final CloudinarySharedService cloudinarySharedService;

  public GenerateUploadSignatureOutputDTO generateUploadSignature(GenerateUploadSignatureInputDTO input){
    CloudinarySharedService.CloudinarySignatureInfos infos = cloudinarySharedService.generateUploadSignature(input.fileType());

    return CloudinaryMapper.generateSignatureInfosToOutput(infos);
  }

}

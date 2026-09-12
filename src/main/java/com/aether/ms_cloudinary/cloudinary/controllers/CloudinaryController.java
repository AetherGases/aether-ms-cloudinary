package com.aether.ms_cloudinary.cloudinary.controllers;

import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.dto.query_params.GenerateUploadSignatureQueryParamsDTO;
import com.aether.ms_cloudinary.cloudinary.mappers.CloudinaryMapper;
import com.aether.ms_cloudinary.cloudinary.services.CloudinaryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cloudinary", description = "Módulo responsável pela geração de URLs assinadas do Cloudinary.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cloudinary")
public class CloudinaryController implements com.aether.ms_cloudinary.shared.docs.CloudinaryControllerDocs {
  private final CloudinaryService cloudinaryService;

  @Override
  @GetMapping("/upload-signature")
  public ResponseEntity<GenerateUploadSignatureOutputDTO> generateUploadSignature(
      @ModelAttribute
      @Valid
      GenerateUploadSignatureQueryParamsDTO query
  ){
    return new ResponseEntity<>(
        this.cloudinaryService.generateUploadSignature(CloudinaryMapper.generateUploadSignatureQueryParamsToInput(query)),
        HttpStatus.OK
    );
  }
}

package com.aether.ms_cloudinary.shared.docs;

import com.aether.ms_cloudinary.cloudinary.dto.output.GenerateUploadSignatureOutputDTO;
import com.aether.ms_cloudinary.cloudinary.dto.query_params.GenerateUploadSignatureQueryParamsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface CloudinaryControllerDocs {
  @Operation(
      summary = "Retorna a url assinada para um tipo de arquivo informado.",
      description = "Retornar uma url assinada para upload de arquivo na cloudinary.",
      tags = {"Cloudinary"},
      responses = {
          @ApiResponse(description = "Success", responseCode = "200", content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = GenerateUploadSignatureOutputDTO.class))
          )
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Unhautorized", responseCode = "401", content = @Content),
          @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
      }
  )
  ResponseEntity<GenerateUploadSignatureOutputDTO> generateUploadSignature(
      @Valid
      GenerateUploadSignatureQueryParamsDTO query
  );
}

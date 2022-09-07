package tech.velco.testtechnique.controller.dto;

import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents the request
 */
@Value
@Builder
public class Request {

    MultipartFile file;
    String sortKey;

}

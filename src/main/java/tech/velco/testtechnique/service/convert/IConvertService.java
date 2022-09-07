package tech.velco.testtechnique.service.convert;

import org.springframework.web.multipart.MultipartFile;
import tech.velco.testtechnique.model.ReferenceFormat;

/**
 * Contract for the ConvertService
 */
public interface IConvertService {

    /**
     * Converts the input file to a ReferenceFormat
     *
     * @param inputFile the input file
     * @param sortKey   the sort key
     * @return the converted input file
     */
    ReferenceFormat convert(final MultipartFile inputFile, final String sortKey);

}

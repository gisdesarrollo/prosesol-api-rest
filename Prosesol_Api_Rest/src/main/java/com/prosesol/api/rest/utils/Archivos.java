package com.prosesol.api.rest.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Luis Enrique Morales Soriano
 */

@Service
public class Archivos {

    /**
     * Método para encriptar archivos
     * @param sourceFile
     * @throws IOException
     * @return
     */

    public String encode(File sourceFile) throws IOException {

        byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(sourceFile));
        return new String(encoded, StandardCharsets.UTF_8);

    }
}

package com.alexandervov.service;

import com.alexandervov.entity.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

@Slf4j
@Service
@Transactional
public class ZipFileService {

    private byte[] defaultImg128Base64;
    private byte[] defaultImg512Base64;

    /**
     * Method for prepare temp zip file for uploaded application.
     * @param fileBytes uploaded zip file content
     * @param name file name
     * @return prepared zip file
     * @throws IOException could be occurred during processing uploaded file
     */
    public ZipFile prepareTempZipFile(final byte[] fileBytes, final String name) throws IOException {
        File tempFile = new File(name);

        try (OutputStream os = new FileOutputStream(tempFile)) {
            os.write(fileBytes);
            return new ZipFile(new File(name));
        }
    }

    /**
     * Mwethod for removing temp created zip file during upload.
     * @param name of temp zip file
     * @throws IOException could be occurred during file deleting process
     */
    public void removeTempZipFile(final String name) throws IOException {
        Files.delete(Path.of(name));
    }

    /**
     * Method use for db content initiation
     * @param zipFile with an application content
     * @param application prepared instance for initialization
     * @return completely initialized instance
     * @throws IOException could be occurred during reading file
     */
    public Application initApplicationFromZip(final ZipFile zipFile, final Application application) throws IOException {
        if (Objects.isNull(application)) {
            throw new ZipException("Required txt file isn't exist");
        }

        fillApplicationFromTextFile(zipFile, application);
        application.setPackageContent(Files.readAllBytes(Path.of(zipFile.getName())));

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        if (!application.getPictureName128().isEmpty()) {
            while (entries.hasMoreElements()) {
                var zipEntry = entries.nextElement();
                if (application.getPictureName128().equals(zipEntry.getName())) {
                    application.setPicture128Base64Bytes(
                        prepareBase64Bytes(zipFile.getInputStream(zipEntry)
                            .readAllBytes()));
                    break;
                }
            }
        }

        entries = zipFile.entries();
        if (!application.getPictureName512().isEmpty()) {
            while (entries.hasMoreElements()) {
                var zipEntry = entries.nextElement();
                if (application.getPictureName512().equals(zipEntry.getName())) {
                    application.setPicture512Base64Bytes(
                        prepareBase64Bytes(zipFile.getInputStream(zipEntry)
                            .readAllBytes()));
                    break;
                }
            }
        }

        checkAndSetImg(application);
        return application;
    }

    private void checkAndSetImg(final Application application) throws IOException {
        if (Objects.isNull(application.getPicture128Base64Bytes())) {
            application.setPicture128Base64Bytes(getDefaultImg128());
        }

        if (Objects.isNull(application.getPicture512Base64Bytes())) {
            application.setPicture512Base64Bytes(getDefaultImg512());
        }
    }

    private byte[] getDefaultImg128() throws IOException {
        if (defaultImg128Base64 == null) {
            defaultImg128Base64 = Base64.getEncoder()
                .encode(new ClassPathResource("img/default_img_128.png")
                    .getInputStream()
                    .readAllBytes());
        }
        return defaultImg128Base64;
    }

    private byte[] getDefaultImg512() throws IOException {
        if (defaultImg512Base64 == null) {
            defaultImg512Base64 = Base64.getEncoder()
                .encode(new ClassPathResource("img/default_img_512.png")
                    .getInputStream()
                    .readAllBytes());
        }
        return defaultImg512Base64;
    }

    private byte[] prepareBase64Bytes(final byte[] bytes) {
        final var encoder = Base64.getEncoder();
        return encoder.encode(bytes);
    }

    private void fillApplicationFromTextFile(final ZipFile zipFile, final Application application) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.getName().endsWith(".txt")) {
                var value = getValueFromTextFile(zipFile.getInputStream(zipEntry));
                application.setPackageName(value.get("package"));
                application.setPictureName128(value.get("picture_128"));
                application.setPictureName512(value.get("picture_512"));
                break;
            }
        }
    }

    private Map<String, String> getValueFromTextFile(final InputStream inputStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        var keyValueMap = new HashMap<String, String>();
        try {
            while (reader.ready()) {
                String line = reader.readLine();
                final var keyValueArray = line.split(":");
                if (keyValueArray.length > 1) {
                    keyValueMap.put(keyValueArray[0].trim(), keyValueArray[1].trim());
                }
            }
            return keyValueMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

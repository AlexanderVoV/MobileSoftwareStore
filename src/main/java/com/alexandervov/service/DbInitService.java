package com.alexandervov.service;

import com.alexandervov.entity.Application;
import com.alexandervov.entity.Category;
import com.alexandervov.repository.ApplicationRepository;
import com.alexandervov.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

@Slf4j
@Service
@AllArgsConstructor
public class DbInitService {

    private final CategoryRepository categoryRepository;
    private final ApplicationRepository applicationRepository;
    private final ZipFileService zipFileService;

    private final static String PREFIX_PATH = "init-data/";
    private final static String GOOGLE_MAPS_APP_ARCHIVE_PATH = PREFIX_PATH + "GoogleMaps.zip";
    private final static String GOOGLE_MAPS_APP_ARCHIVE_COPY = "GoogleMapsCopy.zip";
    private final static String WAZE_APP_ARCHIVE_PATH = PREFIX_PATH + "Waze.zip";
    private final static String WAZE_APP_ARCHIVE_COPY = "WazeCopy.zip";
    private final static String SOUNDCLOUD_APP_ARCHIVE_PATH = PREFIX_PATH + "SoundCloud.zip";
    private final static String SOUNDCLOUD_APP_ARCHIVE_COPY = "SoundCloudCopy.zip";
    private final static String UNSPLASH_APP_ARCHIVE_PATH = PREFIX_PATH + "Unsplash.zip";
    private final static String UNSPLASH_APP_ARCHIVE_COPY = "Unsplash.zip";

    @EventListener(ApplicationReadyEvent.class)
    public void initDb() throws IOException {
        if (categoryRepository.count() <= 0) {
            initCategories();
        }

        if (applicationRepository.count() <= 0) {
            initApplications();
        }
    }

    private void initCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Games"));
        categories.add(new Category("Tools"));
        categories.add(new Category("Images"));
        categories.add(new Category("Health"));
        categories.add(new Category("Lifestyle"));
        categories.add(new Category("Multimedia"));
        categories.add(new Category("Navigations"));
        categories.add(new Category("Productivity"));
        categoryRepository.saveAll(categories);
    }

    private void initApplications() throws IOException {
        List<Application> applications = new ArrayList<>();

        var archCopy = getPathAndZipFileOfCopy(GOOGLE_MAPS_APP_ARCHIVE_PATH, GOOGLE_MAPS_APP_ARCHIVE_COPY);
        applications.add(zipFileService.initApplicationFromZip(archCopy,
            new Application.Builder()
                .name("Google Maps")
                .category(categoryRepository.findByName("Navigations"))
                .description("Google Global Navigation App")
                .downloadCounter(290)
                .build())
        );
        zipFileService.removeTempZipFile(GOOGLE_MAPS_APP_ARCHIVE_COPY);

        archCopy = getPathAndZipFileOfCopy(WAZE_APP_ARCHIVE_PATH, WAZE_APP_ARCHIVE_COPY);
        applications.add(zipFileService.initApplicationFromZip(archCopy,
            new Application.Builder()
                .name("Waze")
                .category(categoryRepository.findByName("Navigations"))
                .description("Global Navigation App")
                .downloadCounter(295)
                .build()));
        zipFileService.removeTempZipFile(WAZE_APP_ARCHIVE_COPY);

        archCopy = getPathAndZipFileOfCopy(SOUNDCLOUD_APP_ARCHIVE_PATH, SOUNDCLOUD_APP_ARCHIVE_COPY);
        applications.add(zipFileService.initApplicationFromZip(archCopy,
            new Application.Builder()
                .name("SoundCloud")
                .category(categoryRepository.findByName("Multimedia"))
                .description("Musical App")
                .downloadCounter(310)
                .build()));
        archCopy.close();
        zipFileService.removeTempZipFile(SOUNDCLOUD_APP_ARCHIVE_COPY);

        archCopy = getPathAndZipFileOfCopy(UNSPLASH_APP_ARCHIVE_PATH, UNSPLASH_APP_ARCHIVE_COPY);
        applications.add(zipFileService.initApplicationFromZip(archCopy,
            new Application.Builder()
                .name("Unsplash")
                .category(categoryRepository.findByName("Images"))
                .description("App for getting nice images")
                .downloadCounter(300)
                .build()));
        archCopy.close();
        zipFileService.removeTempZipFile(UNSPLASH_APP_ARCHIVE_COPY);

        applicationRepository.saveAll(applications);
    }

    public ZipFile getPathAndZipFileOfCopy(String originalZipPath, String fileNameCopy) throws IOException {
        return zipFileService.prepareTempZipFile(new ClassPathResource(originalZipPath)
            .getInputStream().readAllBytes(), fileNameCopy);
    }
}

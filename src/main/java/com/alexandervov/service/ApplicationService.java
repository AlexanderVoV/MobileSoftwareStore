package com.alexandervov.service;

import com.alexandervov.dao.ApplicationDao;
import com.alexandervov.entity.Application;
import com.alexandervov.entity.Category;
import com.alexandervov.repository.ApplicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.alexandervov.service.Constants.QUANTITY_MAX_POPULAR_APPS;

@Service
@AllArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CategoryService categoryService;
    private final ZipFileService zipFileService;

    /**
     * Method for upload new application from zip file.
     *
     * @param name        of application
     * @param categoryId  of application
     * @param description of application
     * @param file        zip archive of application
     * @throws IOException can be occurred during processing with zip file
     */
    public void uploadApplication(final String name, final Integer categoryId,
                                  final String description, final MultipartFile file) throws IOException {
        final var tempZipFile = zipFileService.prepareTempZipFile(file.getBytes(), file.getOriginalFilename());
        final var application = zipFileService.initApplicationFromZip(tempZipFile, new Application.Builder()
            .name(name)
            .category(categoryService.getCategory(categoryId))
            .description(description)
            .build());

        applicationRepository.save(application);
        zipFileService.removeTempZipFile(file.getOriginalFilename());
    }

    /**
     * Get all applications dao.
     *
     * @return all applications dao
     */
    public List<ApplicationDao> getAllApplicationsDao() {
        return prepareApplicationsDao(applicationRepository.findAll());
    }

    /**
     * Get DESC sorted top applications.
     *
     * @return list applications dao
     */
    public Set<ApplicationDao> getSortedTopFiveApps() {
        final var result = new TreeSet<>(Comparator.comparingInt(ApplicationDao::getDownloadCounter)
            .thenComparing(ApplicationDao::getName).reversed());
        final var tempList = getAllApplicationsDao();

        if (tempList.size() <= QUANTITY_MAX_POPULAR_APPS) {
            result.addAll(tempList);
        } else {
            result.addAll(tempList.subList(0, QUANTITY_MAX_POPULAR_APPS));
        }

        return result;
    }

    /**
     * Get all application with the same category id.
     *
     * @param id category id
     * @return list of applications dao
     */
    public List<ApplicationDao> getApplicationByCategory(final int id) {
        return prepareApplicationsDao(applicationRepository.findApplicationsByCategoryId(id));
    }

    /**
     * Get list of categories by existing applications.
     *
     * @return categories list
     */
    public Set<Category> getCategoriesByExistingApplications() {
        final var applications = applicationRepository.findAll();
        return StreamSupport.stream(applications.spliterator(), false)
            .map(Application::getCategory)
            .collect(Collectors.toSet());
    }

    /**
     * Get application by id.
     *
     * @param id application
     * @return application
     */
    public Application getApplicationById(final int id) {
        return applicationRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("Application with id: " + id + " doesn't exist"));
    }

    /**
     * Get application dao by id.
     *
     * @param id application dao
     * @return application dao
     */
    public ApplicationDao getApplicationDaoById(final int id) {
        return prepareApplicationDao(getApplicationById(id));
    }

    /**
     * Check existing application by name.
     *
     * @param name application
     * @return boolean value if found an application than return true value
     */
    public boolean isExistApplicationByName(final String name) {
        final var optionalApplication = applicationRepository.findApplicationByName(name);
        return optionalApplication.isPresent();
    }

    /**
     * Method for getting from db application by name.
     *
     * @param name application name
     * @return application instance
     */
    @Transactional
    public Application downloadApplication(String name) {
        final var application = applicationRepository.findApplicationByName(name).orElseThrow();
        var downloadCounter = application.getDownloadCounter();
        application.setDownloadCounter(++downloadCounter);
        applicationRepository.save(application);
        return application;
    }

    private List<ApplicationDao> prepareApplicationsDao(final Iterable<Application> applications) {
        List<ApplicationDao> result = new ArrayList<>();
        applications.forEach(app -> result.add(prepareApplicationDao(app)));

        return result;
    }

    private ApplicationDao prepareApplicationDao(final Application application) {
        return ApplicationDao.builder()
            .id(application.getId())
            .name(application.getName())
            .description(application.getDescription())
            .packageName(application.getPackageName())
            .picture128Base64(new String(application.getPicture128Base64Bytes()))
            .picture512Base64(new String(application.getPicture512Base64Bytes()))
            .category(categoryService.getCategory(application.getCategory().getId()))
            .downloadCounter(application.getDownloadCounter())
            .build();
    }
}

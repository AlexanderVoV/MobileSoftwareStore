package com.alexandervov.controller;

import com.alexandervov.service.ApplicationService;
import com.alexandervov.service.CategoryService;
import com.alexandervov.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.alexandervov.service.Constants.APP_NAME_DUPLICATION_ERROR_MESSAGE;

@Slf4j
@Controller
@AllArgsConstructor
public class MainViewController {

    private final ApplicationService applicationService;
    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * @param modelMap   value for mapping values on page
     * @param categoryId getting list of applications by selected category
     * @return index page
     */
    @RequestMapping("/")
    public String getMain(final ModelMap modelMap,
                          @RequestParam(value = "categoryId", required = false) final Integer categoryId) {
        initModelMapByMainValues(modelMap);
        modelMap.put("categories",
            applicationService.getCategoriesByExistingApplications());

        if (!Objects.isNull(categoryId)) {
            modelMap.put("appsByCategory",
                applicationService.getApplicationByCategory(categoryId));
        }

        return "index";
    }

    /**
     * @param modelMap value for mapping values on page
     * @param id       used for getting application info
     * @return index application-details page
     */
    @RequestMapping("/application/{id}")
    public String applicationDetails(final ModelMap modelMap,
                                     @PathVariable final int id) {
        initModelMapByMainValues(modelMap);
        modelMap.put("app", applicationService.getApplicationDaoById(id));
        return "application-details";
    }

    /**
     * @param modelMap value for mapping values on page
     * @return index application-upload page
     */
    @RequestMapping("/application-upload")
    public String uploadApplication(final ModelMap modelMap) {
        initModelMapByMainValues(modelMap);
        modelMap.put("categories", categoryService.getAllCategories());
        return "application-upload";
    }

    /**
     * Method for upload application.
     *
     * @param name        of uploaded application
     * @param categoryId  of uploaded application
     * @param description of uploaded application
     * @param file        archive with apps txt and images
     * @param modelMap    value for mapping values on page
     * @return redirected on main page
     */
    @PostMapping(value = "/application/upload",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadApplication(@RequestParam final String name,
                                    @RequestParam final Integer categoryId,
                                    @RequestParam final String description,
                                    @RequestParam final MultipartFile file,
                                    final ModelMap modelMap) {
        String errorMessage = "";
        initModelMapByMainValues(modelMap);
        if (applicationService.isExistApplicationByName(name)) {
            modelMap.put("categories", categoryService.getAllCategories());
            modelMap.put("errorMessage", APP_NAME_DUPLICATION_ERROR_MESSAGE);
            return "application-upload";
        }

        try {
            applicationService
                .uploadApplication(name, categoryId, description, file);
            return "redirect:/";
        } catch (IOException e) {
            modelMap.put("categories", categoryService.getAllCategories());
            modelMap.put("errorMessage",
                "Upload error: " + errorMessage + e.getMessage());
            log.debug("Error: " + errorMessage + e.getMessage());
            return "application-upload";
        }
    }

    /**
     * Login page mapping.
     *
     * @return login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    private void initModelMapByMainValues(final ModelMap modelMap) {
        modelMap.put("isDeveloper", userService.isDeveloper());
        modelMap.put("userName", userService.getUserName());
        modelMap.put("applications", applicationService.getSortedTopFiveApps());
    }
}

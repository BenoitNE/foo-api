package com.fooapi;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setUp() {
        importedClasses = new ClassFileImporter()
                // Ne pas inclure les classes de test dans l'analyse de l'architecture de production
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example.fooapi"); // Package racine de votre application
    }

    @Test
    @DisplayName("Les services ne doivent résider que dans le package service")
    void services_should_reside_in_service_package() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Service.class)
                .or().haveSimpleNameEndingWith("Service")
                .or().haveSimpleNameEndingWith("ServiceImpl")
                .should().resideInAPackage("..service..")
                .as("Les services doivent résider dans un package '..service..'");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Les contrôleurs ne doivent résider que dans le package controller")
    void controllers_should_reside_in_controller_package() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .or().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..controller..")
                .as("Les contrôleurs doivent résider dans un package '..controller..'");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Les repositories ne doivent résider que dans le package repository")
    void repositories_should_reside_in_repository_package() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Repository.class)
                .or().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..repository..")
                .as("Les repositories doivent résider dans un package '..repository..'");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Les classes de service doivent avoir le suffixe 'Service' ou 'ServiceImpl'")
    void services_should_be_named_correctly() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Service.class)
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("ServiceImpl")
                .as("Les classes annotées avec @Service doivent se terminer par 'Service' ou 'ServiceImpl'");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Les classes de repository doivent avoir le suffixe 'Repository'")
    void repositories_should_be_named_correctly() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Repository.class)
                .should().haveSimpleNameEndingWith("Repository")
                .as("Les classes annotées avec @Repository doivent se terminer par 'Repository'");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Les classes de contrôleur doivent avoir le suffixe 'Controller'")
    void controllers_should_be_named_correctly() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should().haveSimpleNameEndingWith("Controller")
                .as("Les classes annotées avec @RestController doivent se terminer par 'Controller'");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Vérification de l'architecture en couches")
    void layered_architecture_should_be_respected() {
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Config").definedBy("..config..")
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .layer("DTO").definedBy("..dto..")
                .layer("Entity").definedBy("..entity..")
                .layer("Mapper").definedBy("..mapper..")
                .layer("Exception").definedBy("..exception..")

                .whereLayer("Controller").mayOnlyBeAccessedByLayers("Config") // Ex: SecurityConfig peut référencer des contrôleurs pour les permissions
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service", "Config") // Services peuvent s'appeler entre eux, ou être utilisés par la config (ex: UserDetailsService)
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("DTO").mayOnlyBeAccessedByLayers("Controller", "Service", "Mapper") // DTOs sont des objets de données, utilisables partout
                .whereLayer("Entity").mayOnlyBeAccessedByLayers("Service", "Repository", "Mapper") // Typiquement, les services manipulent des entités, les repos les persistent, les mappers les convertissent
                .whereLayer("Mapper").mayOnlyBeAccessedByLayers("Service", "Controller") // Les services et parfois les contrôleurs peuvent utiliser des mappers
                .whereLayer("Exception").mayOnlyBeAccessedByLayers("Controller", "Service", "Mapper"); // Les exceptions peuvent être lancées/utilisées partout

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Les entités ne doivent pas dépendre des services ou des contrôleurs")
    void entities_should_not_depend_on_services_or_controllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..entity..")
                .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..")
                .as("Les entités ne doivent pas dépendre des services ou des contrôleurs");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Les services ne doivent pas accéder directement à HttpServletRequest/Response")
    void services_should_not_access_servlet_api() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("jakarta.servlet..")
                .as("Les services ne devraient pas dépendre directement de l'API Servlet (jakarta.servlet)");
        rule.check(importedClasses);
    }
}

package com.example.visabreno;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@Tag("architecture")
class ArchitectureTests {

    private static final String ROOT_PACKAGE = "com.example.visabreno";
    private static final String DOMAIN_PACKAGE = ROOT_PACKAGE + ".domain..";
    private static final String HTTP_PACKAGE = ROOT_PACKAGE + ".adapter.http..";
    private static final String POSTGRES_PACKAGE = ROOT_PACKAGE + ".adapter.postgres..";
    private static final String CONFIGURATION_PACKAGE = ROOT_PACKAGE + ".configuration..";

    private static final JavaClasses APPLICATION_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT_PACKAGE);

    @Test
    void domainDependsOnlyOnItselfAndTheJavaStandardLibrary() {
        classes()
                .that().resideInAPackage(DOMAIN_PACKAGE)
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", DOMAIN_PACKAGE)
                .check(APPLICATION_CLASSES);
    }

    @Test
    void adapterDependenciesPointInwardTowardTheDomain() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy(DOMAIN_PACKAGE)
                .layer("HTTP adapter").definedBy(HTTP_PACKAGE)
                .layer("PostgreSQL adapter").definedBy(POSTGRES_PACKAGE)
                .layer("Configuration").definedBy(CONFIGURATION_PACKAGE)
                .whereLayer("Domain").mayOnlyBeAccessedByLayers(
                        "HTTP adapter",
                        "PostgreSQL adapter",
                        "Configuration"
                )
                .whereLayer("HTTP adapter").mayNotBeAccessedByAnyLayer()
                .whereLayer("PostgreSQL adapter").mayNotBeAccessedByAnyLayer()
                .whereLayer("Configuration").mayNotBeAccessedByAnyLayer()
                .check(APPLICATION_CLASSES);
    }

    @Test
    void adapterPackagesAreFreeOfCycles() {
        slices()
                .matching(ROOT_PACKAGE + ".adapter.(*)..")
                .should().beFreeOfCycles()
                .check(APPLICATION_CLASSES);
    }
}

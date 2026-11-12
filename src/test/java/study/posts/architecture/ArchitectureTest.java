package study.posts.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import study.posts.application.CreatePostUseCase;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "study.posts", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest
    static final ArchRule domainDoesNotDependOnOuterLayers = noClasses()
            .that().resideInAPackage("study.posts.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "study.posts.application..", "study.posts.adapter..");

    @ArchTest
    static final ArchRule applicationDoesNotDependOnAdapters = noClasses()
            .that().resideInAPackage("study.posts.application..")
            .should().dependOnClassesThat().resideInAPackage("study.posts.adapter..");

    @ArchTest
    static final ArchRule webDoesNotDependOnPersistence = noClasses()
            .that().resideInAPackage("study.posts.adapter.web..")
            .should().dependOnClassesThat().resideInAPackage("study.posts.adapter.persistence..")
            .because("the controller delegates post creation to the application use case");

    @ArchTest
    static final ArchRule webUsesOnlyInputPortFromApplication = noClasses()
            .that().resideInAPackage("study.posts.adapter.web..")
            .should().dependOnClassesThat(resideInAPackage("study.posts.application..")
                    .and(not(equivalentTo(CreatePostUseCase.class))))
            .because("the web adapter depends on the post creation input port");
}

package com.levislv.r2.generator.build.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.android.builder.model.SourceProvider
import com.levislv.r2.generator.build.gradle.R2Generator
import groovy.util.slurpersupport.GPathResult
import kotlin.io.FilesKt
import kotlin.text.StringsKt
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

/**
 * @author levislv
 * @email  levislv@levislv.com
 * @blog   https://blog.levislv.com/
 * @github https://github.com/levislv/
 */
class R2GeneratorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project.plugins.hasPlugin(AppPlugin.class)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            configureRGeneration(project, appExtension.applicationVariants)
        } else if (project.plugins.hasPlugin(LibraryPlugin.class)) {
            LibraryExtension libraryExtension = project.extensions.getByType(LibraryExtension)
            configureRGeneration(project, libraryExtension.libraryVariants)
        }
    }

    // Parse the variant's main manifest file in order to get the package id which is used to create
    // R.java in the right place.
    private String getPackageName(BaseVariant variant) {
        XmlSlurper slurper = new XmlSlurper(false, false)
        List<File> list = new ArrayList<>(variant.sourceSets.size())
        variant.sourceSets.forEach(new Consumer<SourceProvider>() {
            @Override
            void accept(SourceProvider sourceProvider) {
                list.add(sourceProvider.manifestFile)
            }
        })

        // According to the documentation, the earlier files in the list are meant to be overridden by the later ones.
        // So the first file in the sourceSets list should be main.
        GPathResult result = slurper.parse(list.get(0))
        return result.getProperty("@package").toString()
    }

    // 配置R2
    void configureRGeneration(Project project, DomainObjectSet<BaseVariant> variants) {
        variants.all { variant ->
            File outputDir = FilesKt.resolve(project.buildDir, "generated/source/r2/${variant.dirName}")

            String rPackage = getPackageName(variant)
            AtomicBoolean once = new AtomicBoolean()
            variant.outputs.all { output ->
                ProcessAndroidResources processResources = output.processResources

                // Though there might be multiple outputs, their R files are all the same. Thus, we only
                // need to configure the task once with the R.java input and action.
                if (once.compareAndSet(false, true)) {
                    File file
                    if (processResources instanceof GenerateLibraryRFileTask) {
                        file = ((GenerateLibraryRFileTask) processResources).textSymbolOutputFile
                    } else if (processResources instanceof LinkApplicationAndroidResourcesTask) {
                        file = ((LinkApplicationAndroidResourcesTask) processResources).textSymbolOutputFile
                    } else {
                        throw new RuntimeException("Minimum supported Android Gradle Plugin is 3.1.0")
                    }

                    ConfigurableFileCollection rFile = project.files(file).builtBy(processResources)
                    project.tasks.create("generate${StringsKt.capitalize(variant.name)}R2", R2Generator.class, { generator ->
                        generator.outputDir = outputDir
                        generator.RFile = rFile
                        generator.packageName = rPackage
                        generator.className = "R2"
                        variant.registerJavaGeneratingTask(generator, outputDir)
                    })
                }
            }
        }
    }
}

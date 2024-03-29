package com.levislv.r2.generator.build.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * @author levislv
 * @email  levislv@levislv.com
 * @blog   https://blog.levislv.com/
 * @github https://github.com/levislv/
 */
class R2Generator extends DefaultTask {
    private File outputDir
    private FileCollection rFile
    private String packageName
    private String className

    @OutputDirectory
    File getOutputDir() {
        return outputDir
    }

    void setOutputDir(File var1) {
        this.outputDir = var1
    }

    @InputFiles

    FileCollection getRFile() {
        return rFile
    }

    void setRFile(FileCollection var1) {
        this.rFile = var1
    }

    @Input

    String getPackageName() {
        return packageName
    }

    void setPackageName(String var1) {
        this.packageName = var1
    }

    @Input

    String getClassName() {
        return className
    }

    void setClassName(String var1) {
        this.className = var1
    }

    @SuppressWarnings("unused")
    @TaskAction
    void brewJava() {
        brewJava(rFile.singleFile, outputDir, packageName, className)
    }

    void brewJava(File rFile, File outputDir, String packageName, String className) {
        FinalRClassBuilder builder = new FinalRClassBuilder(packageName, className)
        new ResourceSymbolListReader(builder).readSymbolTable(rFile)
        builder.build().writeTo(outputDir)
    }
}

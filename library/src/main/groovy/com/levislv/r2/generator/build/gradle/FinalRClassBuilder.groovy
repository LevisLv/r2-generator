package com.levislv.r2.generator.build.gradle

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import kotlin.text.StringsKt

import javax.lang.model.element.Modifier

/**
 * @author levislv
 * @email  levislv@levislv.com
 * @blog   https://blog.levislv.com/
 * @github https://github.com/levislv/
 */
class FinalRClassBuilder {
    public static final String ANNOTATION_PACKAGE = 'androidx.annotation'
    public static final String[] SUPPORTED_TYPES = ['anim', 'array', 'attr', 'bool', 'color', 'dimen', 'drawable', 'id', 'integer', 'layout', 'menu', 'plurals', 'string', 'style', 'styleable']

    private String packageName
    private String className
    private Map<String, TypeSpec.Builder> resourceTypes = new LinkedHashMap<>()

    FinalRClassBuilder(String packageName, String className) {
        this.packageName = packageName
        this.className = className
    }

    JavaFile build() {
        TypeSpec.Builder result = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        for (int index = 0; index < SUPPORTED_TYPES.length; index++) {
            String resourceType = resourceTypes.get(SUPPORTED_TYPES[index])
            if (resourceType != null) {
                result.addType(resourceTypes.get(SUPPORTED_TYPES[index]).build())
            }
        }
        return JavaFile.builder(packageName, result.build())
                .addFileComment('Generated code from statistics-gradle-plugin. Do not modify!')
                .build()
    }

    void addResourceField(String type, String fieldName, String fieldValue) {
        if (!SUPPORTED_TYPES.contains(type)) {
            return
        }
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(Integer.TYPE, fieldName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(fieldValue)

        fieldSpecBuilder.addAnnotation(getSupportAnnotationClass(type))

        TypeSpec.Builder resourceType = resourceTypes.get(type)
        if (resourceType == null) {
            resourceType = TypeSpec.classBuilder(type).addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            resourceTypes.put(type, resourceType)
        }

        resourceType.addField(fieldSpecBuilder.build())
    }

    private ClassName getSupportAnnotationClass(String type) {
        return ClassName.get(ANNOTATION_PACKAGE, StringsKt.capitalize(type) + 'Res')
    }
}

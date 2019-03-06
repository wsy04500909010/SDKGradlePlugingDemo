package com.wsy.testgroovy

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginImpl implements Plugin<Project>{


    @Override
    void apply(Project project) {

        System.out.println("========================");
        System.out.println("use my first gradle plugin!");
        System.out.println("========================");

        //使用Transform实行遍历
//        def android = project.extensions.getByType(AppExtension)
//        registerTransform(android)

    }

//    def static registerTransform(BaseExtension android) {
//        AutoTransform transform = new AutoTransform()
//        android.registerTransform(transform)
//    }
}
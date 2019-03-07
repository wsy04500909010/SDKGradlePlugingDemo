package com.wsy.testgroovy.util

public class IMetisTextUtil{
    static String path2ClassName(String pathName) {
        pathName.replace(File.separator, ".").replace(".class", "")
    }
}
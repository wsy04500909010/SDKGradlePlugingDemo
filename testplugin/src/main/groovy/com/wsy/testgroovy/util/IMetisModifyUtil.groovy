package com.wsy.testgroovy.util

public class IMetisModifyUtil{
    static boolean needModify(String className){
        if (className.contains('R$') ||
                className.contains('R2$') ||
                className.endsWith('R') ||
                className.endsWith('R2') ||
                className.endsWith('BuildConfig')){
            return false
        }
        //TODO 考虑用户定义过滤规则
        return true
    }
}
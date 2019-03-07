package com.wsy.testgroovy.asm

import org.objectweb.asm.AnnotationVisitor

public class ActivityAnnotationVisitor extends AnnotationVisitor {
    public String desc;
    public String name;
    public String value;

    public ActivityAnnotationVisitor(int api, AnnotationVisitor av, String paramDesc) {
        super(api, av);
        this.desc = paramDesc;
    }

    public void visit(String paramName, Object paramValue) {
        this.name = paramName;
        this.value = paramValue.toString();
        println("visitAnnotation: name=" + name + " value=" + value)
    }

}
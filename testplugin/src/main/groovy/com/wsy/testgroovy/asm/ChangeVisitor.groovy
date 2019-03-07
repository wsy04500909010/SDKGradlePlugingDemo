package com.wsy.testgroovy.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

public class ChangeVisitor extends ClassVisitor {
    // 记录文件名
    private String owner;
    private ActivityAnnotationVisitor fileAnnotationVisitor = null;

    public ChangeVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
        println("construct")
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
        println("visit")
    }

    @Override
// 处理class文件的注解 在类文件上写上注解才走这里
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        println("visitAnnotation: desc=" + desc + " visible=" + visible);
        AnnotationVisitor annotationVisitor = super.visitAnnotation(desc, visible);
        if (desc != null && "Lcom/wsy/testsdk/inject/InjectBindClass;".equals(desc)) {//拥有特定注解的类才走这个流程,比对时候千万不要忘了 ";"
// 如果注解不是空，传递给ActivityAnnotationVisitor处理。
            fileAnnotationVisitor = new ActivityAnnotationVisitor(Opcodes.ASM5, annotationVisitor, desc);
            return fileAnnotationVisitor;
        }
        return annotationVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
// 获取到原始的MethodVisitor
        println("visitMethod------------------------" + name + "---------------" + desc)
        MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
// 如果文件的注解不为空，说明文件要进行修改。则创建RedefineAdvice，修改方法
        if (fileAnnotationVisitor != null) {

            println("enter Method flow")
            return new RedefineAdvice(mv, access, owner, name, desc);
        }
        return mv;
    }

}
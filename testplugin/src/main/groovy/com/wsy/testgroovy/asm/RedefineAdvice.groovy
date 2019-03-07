package com.wsy.testgroovy.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.Opcodes

public class RedefineAdvice extends AdviceAdapter {
    String owner = "";
    ActivityAnnotationVisitor activityAnnotationVisitor = null;

    protected RedefineAdvice(MethodVisitor mv, int access, String className, String name, String desc) {
        super(Opcodes.ASM5, mv, access, name, desc);
        owner = className;
    }

    //在方法上写上注解才走这里
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        println("visitAnnotation: desc=" + desc + " visible=" + visible);
        AnnotationVisitor annotationVisitor = super.visitAnnotation(desc, visible);
// 先判断方法上是否有注解，如果有注解，则使用ActivityAnnotationVisitor解析注解
        if (desc != null && "Lcom/wsy/testsdk/inject/InjectBind;".equals(desc)) {//拥有特定注解的类才走这个流程 ,比对时候千万不要忘了 ";"
            activityAnnotationVisitor = new ActivityAnnotationVisitor(Opcodes.ASM5, annotationVisitor, desc);
            return activityAnnotationVisitor;
        }
        return annotationVisitor;
    }

    @Override
// 修改方法入口，在方法执行前，插入字节码
    protected void onMethodEnter() {
        if (activityAnnotationVisitor == null) {
            return;
        }
        super.onMethodEnter();

        println("onMethodEnter")
//插入字节码，ALOAD
//        mv.visitVarInsn(ALOAD, 0);
//插入字节码INVOKESTATIC，调用ActivityTimeManger.onCreateStart().
// onCreate使用注解写入 原始的 先注释掉
//        mv.visitMethodInsn(INVOKESTATIC, "com/test/aop/tools/ActivityTimeManger",
//                activityAnnotationVisitor.value+"Start",
//                "(Landroid/app/Activity;)V");
        mv.visitMethodInsn(INVOKESTATIC, "com/wsy/testsdk/SDKTestTools",
                "AddFunction",
                "()V", false);
    }

//在方法执行结束前，插入字节码
    @Override
    protected void onMethodExit(int opcode) {
        if (activityAnnotationVisitor == null) {
            return;
        }
        super.onMethodExit(opcode);
//插入字节码，ALOAD
//        mv.visitVarInsn(ALOAD, 0);
//插入字节码INVOKESTATIC，调用ActivityTimeManger.onCreateEnd().
// onCreate使用注解写入
//        mv.visitMethodInsn(INVOKESTATIC, "com/test/aop/tools/ActivityTimeManger",
//                activityAnnotationVisitor.value+"End",
//                "(Landroid/app/Activity;)V");
    }
}
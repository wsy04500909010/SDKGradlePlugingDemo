package com.wsy.testgroovy

import com.android.annotations.NonNull
import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.tools.build.jetifier.core.utils.Log
import com.wsy.testgroovy.asm.ChangeVisitor
import groovy.io.FileType
import org.gradle.api.Nullable
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import com.wsy.testgroovy.util.IMetisModifyUtil
import com.wsy.testgroovy.util.IMetisTextUtil
import com.wsy.testgroovy.util.Logger
import org.objectweb.asm.Opcodes

public class MyTransform extends Transform{
    @Override
    String getName() {
        return "AutoTrack"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    public void transform(
            @NonNull Context context,
            @NonNull Collection<TransformInput> inputs,
            @NonNull Collection<TransformInput> referencedInputs,
            @Nullable TransformOutputProvider outputProvider,
            boolean isIncremental) throws IOException, TransformException, InterruptedException {
        //此处会遍历所有文件
        /**遍历输入文件*/
        inputs.each { TransformInput input ->
            /**
             * 遍历jar
             */
            input.jarInputs.each {
                JarInput jarInput ->
//                    Logger.info("jarInput----------------------${jarInput.file.absolutePath}")
                    String destName = jarInput.file.name
                    def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                    if (destName.endsWith(".jar")) {
                        destName = destName.substring(0, destName.length() - 4)
                    }
                    //jar包暂时不处理
                    File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
//                        def modfiedJar = modifyJarFile(jarInput.file,context.getTemporaryDir())
//                        if (modfiedJar == null){
//                            modfiedJar = jarInput.file
//                        }
//                        FileUtils.copyFile(modfiedJar,dest)
                    FileUtils.copyFile(jarInput.file, dest)
            }
            /**
             * 遍历目录
             */
            input.directoryInputs.each {
                DirectoryInput directoryInput ->
                    File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
//                    Logger.info("dest-------------------------->>>>>" + dest)
//                    Logger.info("context----------------------->>>>>" + context.temporaryDir)
                    File srcFile = directoryInput.file
//                    Logger.info("srcFile------------------------>>>>>>" + srcFile)
                    if (srcFile) {
                        HashMap<String, File> newClassFileMap = new HashMap<>()
                        srcFile.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                                //按规则遍历srcFile
                            File classFile ->
//                                Logger.info("dirInput----------------------" + classFile)

                                File newClassFile = modifyClass(srcFile, classFile, context.temporaryDir)
//                                Logger.info("AAAAAAA" + newClassFile)

                                if (newClassFile != null) {
                                    String key = classFile.absolutePath.replace(srcFile.absolutePath, "")
//                                    Logger.info("key---------------------->>>" + key)
                                    newClassFileMap.put(key, newClassFile)
                                }
                        }
                        FileUtils.copyDirectory(directoryInput.file, dest)//拷贝到transforms目录下
                        newClassFileMap.entrySet().each {
                            Map.Entry<String, File> en ->
                                File target = new File(dest.absolutePath + en.getKey())
//                                Logger.info("target------------------------------" + target)
                                if (target.exists()) {
                                    target.delete()
                                }
                                FileUtils.copyFile(en.getValue(), target)
                        }
                    }
            }

        }

    }


    private static File modifyClass(File dir, File classFile, File dest) {
        String className = IMetisTextUtil.path2ClassName(classFile.absolutePath.replace(dir.absolutePath + File.separator, ""))
        File newClassFile = null
        FileOutputStream outputStream = null
        try {
            if (IMetisModifyUtil.needModify(className)) {
                Logger.info("className2---------------------" + className)
                byte[] classFileBytes = IOUtils.toByteArray(new FileInputStream(classFile))
                byte[] newClassFileBytes = referHack(classFileBytes)
//                byte[] newClassFileBytes = IMetisModify.modifyClass(classFileBytes)
                if (newClassFileBytes) {
                    newClassFile = new File(dest, className.replace('.', '') + '.class')
                    if (newClassFile.exists()) {
                        newClassFile.delete()
                    }
                    newClassFile.createNewFile()
                    outputStream = new FileOutputStream(newClassFile)
                    outputStream.write(newClassFileBytes)
                }
            } else {
                return classFile
            }
        } catch (Exception e) {
            Logger.info("---------------modifyError-------------" + e.getMessage())
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close()
                }
            } catch (Exception e) {
            }
        }
        return newClassFile
    }

    private static byte[] referHack(byte[] inputStream) {
        try {
            ClassReader classReader = new ClassReader(inputStream);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor changeVisitor = new ChangeVisitor(classWriter);
            classReader.accept(changeVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

}
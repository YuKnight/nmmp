package com.nmmedit.apkprotect.aar.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import java.util.List;

public class AsmUtils {

    //生成NativeUtils类, 用于实现lib加载及类初始化
    public static byte[] genCfNativeUtil(@Nonnull String clsName,
                                         @Nonnull String libName,
                                         @Nonnull List<String> initMethodNames) {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                clsName, null, "java/lang/Object", null);

        // static{
        // System.loadLibrary("libname");
        // }
        final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "<clinit>", "()V",
                null, null);
        mv.visitLdcInsn(libName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "loadLibrary", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitEnd();

        //native void classInit0(int idx);
        for (String methodName : initMethodNames) {
            cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_NATIVE, methodName, "(I)V", null, null);
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

}

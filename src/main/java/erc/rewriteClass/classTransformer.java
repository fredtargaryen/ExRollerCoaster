package erc.rewriteClass;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sun.org.apache.bcel.internal.generic.ALOAD;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;

public class classTransformer implements IClassTransformer {

	// ���ϑΏۂ̃N���X�̊��S�C�����ł��B
    // ��q��Minecraft.jar���̓�ǉ������t�@�C����ΏۂƂ���ꍇ�̊ȈՂȎ擾���@���Љ�܂��B
    private static final String TARGET_CLASS_NAME = "net.minecraft.client.renderer.EntityRenderer";
     static int counter = 0;
 	@Override
 	public byte[] transform(String name, String transformedName, byte[] bytes) 
 	{
// 		FMLRelaunchLog.info("MFWTransformLog : Classname'%s'++'%s'", name, transformedName);
// 		if(FMLLaunchHandler.side().isServer())return bytes;
 		
 		if (TARGET_CLASS_NAME.equals(transformedName))
 		{
 			ClassReader cr = new ClassReader(bytes); 	// byte�z���ǂݍ��݁A���p���₷���`�ɂ���B
	 		ClassWriter cw = new ClassWriter(cr, 1); 	// �����visit���ĂԂ��Ƃɂ���ď�񂪗��܂��Ă����B
	 		ClassVisitor cv = new ClassAdapter(cw); 	// Adapter��ʂ��ď��������o����悤�ɂ���B
	 		cr.accept(cv, 0); 							// ���̃N���X�Ɠ��l�̏��Ԃ�visit���\�b�h���Ă�ł����
	 		return cw.toByteArray(); 					// Writer���̏���byte�z��ɂ��ĕԂ��B
 		}
 		else if ("net.minecraft.entity.player.EntityPlayer".equals(transformedName))
 		{
 			ClassReader cr = new ClassReader(bytes); 	// byte�z���ǂݍ��݁A���p���₷���`�ɂ���B
	 		ClassWriter cw = new ClassWriter(cr, 1); 	// �����visit���ĂԂ��Ƃɂ���ď�񂪗��܂��Ă����B
	 		ClassVisitor cv = new ClassAdapter_GetOff(cw); 	// Adapter��ʂ��ď��������o����悤�ɂ���B
	 		cr.accept(cv, 0); 							// ���̃N���X�Ɠ��l�̏��Ԃ�visit���\�b�h���Ă�ł����
	 		return cw.toByteArray(); 					// Writer���̏���byte�z��ɂ��ĕԂ��B
 		}
 		else
 			return bytes;
 	}
     
 	
 	public static class ClassAdapter extends ClassVisitor 
	{
		public ClassAdapter(ClassVisitor cv)
		{
			super(ASM5, cv);
		}

		/**
		 * ���\�b�h�ɂ��ČĂ΂��B
		 * 
		 * @param access  {@link Opcodes}�ɍڂ��Ă��Bpublic�Ƃ�static�Ƃ��̏�Ԃ��킩��B
		 * @param name	���\�b�h�̖��O�B
		 * @param desc ���\�b�h��(�����ƕԂ�l�����킹��)�^�B
		 * @param signature   �W�F�l���b�N�������܂ރ��\�b�h��(�����ƕԂ�l�����킹��)�^�B�W�F�l���b�N�t���łȂ���΂����炭null�B
		 * @param exceptions  throws��ɂ�����Ă���N���X���񋓂����BL��;�ň͂��Ă��Ȃ��̂�  {@link String#replace(char, char)}��'/'��'.'��u�����Ă���OK�B
		 * @return �����ŕԂ���MethodVisitor�̃��\�b�h�Q���K�������B  ClassWriter���Z�b�g����Ă����MethodWriter��super����~��Ă���B
		 */
		private static final String TARGET_TRANSFORMED_NAME = "func_78467_g";
		private static final String TARGET_Original_NAME = "orientCamera";
		private static final String TARGET_DESC = "(F)V";
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			boolean flag = false;
			flag |= TARGET_TRANSFORMED_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
			flag |= TARGET_Original_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
			if(flag && TARGET_DESC.equals(desc))
			{
				return new MethodAdapter(super.visitMethod(access, name, desc, signature, exceptions));
			}
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}
 	
 	public static class MethodAdapter extends MethodVisitor {
		public MethodAdapter(MethodVisitor mv) 
		{
			super(ASM5, mv);
		}

		/**
		 * int�^�ϐ����̑��쎞�ɌĂ΂��B
		 * 
		 * @param opcode   byte�͈̔͂ň�����Ȃ�BIPUSH�Ashort�͈̔͂ň�����Ȃ�SIPUSH�������Ă���B
		 * @param operand    short�͈̔͂Ɏ��܂�l�������Ă���B
		 */
		public static int MethodCount = 0;
//		private static final String TARGET_CLASS_NAME = "net/minecraft/client/enderer/RenderGlobal";
//		private static final String TARGET_TRANSFORMED_NAME = "func_72719_a"; 
//		private static final String TARGET_Orginal_NAME = "sortAndRender";
//		private static final String TARGET_DESC = "(Lnet/minecraft/entity/EntityLivingBase;ID)I";
		@Override
		public void visitVarInsn(int opcode, int var)
		{
			if(MethodCount++==0)
			{
				super.visitVarInsn(Opcodes.FLOAD, 1);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "erc/manager/ERC_CoasterAndRailManager", "CameraProc", "(F)V", false);
			}
			super.visitVarInsn(opcode, var);
		}
	}
 	
 	public static class ClassAdapter_GetOff extends ClassVisitor 
	{
		public ClassAdapter_GetOff(ClassVisitor cv){super(ASM5, cv);}

		private static final String TARGET_TRANSFORMED_NAME = "func_70098_U";
		private static final String TARGET_Original_NAME = "updateRidden";
		private static final String TARGET_DESC = "()V";
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			boolean flag = false;
			flag |= TARGET_TRANSFORMED_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
			flag |= TARGET_Original_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
//			if(flag && TARGET_DESC.equals(desc))
//			{
//				return new MethodAdapter_GetOff(super.visitMethod(access, name, desc, signature, exceptions));
//			}
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}
 	
 	public static class MethodAdapter_GetOff extends MethodVisitor {
		public MethodAdapter_GetOff(MethodVisitor mv) {super(ASM5, mv);}

		public static int MethodCount = 0;
		@Override
		public void visitVarInsn(int opcode, int var)
		{
			if(opcode == Opcodes.ALOAD && var == 0)
			{
				MethodCount++;
				if(MethodCount == 1)
				{
					super.visitVarInsn(Opcodes.ALOAD, 0);
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "erc/manager/ERC_CoasterAndRailManager", "GetOffAndButtobi", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false);
				}
			}
			super.visitVarInsn(opcode, var);
		}
		
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
		{
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			if(name.equals("setSneaking") || name.equals("func_70095_a"))
			{
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "erc/manager/ERC_CoasterAndRailManager", "motionactive", "()V", false);
			}
		}
	}
 	
	/**
	 * ���\�b�h�̖��O���Փǉ�(deobfuscation)����B
	 */
	public static String mapMethodName(String owner, String methodName, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(owner), methodName, desc);
	}
	
	/**
	 * �N���X�̖��O���ǉ�(obfuscation)����B
	 */
	public static String unmapClassName(String name) {
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
	}

}

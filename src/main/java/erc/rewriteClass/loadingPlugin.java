package erc.rewriteClass;

import java.io.File;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

//@TransformerExclusions({"erc.rewriteClass"})
@MCVersion("1.7.10")
public class loadingPlugin implements IFMLLoadingPlugin {
	
	static File location;
	
	public String[] getLibraryRequestClass() {
		return null;
	}

    @Override
    public String[] getASMTransformerClass() {
    	return new String[] {"erc.rewriteClass.classTransformer"};
    }
    @Override
    public String getModContainerClass() {
    	return "erc.rewriteClass.modContainer";
    }
    @Override
    public String getSetupClass() {
    	return null;
    }
    
    // IFMLLoadingPlugin �̃��\�b�h�ł��B(IFMLCallHook �ɂ������V�O�l�`���[�̃��\�b�h������܂����A�Ⴂ�܂�)
    // ����� coremod ���g�� jar �t�@�C���p�X���擾���Ă��܂��B����͌�q�̃g�����X�t�H�[�}�[�N���X�ŁA
    // jar����u���p�N���X���擾���Ă��邽�߂ŁA���̂悤�ȏ������s��Ȃ��̂ł���Ή����������Ȃ��Ă��\���܂���B
    // 
    // �Ȃ��AIFMLLoadingPlugin �̃��\�b�h�Ƃ��ČĂ΂ꂽ�ۂ́A"mcLocation"�A"coremodList"�A"coremodLocation" ��3�A
    // IFMLCallHook �̃��\�b�h�Ƃ��ČĂ΂ꂽ�ۂ́A"classLoader" ���}�b�v�ɐݒ肳��Ă��܂��B(FML#511����)
    // 
    // �n�����}�b�v�̒��g�́Anet.minecraftforge.fml.relauncher.RelaunchLibraryManager �̎���������m�F���鎖���o���܂��B	TODO
    @Override
    public void injectData(Map<String, Object> data) {
    	 if (data.containsKey("coremodLocation"))
         {
             location = (File) data.get("coremodLocation");
         }
    }

	@Override
	public String getAccessTransformerClass() {
		return "erc.rewriteClass.classTransformer";
	}
}

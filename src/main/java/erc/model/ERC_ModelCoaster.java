package erc.model;

import erc.renderer.ModelRenderer;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.FMLClientHandler;
import erc.entity.ERC_EntityCoaster;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;

public class ERC_ModelCoaster extends ModelBase {
	
	private OBJModel modelCoaster;
	private ResourceLocation TextureResource;
	
	@SuppressWarnings("unused")
	private ERC_ModelCoaster(){} //���[�h����t�@�C�������w��C���X�^���X�������ۂł���H
	
	public ERC_ModelCoaster(OBJModel Obj, ResourceLocation Tex)
	{
		modelCoaster = Obj;
		TextureResource = Tex;
	}
	
	public void render(ERC_EntityCoaster coaster, double x, double y, double z, float t) 
	{
 		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
// 		if(coaster.ERCPosMat != null)
//		{
// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
//		}
		GL11.glRotatef(180.0F - coaster.ERCPosMat.getFixedYaw(t), 0f, 1f, 0f);
		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),-1f, 0f, 0f);
 		GL11.glRotatef(-coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

		GL11.glScalef(1.0f, 1.0f, 1.0f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
		ModelRenderer.renderObj(this.modelCoaster);
		GL11.glPopMatrix();
	}
}
package erc.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
	
	private void render() 
	{
		//Prepare to draw
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.disableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		vertexbuffer.pos(1.0, 0.0, 1.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();
		vertexbuffer.pos(1.0, 0.0, 0.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();
		vertexbuffer.pos(0.0, 0.0, 0.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();
		vertexbuffer.pos(0.0, 0.0, 1.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();

		vertexbuffer.pos(1.0, 0.0, 0.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();
		vertexbuffer.pos(1.0, 0.0, 1.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();
		vertexbuffer.pos(0.0, 0.0, 1.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();
		vertexbuffer.pos(0.0, 0.0, 0.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();

		tessellator.draw();

		//Clear up
		GlStateManager.enableLighting();
	}
	
	public void render(ERC_EntityCoaster coaster, double x, double y, double z, float t) 
	{
 		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
// 		if(coaster.ERCPosMat != null)
//		{
// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
//		}
		GL11.glRotatef(coaster.ERCPosMat.getFixedYaw(t), 0f, -1f, 0f);
 		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),1f, 0f, 0f);
 		GL11.glRotatef(coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

		GL11.glScalef(1.0f, 1.0f, 1.0f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
		this.render();
		GL11.glPopMatrix();
	}

}
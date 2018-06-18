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

import javax.vecmath.Vector3f;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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

		//////////////////////////////////////////////////////////////////////
		//Green and blue square - backup until can render the thing properly//
		//////////////////////////////////////////////////////////////////////
//		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
//		vertexbuffer.pos(1.0, 0.0, 1.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();
//		vertexbuffer.pos(1.0, 0.0, 0.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();
//		vertexbuffer.pos(0.0, 0.0, 0.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();
//		vertexbuffer.pos(0.0, 0.0, 1.0).color(0.7F, 1.0F, 0.7F, 1.0F).endVertex();
//
//		vertexbuffer.pos(1.0, 0.0, 0.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();
//		vertexbuffer.pos(1.0, 0.0, 1.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();
//		vertexbuffer.pos(0.0, 0.0, 1.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();
//		vertexbuffer.pos(0.0, 0.0, 0.0).color(0.7F, 0.7F, 1.0F, 1.0F).endVertex();

		///////////////////////////////////////////////////////////////
		//Placeholder coaster renderer; until I find something better//
		///////////////////////////////////////////////////////////////
		//Yes it's deprecated but I don't know any alternatives
		Collection<OBJModel.Group> groups = this.modelCoaster.getMatLib().getGroups().values();
		Iterator<OBJModel.Group> iterGroup = groups.iterator();
		vertexbuffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_NORMAL);
		while(iterGroup.hasNext()) {
			OBJModel.Group g = iterGroup.next();
			Set<OBJModel.Face> faces = g.getFaces();
			Iterator<OBJModel.Face> iterFace = faces.iterator();
			while(iterFace.hasNext()) {
				OBJModel.Face f = iterFace.next();
				OBJModel.Vertex[] vertices = f.getVertices();
				Vector3f pos;
				OBJModel.Normal norm;
				OBJModel.TextureCoordinate uv;
				for(int i = 0; i < vertices.length; ++i) {
					pos = vertices[i].getPos3();
					norm = vertices[i].getNormal();
					uv = vertices[i].getTextureCoordinate();
					//OBJModel: May need to use 1 - uv.v instead
					vertexbuffer.pos(pos.x, pos.y, pos.z).tex(uv.u, uv.v).normal(norm.x, norm.y, norm.z).endVertex();
				}
			}
		}

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
		GL11.glRotatef(180.0F - coaster.ERCPosMat.getFixedYaw(t), 0f, 1f, 0f);
		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),-1f, 0f, 0f);

 		GL11.glRotatef(-coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

		GL11.glScalef(1.0f, 1.0f, 1.0f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
		this.render();
		GL11.glPopMatrix();
	}
}
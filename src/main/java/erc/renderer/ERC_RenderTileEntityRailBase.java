package erc.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc._core.ERC_CONST;
import erc.manager.ERC_CoasterAndRailManager;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

@SideOnly(Side.CLIENT)
public class ERC_RenderTileEntityRailBase extends TileEntitySpecialRenderer<Wrap_TileEntityRail>{
	
//	private static final ResourceLocation TEXTURE  ;
	private static final ResourceLocation TEXTUREguiarraw = new ResourceLocation(ERC_CONST.DOMAIN,"textures/gui/ringarraw.png");
	private static final GameSettings settings = Minecraft.getMinecraft().gameSettings;
	//new ResourceLocation(", "textures/blocks/pink.png");
	
	public void renderTileEntityAt(Wrap_TileEntityRail t, double x, double y, double z)
	{
		Tessellator tessellator = Tessellator.getInstance();
		this.bindTexture(t.getDrawTexture());
		GlStateManager.pushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE); // �J�����OOFF
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		//tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		t.render(tessellator);

		//GL11.glTranslated(t.x, t.y, t.z);
		//GL11.glTranslated(0.5, y-t.y, z-t.z);
    	
    	if(t == ERC_CoasterAndRailManager.clickedTileForGUI){
    		DrawRotaArrow(tessellator, t);
    	}
    	//DrawArrow(tessellator, t.vecUp);
      	GL11.glEnable(GL11.GL_CULL_FACE); // �J�����OON
		GL11.glPopMatrix();
	}
	
	public void p_bindTexture(ResourceLocation texture){ this.bindTexture(texture);}
	
	@SuppressWarnings("unused")
	private void DrawArrow(Tessellator tess, Vec3d vec)
	{
		BufferBuilder bb = tess.getBuffer();
      	bb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
      	bb.pos(0.2d, 0d, 0.2d).tex(0.0d, 0.0d).endVertex();
      	bb.pos(vec.x*3d, vec.y*3d, vec.z*3d).tex(0.0d, 0.0d).endVertex();
      	bb.pos(-0.2d, 0d, -0.2d).tex(0.0d, 0.0d).endVertex();
      	tess.draw();
	}
	
	// GUI�\�����̉�]���`��p
	public void DrawRotaArrow(Tessellator tess, Wrap_TileEntityRail tile)
	{
		this.bindTexture(TEXTUREguiarraw);
      	Vec3d d = tile.getRail().BaseRail.vecDir;
		Vec3d u = tile.getRail().BaseRail.vecUp;
      	Vec3d p = d.crossProduct(u);
      	
      	d = d.normalize();
      	u = u.normalize();
      	p = p.normalize();
      	
      	float s = 2.0f; // s
      	
      	// yaw axis
      	GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
      	BufferBuilder bb = tess.getBuffer();
      	bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
	    bb.pos(( d.x+p.x)*s, ( d.y+p.y)*s, ( d.z+p.z)*s).tex(0.0d, 0.0d).endVertex();
		bb.pos(( d.x-p.x)*s, ( d.y-p.y)*s, ( d.z-p.z)*s).tex(1.0d, 0.0d).endVertex();
		bb.pos((-d.x+p.x)*s, (-d.y+p.y)*s, (-d.z+p.z)*s).tex(0.0d, 1.0d).endVertex();
		bb.pos((-d.x-p.x)*s, (-d.y-p.y)*s, (-d.z-p.z)*s).tex(1.0d, 1.0d).endVertex();
		tess.draw();
		// pitch axis
		s = 1.5f;
		GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
	    bb.pos(( u.x+d.x)*s, ( u.y+d.y)*s, ( u.z+d.z)*s).tex(0.0d, 0.0d).endVertex();
		bb.pos(( u.x-d.x)*s, ( u.y-d.y)*s, ( u.z-d.z)*s).tex(1.0d, 0.0d).endVertex();
		bb.pos((-u.x+d.x)*s, (-u.y+d.y)*s, (-u.z+d.z)*s).tex(0.0d, 1.0d).endVertex();
		bb.pos((-u.x-d.x)*s, (-u.y-d.y)*s, (-u.z-d.z)*s).tex(1.0d, 1.0d).endVertex();
		tess.draw();
		// roll axis
		s = 1.0f;
		GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
	    bb.pos(( u.x+p.x)*s, ( u.y+p.y)*s, ( u.z+p.z)*s).tex(0.0d, 0.0d).endVertex();
		bb.pos(( u.x-p.x)*s, ( u.y-p.y)*s, ( u.z-p.z)*s).tex(1.0d, 0.0d).endVertex();
		bb.pos((-u.x+p.x)*s, (-u.y+p.y)*s, (-u.z+p.z)*s).tex(0.0d, 1.0d).endVertex();
		bb.pos((-u.x-p.x)*s, (-u.y-p.y)*s, (-u.z-p.z)*s).tex(1.0d, 1.0d).endVertex();
		tess.draw();                            
	}

	@Override
	public void render(Wrap_TileEntityRail te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		renderTileEntityAt(te,x,y,z);
	}

	/**
	 * Fixes rails disappearing from certain viewpoints.
	 */
	@Override
	public boolean isGlobalRenderer(Wrap_TileEntityRail te) {
		//Use this line to only use the fix on fancy graphics, if this turns out to be a performance issue.
		//Though it is probably better to make a renderer for all rails, in a RenderWorldLastEvent handler.
		//return settings.fancyGraphics;
		return true;
	}
}

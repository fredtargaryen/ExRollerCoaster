package erc.renderer;

import erc._core.ERC_CONST;
import erc.entity.entitySUSHI;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class renderEntitySUSIHI extends Render<entitySUSHI> {

	private static final ResourceLocation tex = new ResourceLocation(ERC_CONST.DOMAIN + ":textures/entities/sushi.png");
	protected boolean canBePushed = true;

	protected renderEntitySUSIHI(RenderManager renderManager) {
		super(renderManager);
	}

	public void doRender(entitySUSHI sushi, double x, double y, double z, float f, float p_76986_9_)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y-0.2f, (float)z);
		GL11.glRotatef(sushi.getInterpRotation(f), 0f, -1f, 0f);
// 		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),1f, 0f, 0f);
// 		GL11.glRotatef(coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

		GL11.glScalef(1.2f, 1.2f, 1.2f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(tex);
		OBJModel model = entitySUSHI.models[sushi.getId()];
		if (model != null) ModelRenderer.renderObj(model);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(entitySUSHI e) {
		return tex;
	}
}

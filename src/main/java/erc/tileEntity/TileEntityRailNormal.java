package erc.tileEntity;

import erc.entity.ERC_EntityCoaster;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityRailNormal extends TileEntityRailBase{

	@Override
	public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster) {}

	@Override
	public World getWorldObj() {
		return this.world;
	}

	@Override
	public void render(Tessellator tess)
	{
		GlStateManager.disableLighting();
		float col = 1.0f;
		GL11.glColor4f(col, col, col, 1.0F);
		super.render(tess);
		GlStateManager.enableLighting();
	}
}

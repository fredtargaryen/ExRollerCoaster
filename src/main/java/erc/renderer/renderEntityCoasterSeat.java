package erc.renderer;

import erc.entity.ERC_EntityCoasterSeat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class renderEntityCoasterSeat extends Render<ERC_EntityCoasterSeat> {

	public renderEntityCoasterSeat(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
		public void doRender(ERC_EntityCoasterSeat entity, double x, double y, double z, float f, float p_76986_9_) {
//			renderOffsetAABB(entity.boundingBox.getOffsetBoundingBox(-entity.posX, -entity.posY, -entity.posZ), x, y, z);
			return;
		}

		@Override
		protected ResourceLocation getEntityTexture(ERC_EntityCoasterSeat p_110775_1_) {
			return null;
		}


}

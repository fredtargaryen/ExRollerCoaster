package erc.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc.entity.ERC_EntityCoaster;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ERC_RenderEntityCoaster extends Render<ERC_EntityCoaster> {
	
	protected boolean canBePushed = true;

	public ERC_RenderEntityCoaster(RenderManager renderManager) {
		super(renderManager);
	}

	public void doRender(ERC_EntityCoaster Coaster, double x, double y, double z, float f, float p_76986_9_)
	{
		if(Coaster.getModelRenderer()==null)return;
		Coaster.getModelRenderer().render((ERC_EntityCoaster)Coaster, x, y, z, p_76986_9_);
//		Entity[] ea = Coaster.getParts();
//		for(int i=0; i<ea.length; i++)
//		{
//			renderOffsetAABB(ea[i].boundingBox.getOffsetBoundingBox(-Coaster.posX, -Coaster.posY, -Coaster.posZ), x, y, z);
//		}
	}

	@Override
	protected ResourceLocation getEntityTexture(ERC_EntityCoaster p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}


}

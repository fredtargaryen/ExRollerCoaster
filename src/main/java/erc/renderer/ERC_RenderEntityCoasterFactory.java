package erc.renderer;

import erc.entity.ERC_EntityCoaster;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class ERC_RenderEntityCoasterFactory implements IRenderFactory<ERC_EntityCoaster>
{
    @Override
    public Render<? super ERC_EntityCoaster> createRenderFor(RenderManager manager)
    {
        return new ERC_RenderEntityCoaster(manager);
    }
}

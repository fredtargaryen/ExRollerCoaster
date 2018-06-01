package erc.renderer;

import erc.entity.ERC_EntityCoasterSeat;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class renderEntityCoasterSeatFactory implements IRenderFactory<ERC_EntityCoasterSeat> {
    @Override
    public Render<? super ERC_EntityCoasterSeat> createRenderFor(RenderManager manager) {
        return new renderEntityCoasterSeat(manager);
    }
}

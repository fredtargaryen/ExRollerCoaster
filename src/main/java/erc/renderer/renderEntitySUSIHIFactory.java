package erc.renderer;

import erc.entity.entitySUSHI;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class renderEntitySUSIHIFactory implements IRenderFactory<entitySUSHI> {
    @Override
    public Render<? super entitySUSHI> createRenderFor(RenderManager manager) {
        return new renderEntitySUSIHI(manager);
    }
}

package erc.model;

import erc._core.ERC_CONST;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * .obj file loader for the coasters and sushi
 */
public class ModelLoader implements ICustomModelLoader {
    //public static final ExampleModel EX_MO = new ExampleModel();

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        String domain = modelLocation.getResourceDomain();
        if(domain.equals(ERC_CONST.DOMAIN) || domain.equals(ERC_CONST.D_AM)) {
            String path = modelLocation.getResourcePath();
            return path.startsWith("coaster") || path.startsWith("sushi");
        }
        return false;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        //return EX_MO;
        return null;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}

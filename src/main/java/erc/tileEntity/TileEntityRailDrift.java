package erc.tileEntity;

import erc.entity.ERC_EntityCoaster;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TileEntityRailDrift extends TileEntityRailBase {
    public TileEntityRailDrift() {
        super();
        RailTexture = new ResourceLocation("textures/blocks/ice.png");
    }

    @Override
    public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster) {
        EntityCoaster.ERCPosMat.yaw = EntityCoaster.ERCPosMat.prevYaw;
    }

    @Override
    public World getWorldObj() {
        return this.world;
    }
}

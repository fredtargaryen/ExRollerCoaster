package erc.tileEntity;

import erc.entity.ERC_EntityCoaster;
import net.minecraft.world.World;

public class TileEntityRailNormal extends TileEntityRailBase{

	@Override
	public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster) {}

	@Override
	public World getWorldObj() {
		return this.world;
	}
}

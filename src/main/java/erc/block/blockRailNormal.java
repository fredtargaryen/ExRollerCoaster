package erc.block;

import erc.tileEntity.TileEntityRailNormal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class blockRailNormal extends blockRailBase{
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityRailNormal();
	}
}

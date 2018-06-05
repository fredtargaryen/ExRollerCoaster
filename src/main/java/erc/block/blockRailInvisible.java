package erc.block;

import erc.tileEntity.TileEntityRailInvisible;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class blockRailInvisible extends blockRailBase{

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityRailInvisible();
	}

}

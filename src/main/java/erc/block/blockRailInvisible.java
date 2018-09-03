package erc.block;

import erc.tileEntity.TileEntityRailInvisible;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class blockRailInvisible extends blockRailBase{

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityRailInvisible();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
}

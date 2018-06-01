package erc.block;

import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class blockRailDetector extends blockRailBase{

	@Override
	public TileEntityRailBase getTileEntityInstance() 
	{
		return new TileEntityRailDetector();
	}

	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}
	

    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
    	TileEntityRailDetector rail = (TileEntityRailDetector)blockAccess.getTileEntity(pos);
        return rail.getFlag() ? 15 : 0;
    }

    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
//        return (p_149748_1_.getBlockMetadata(p_149748_2_, p_149748_3_, p_149748_4_) & 8) == 0 ? 0 : (p_149748_5_ == 1 ? 15 : 0);
    	TileEntityRailDetector rail = (TileEntityRailDetector)blockAccess.getTileEntity(pos);
        return rail.getFlag() ? 15 : 0;
    }
	
}

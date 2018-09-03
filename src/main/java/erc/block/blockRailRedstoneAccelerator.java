package erc.block;

import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailRedstoneAccelerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author MOTTY
 *
 */
public class blockRailRedstoneAccelerator extends blockRailBase{
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityRailRedstoneAccelerator();
	}

	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
        	boolean flag = world.isBlockIndirectlyGettingPowered(pos) == 0;
        	
        	if (flag)
            {
//        		 TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator)world.getTileEntity(x, y, z);
//        		 boolean tgle = rail.getToggleFlag();
             	
//        		 if (flag != tgle)
                 {
//                 	rail.changeToggleFlag();
                     //Not powered, so use the higher meta values - FT
                 	world.setBlockState(pos, state.withProperty(META,8^state.getValue(META)), 2);
//                 	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                 	world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0F, 0F); //���ʉ��H
                 }
            } 
        }
    }

    @Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator) worldIn.getTileEntity(pos);
		rail.setToggleFlag(worldIn.getBlockState(pos).getValue(META) < 8);
    }

	// �ԐΓ��͗p
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            boolean poweredNow = world.isBlockIndirectlyGettingPowered(pos) > 0;
            TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator)world.getTileEntity(pos);
            boolean poweredBefore = rail.getToggleFlag();
            if (poweredBefore != poweredNow)
            {
                rail.setToggleFlag(poweredNow);
                world.setBlockState(pos, state.withProperty(META,8^state.getValue(META)), 2);
                ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0F, 0F); //���ʉ��H
            }
        }
    }
}

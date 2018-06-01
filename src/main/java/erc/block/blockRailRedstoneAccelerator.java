package erc.block;

import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailRedstoneAccelerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author MOTTY
 *
 */
public class blockRailRedstoneAccelerator extends blockRailBase{
	
	@Override
	public TileEntityRailBase getTileEntityInstance() 
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
//        		 ERC_TileEntityRailRedstoneAccelerator rail = (ERC_TileEntityRailRedstoneAccelerator)world.getTileEntity(x, y, z);
//        		 boolean tgle = rail.getToggleFlag();
             	
//        		 if (flag != tgle)
                 {
//                 	rail.changeToggleFlag();
                 	world.setBlockState(pos, state.withProperty(META,8^state.getValue(META)), 2);
//                 	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                     //Sound 1003?? - FT
                 	world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 0F, 0F); //���ʉ��H
                 }
            } 
        }
    }

    @Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator) worldIn.getTileEntity(pos);
		rail.setToggleFlag(0 != (8 & worldIn.getBlockState(pos).getValue(META)));
    }

	// �ԐΓ��͗p
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            boolean flag = world.isBlockIndirectlyGettingPowered(pos) == 0;
            
            if (flag || block.canProvidePower(state))
            {
            	TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator)world.getTileEntity(pos);
            	boolean tgle = rail.getToggleFlag();

                if (flag != tgle)
                {
                	rail.changeToggleFlag();
                	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                	//Sound 1003?? - FT
                	world.playSound((EntityPlayer)null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0F, 0F); //���ʉ��H
                }
            }
        }
    }
}

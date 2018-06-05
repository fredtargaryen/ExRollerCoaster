package erc.block;

import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailConstVelosity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class blockRailConstVelocity extends blockRailBase{

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityRailConstVelosity();
	}
	

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            boolean flag = world.isBlockIndirectlyGettingPowered(pos) != 0;
            
            if (flag || block.canProvidePower(state))
            {
            	TileEntityRailConstVelosity rail = (TileEntityRailConstVelosity)world.getTileEntity(pos);
            	boolean tgle = rail.getToggleFlag();
            	
            	if (flag && !tgle)
                {
            		rail.changeToggleFlag();
            		rail.turnOnFlag();
                	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                	//What was sound 1003 - FT
                	world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 0F, 0F); //���ʉ��H
                }
            	else if(!flag && tgle)
                {
                	rail.changeToggleFlag();
                }
            }
        }
    }
}

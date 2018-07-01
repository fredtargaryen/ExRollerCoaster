package erc.item;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc._core.ERC_CONST;
import erc.message.ERC_MessageItemWrenchSync;
import erc.message.ERC_PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ERC_ItemWrenchPlaceBlock extends Item {
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if(world.isRemote)
		{
			Block placedBlock = Blocks.DIRT;
			ItemStack placedBlockItemStack = new ItemStack(Item.getItemFromBlock(placedBlock));
			
			boolean iscreative = player.capabilities.isCreativeMode;
			if(!player.inventory.hasItemStack(placedBlockItemStack) && !iscreative)return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(handIn));
	
			double pit = Math.cos(Math.toRadians(player.rotationPitch));
			int x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*2*pit);
			int y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*2);
			int z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*2*pit);
			BlockPos pos = new BlockPos(x, y, z);
			
			// �u���b�N��ݒu�ł��邩�`�F�b�N
			Block b = world.getBlockState(pos).getBlock();
			boolean canPlaceBlock = b == Blocks.AIR || b == Blocks.WATER || b == Blocks.FLOWING_WATER;
			
			if(canPlaceBlock)
			{
	    		if(!iscreative)player.inventory.clearMatchingItems(placedBlockItemStack.getItem(), -1, 1, null);
	    		world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
	    		player.swingArm(handIn);
	    		ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
			
		}
		return super.onItemRightClick(world, player, handIn);
	}
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, Block block)
    {
    	BlockPos pos = new BlockPos(x, y, z);
		if( (world.getBlockState(pos).getBlock() != Blocks.AIR) )return false;
    	if (!world.setBlockState(pos, block.getDefaultState(), 3))
    	{
    		return false;
    	}
//    	ERC_Logger.info("place block");
    	if (world.getBlockState(pos).getBlock() == block)
    	{
			block.onBlockPlacedBy(world, pos, block.getDefaultState(), player, stack);
			world.scheduleBlockUpdate(pos, block,0, 0);
    	}
    	return true;
    }
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		Block placedBlock = Blocks.DIRT;
		Item placedBlockItem = Item.getItemFromBlock(placedBlock);
		
		boolean iscreative = player.capabilities.isCreativeMode;
		if(!player.inventory.hasItemStack(new ItemStack(placedBlockItem)) && !iscreative)return EnumActionResult.FAIL;

		double pit = Math.cos(Math.toRadians(player.rotationPitch));
		int x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*2*pit);
		int y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*2);
		int z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*2*pit);
		BlockPos newPos = new BlockPos(x, y, z);
		
		// �u���b�N��ݒu�ł��邩�`�F�b�N
		Block b = world.getBlockState(newPos).getBlock();
		boolean canPlaceBlock = (b == Blocks.AIR) || (b == Blocks.WATER) || (b == Blocks.FLOWING_WATER);
		
		if(canPlaceBlock)
		{
    		if(!iscreative)player.inventory.clearMatchingItems(placedBlockItem, -1, 1, null);
    		world.playSound(player, pos, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS, 1.0F, 1.0F);

    		ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
		}
        return EnumActionResult.SUCCESS;
	}

	
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return EnumActionResult.FAIL;
	}
}

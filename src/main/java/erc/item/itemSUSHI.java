package erc.item;

import erc.entity.entitySUSHI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class itemSUSHI extends Item{
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	if (!world.isRemote)
    	{
    		Entity e = new entitySUSHI(world,pos.getX()+0.5,pos.getY()+0.8,pos.getZ()+0.5);
//    		Entity e = new entityPartsTestBase(world,x+0.5,y+1.5,z+0.5);
    		world.spawnEntity(e);
    	}
    	player.getHeldItem(hand).grow(-1);
    	return EnumActionResult.SUCCESS;
    }
}
	
package erc.item;

import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ERC_ItemSmoothAll extends Item {

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if(world.isRemote == false)
		{
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof Wrap_TileEntityRail)
			{
				TileEntityRailBase rail = ((Wrap_TileEntityRail) te).getRail();
				if(rail == null)return EnumActionResult.SUCCESS;
//				ERC_Logger.info("start");
				smoothrail(0, rail, (Wrap_TileEntityRail) te, world, 1);
				smoothrail(0, rail, (Wrap_TileEntityRail) te, world, -1);
			}
		}
			
		
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	private void smoothrail(int num, TileEntityRailBase root, Wrap_TileEntityRail rail, World world, int v)
	{
//		ERC_Logger.info("num"+num);
		if(num>=100)return;
		if(num<=-100)return;
		if(num != 0 && root == rail)return;
		if(rail == null)return;
		
		rail.Smoothing();
		rail.CalcRailLength();
		rail.syncData();
    	Wrap_TileEntityRail prev = rail.getPrevRailTileEntity();
    	if(prev!=null)
    	{
    		TileEntityRailBase r = prev.getRail();
    		r.SetNextRailVectors((TileEntityRailBase) rail.getRail());
    		r.CalcRailLength();
    		prev.syncData();
    	}
		smoothrail(num+v, root, (v==1?rail.getNextRailTileEntity():rail.getPrevRailTileEntity()), world, v);
	}
}

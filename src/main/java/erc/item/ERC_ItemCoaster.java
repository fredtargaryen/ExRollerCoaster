package erc.item;

import erc.block.blockRailBase;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ModelLoadManager;
import erc.message.ERC_MessageSpawnRequestWithCoasterOpCtS;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ERC_ItemCoaster extends Wrap_ItemCoaster
{
    public ERC_ItemCoaster()
    {
    	CoasterType = 0;
    }

    public ERC_EntityCoaster getItemInstance(World world, Wrap_TileEntityRail tile, double x, double y, double z)
    {
    	return new ERC_EntityCoaster(world, tile.getRail(),x, y, z);
    }
    
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	if (!blockRailBase.isBlockRail(world.getBlockState(pos).getBlock()))
    	{
            return EnumActionResult.FAIL;
        }
    	
    	if (world.isRemote)
    	{
    		setCoaster(pos.getX(), pos.getY(), pos.getZ(), -1);
    	}

        player.getHeldItemMainhand().grow(-1);
        return EnumActionResult.SUCCESS;
    }
    
    public void setCoaster(int x, int y, int z, int parentID)
    {
    	ERC_CoasterAndRailManager.SetCoasterPos(x, y, z);
		ERC_CoasterAndRailManager.saveModelID = modelCount;
		ERC_MessageSpawnRequestWithCoasterOpCtS packet = new ERC_MessageSpawnRequestWithCoasterOpCtS(this, modelCount, ERC_ModelLoadManager.getModelOP(modelCount, CoasterType),x,y,z,parentID);
	    ERC_PacketHandler.INSTANCE.sendToServer(packet);
    }
}
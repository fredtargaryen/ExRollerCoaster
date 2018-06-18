package erc.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_ModelLoadManager;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public abstract class Wrap_ItemCoaster extends Item{

	protected int modelCount = 0;
	protected int CoasterType = 0;
	
	public int getModelCount(){return modelCount;}
	public int getCoasterType(){return CoasterType;}
	
	public abstract ERC_EntityCoaster getItemInstance(World world, Wrap_TileEntityRail tile, double x, double y, double z);
	
    @SideOnly(Side.CLIENT)
    public void ScrollMouseHweel(int dhweel)
    {
//    	ERC_Logger.info("wrap_itemcoaster : dhweel:"+dhweel);
    	modelCount += dhweel>0?1:-1;
    	if(modelCount >= ERC_ModelLoadManager.getModelPackNum(CoasterType)) modelCount=0;
    	else if(modelCount < 0)modelCount = ERC_ModelLoadManager.getModelPackNum(CoasterType)-1;
//    	ERC_Logger.info("modelcount:"+modelCount);
    }
}

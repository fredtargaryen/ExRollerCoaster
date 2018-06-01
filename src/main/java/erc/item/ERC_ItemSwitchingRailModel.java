package erc.item;

import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc.block.blockRailBase;
import erc.gui.GUIRail;
import erc.manager.ERC_ModelLoadManager;
import erc.message.ERC_MessageRailGUICtS;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ERC_ItemSwitchingRailModel extends Item{

	private int modelCount = 0;
	
	public ERC_ItemSwitchingRailModel(){}
	
    public int getModelCount(){return modelCount;}
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
    	if (world.isRemote)
    	{
    		// �E�N���b�N�����u���b�N�����[���u���b�N�Ȃ�OK
	    	if (!blockRailBase.isBlockRail(world.getBlockState(pos).getBlock())) return EnumActionResult.FAIL;
	    	
	    	// Wrap_TileEntityRail�������OK(���Ԃ񂠂�)
	    	Wrap_TileEntityRail tile = (Wrap_TileEntityRail) world.getTileEntity(pos);
	    	
	    	// ���f���`��N���X����ւ�
	    	tile.changeRailModelRenderer(modelCount);
	    	
	    	ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(pos.getX(), pos.getY(), pos.getZ(), GUIRail.editFlag.RailModelIndex.ordinal(), modelCount);
	    	ERC_PacketHandler.INSTANCE.sendToServer(packet);
    	}
        return EnumActionResult.SUCCESS;
    }
    
    @SideOnly(Side.CLIENT)
    public void ScrollMouseHweel(int dhweel)
    {
//    	ERC_Logger.info("wrap_itemcoaster : dhweel:"+dhweel);
    	modelCount += dhweel>0?1:-1;
    	if(modelCount >= ERC_ModelLoadManager.getRailPackNum()+1) modelCount=0;
    	else if(modelCount < 0)modelCount = ERC_ModelLoadManager.getRailPackNum();
//    	ERC_Logger.info("modelcount:"+modelCount);
    }

    //Use when dealing with textures - FT
//	@SideOnly(Side.CLIENT)
//    public void registerIcons(IIconRegister p_94581_1_)
//    {
//		String[] names = ERC_ModelLoadManager.getRailIconStrings();
//		itemIcons = new IIcon[names.length];
//    	for(int i=0;i<names.length;++i)
//    	{
//    		this.itemIcons[i] = p_94581_1_.registerIcon(names[i]);
//    	}
//    	itemIcon = itemIcons[0];
//    }
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIconFromDamage(int p_77617_1_)
//	{
//		return itemIcons[modelCount];
//	}
}

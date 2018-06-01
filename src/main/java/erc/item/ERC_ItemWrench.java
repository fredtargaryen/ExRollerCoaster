package erc.item;

import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import erc.block.blockRailBase;
import erc.manager.ERC_CoasterAndRailManager;
import erc.message.ERC_MessageConnectRailCtS;
import erc.message.ERC_MessageItemWrenchSync;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ERC_ItemWrench extends Item {

	/**
	 * �A�C�e���@�����`
	 * �@�\
	 * �E���[���Ԃ̐ڑ�
	 * �E���[���̊p�x�Ȃǒ���
	 * �X�j�[�N�{�E�N���b�N�ŋ@�\�؂�ւ�
	 * �ʏ�E�N���b�N�ŋ@�\����
	 */
	
	int mode = 0;
	static final int modenum = 2;
	final String ModeStr[] = {	"Connection mode", 
								"Adjustment mode"};
	final String texStr[] = {	"wrench_c1", 
								"wrench_c2",
								"wrench_e1",
								"wrench_e2"};
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if(player.isSneaking())
		{
			if(world.isRemote) 
			{	//client
				// ���[�h�ύX�̓N����
				mode = (++mode)%modenum;
				ERC_CoasterAndRailManager.ResetData(); // ���[�h�`�F���W�ŋL������
				player.sendStatusMessage(new TextComponentString(ModeStr[mode]), false);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
		}
		return super.onItemRightClick(world, player, handIn);
	}
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, Block block)
    {
    	BlockPos pos = new BlockPos(x, y, z);
		Block convblock = world.getBlockState(pos).getBlock();
		if( (convblock != Blocks.AIR) && (convblock != Blocks.WATER) && (convblock != Blocks.FLOWING_WATER) )return false;
		
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
		if(player.isSneaking()) return EnumActionResult.FAIL;
		
		// func_150051...�֐��ւ̓��͂̌^��BlockRailTest�Ȃ�true
    	if (blockRailBase.isBlockRail(world.getBlockState(pos).getBlock()))
    	{
    		if(world.isRemote) // �N���C�A���g
    		{
    			switch(mode)
        		{
        		case 0 : 
        			//�N���C�A���g�̓��[����������Γo�^�A����΃T�[�o�[�ɐڑ��p�p�P�b�g���M
        			if(!ERC_CoasterAndRailManager.isPlacedPrevRail())ERC_CoasterAndRailManager.SetPrevData(pos.getX(), pos.getY(), pos.getZ());
        			else{
        				ERC_MessageConnectRailCtS packet 
        					= new ERC_MessageConnectRailCtS(
        							ERC_CoasterAndRailManager.prevX, ERC_CoasterAndRailManager.prevY, ERC_CoasterAndRailManager.prevZ, 
        							pos.getX(), pos.getY(), pos.getZ()
							);
        				ERC_PacketHandler.INSTANCE.sendToServer(packet);
//        				ERC_Logger.info("connection : "+"."+ERC_CoasterAndRailManager.prevX+"."+ERC_CoasterAndRailManager.prevY+"."+ERC_CoasterAndRailManager.prevZ
//    	        				+" -> "+x+"."+y+"."+z);
        				ERC_CoasterAndRailManager.ResetData();
        			}
        			break;
        		case 1 : 
        			Wrap_TileEntityRail tile =  (Wrap_TileEntityRail)world.getTileEntity(pos);
        			ERC_CoasterAndRailManager.OpenRailGUI(tile.getRail());
//        			ERC_Logger.warn("save rail to manager : "+tile.getRail().getClass().getName());
        			break;
        		}
    			
			    ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(mode,pos.getX(), pos.getY(), pos.getZ()));
        		return EnumActionResult.FAIL;
    		}
    		
    		return EnumActionResult.SUCCESS;
    	}
        return EnumActionResult.FAIL;
	}

	
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (blockRailBase.isBlockRail(worldIn.getBlockState(pos).getBlock())) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	//Keep for when doing textures - FT
//	@SideOnly(Side.CLIENT)
//    public void registerIcons(IIconRegister p_94581_1_)
//    {
//    	for(int i=0;i<texStr.length;++i)
//    	{
//    		this.itemIcons[i] = p_94581_1_.registerIcon(ERC_CONST.DOMAIN+":"+texStr[i]);
//    	}
//    	temIcon = itemIcons[0];
//    }
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIconFromDamage(int p_77617_1_)
//	{
//		return ERCwrench_getIcon();
//	}
//
//	@Override
//	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
//	{
//		return ERCwrench_getIcon();
//	}
//
//	@Override
//	public IIcon getIcon(ItemStack stack, int pass)
//	{
//		return ERCwrench_getIcon();
//	}
//
//    private IIcon ERCwrench_getIcon()
//    {
//    	int iconid=0;
//    	switch(mode)
//    	{
//    	case 0:/*connect*/ 	iconid = ERC_CoasterAndRailManager.isPlacedRail() ? 1 : 0; break;
//    	case 1:/*adjust*/	iconid = ERC_CoasterAndRailManager.isPlacedRail() ? 3 : 2; break;
//    	}
//
//		return this.itemIcons[iconid];
//    }
}

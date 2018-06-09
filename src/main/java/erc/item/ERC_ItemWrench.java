package erc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.util.*;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

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

	public ERC_ItemWrench()
	{
		super();
		this.addPropertyOverride(new ResourceLocation("mode"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return (float) mode;
			}
		});
		this.addPropertyOverride(new ResourceLocation("phase"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return ERC_CoasterAndRailManager.isPlacedRail() ? 1.0F : 0.0F;
			}
		});
	}
	
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
}

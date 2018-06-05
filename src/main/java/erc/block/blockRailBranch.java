package erc.block;

import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailBranch2;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class blockRailBranch extends blockRailBase{

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityRailBranch2();
	}

	@Override
	public boolean canProvidePower(IBlockState state)
	 {
		return true;
	}

	// �ԐΓ��͐���
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            boolean flag = world.isBlockIndirectlyGettingPowered(pos) != 0;
            boolean flag2 = block.canProvidePower(state);
            if (flag || flag2)
            {
            	TileEntityRailBranch2 rail = (TileEntityRailBranch2)world.getTileEntity(pos);
            	boolean tgle = rail.getToggleFlag();
            	
                if (flag && !tgle)
                {
                	rail.changeRail();
                	rail.changeToggleFlag();
                	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                	//Sound 1003?? - FT
                	world.playSound((EntityPlayer)null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F); //���ʉ��H
                }
                else if(!flag && tgle)
                {
                	rail.changeToggleFlag();
                }
            }
        }
    }
  
    // TileEntity�������@Branch�p���ꏈ���@���[���Q�Ƃ�������
    protected void onTileEntityInitFirst(World world, EntityLivingBase player, Wrap_TileEntityRail rail, int x, int y, int z)
	{
		// �u���b�N�ݒu���̃v���C���[�̌���
    	TileEntityRailBranch2 railb = (TileEntityRailBranch2) rail;
    	Vec3d metadir = ConvertVec3FromMeta(world.getBlockState(new BlockPos(x, y, z)).getValue(META));
    	Vec3d BaseDir = new Vec3d(
				-Math.sin(Math.toRadians(player.rotationYaw)) * (metadir.x!=0?0:1),
				Math.sin(Math.toRadians(player.rotationPitch)) * (metadir.y!=0?0:1),
				Math.cos(Math.toRadians(player.rotationYaw)) * (metadir.z!=0?0:1));
    	
    	railb.SetBaseRailPosition(x, y, z, BaseDir, metadir, 20f);
    	
    	int saveflag = railb.getNowRailFlag();
    	railb.changeRail(0);
    	for(int i=0; i<2; ++i)
    	{
    		railb.changeRail(i);
			double yaw = ((float)i-0.5) + Math.toRadians(player.rotationYaw);
			double pit = ((float)i-0.5) - Math.toRadians(player.rotationPitch);
			
			Vec3d vecDir = new Vec3d(
					-Math.sin(yaw) * (metadir.x!=0?0:1),
					Math.sin(pit) * (metadir.y!=0?0:1),
					Math.cos(yaw) * (metadir.z!=0?0:1) );
						
			// �V�K�ݒu�̃��[���ɑ΂��č��W�ݒ�B�@�����̓v���C���[�̌����Ă��������			
//			railb.SetNextRailPosition(x+(int)(vecDir.xCoord*10), y+(int)(vecDir.yCoord*10), z+(int)(vecDir.zCoord*10));
			railb.SetNextRailVectors(
					new Vec3d(x+(int)(vecDir.x*10)+0.5, y+(int)(vecDir.y*10)+0.5, z+(int)(vecDir.z*10)+0.5),
					vecDir, 
					railb.getRail().BaseRail.vecUp, 
					0f, 0f,
					railb.getRail().BaseRail.Power,
					-1, -1, -1);
    	}
    	railb.changeRail(saveflag);
	}
}

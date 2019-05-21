package erc.block;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc.manager.ERC_CoasterAndRailManager;
import erc.message.ERC_MessageConnectRailCtS;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class blockRailBase extends Block
{
	public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);
	private static final AxisAlignedBB[] boxes = new AxisAlignedBB[]
			{
					new AxisAlignedBB(0.2F, 0.7F, 0.2F, 0.8F, 1.0F, 0.8F),
					new AxisAlignedBB(0.2F, 0.0F, 0.2F, 0.8F, 0.3F, 0.8F),
					new AxisAlignedBB(0.2F, 0.2F, 0.7F, 0.8F, 0.8F, 1.0F),
					new AxisAlignedBB(0.2F, 0.2F, 0.0F, 0.8F, 0.8F, 0.3F),
					new AxisAlignedBB(0.7F, 0.2F, 0.2F, 1.0F, 0.8F, 0.8F),
					new AxisAlignedBB(0.0F, 0.2F, 0.2F, 0.3F, 0.8F, 0.8F),
					new AxisAlignedBB(0.4F, 0.4F, 0.4F, 0.6F, 0.6F, 0.6F),
					new AxisAlignedBB(0.4F, 0.4F, 0.4F, 0.6F, 0.6F, 0.6F)
			};
	
	public blockRailBase()
	{
		super(Material.GROUND);
		this.setHardness(0.3F);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setLightLevel(0.6F);//0.6
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, META);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(META);
	}
 
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return false;
    }

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
//		Wrap_TileEntityRail WPrevTile = ERC_BlockRailManager.GetPrevTileEntity(world);
//		Wrap_TileEntityRail WNextTile = ERC_BlockRailManager.GetNextTileEntity(world);
		Wrap_TileEntityRail tlRailTest = (Wrap_TileEntityRail) createTileEntity(world, state);
//		ERC_TileEntityRailTest tlRailTest = (ERC_TileEntityRailTest)world.getTileEntity(x, y, z);
		
		/**
		 * ////////////// �N���C�A���g��Manager�ɐݒ�ƃp�P�b�g���M
		*/
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if(world.isRemote) 
		{
			if(ERC_CoasterAndRailManager.isPlacedPrevRail())
			{
				// �O��u�������[��������΃T�[�o�[�ɕ񍐁E�A���v��
				ERC_MessageConnectRailCtS packet 
					= new ERC_MessageConnectRailCtS(
							ERC_CoasterAndRailManager.prevX, ERC_CoasterAndRailManager.prevY, ERC_CoasterAndRailManager.prevZ,
							x, y, z
							);
				ERC_PacketHandler.INSTANCE.sendToServer(packet);
			}
			
			// ���u�������[�����L�^
			ERC_CoasterAndRailManager.SetPrevData(x, y, z);

			if(ERC_CoasterAndRailManager.isPlacedNextRail())
			{
				// �O��폜�������[���̐�Ɏ��̃��[�����q�����Ă���΃T�[�o�[�ɕ񍐁E�A���v��
				ERC_MessageConnectRailCtS packet 
					= new ERC_MessageConnectRailCtS(
							x, y, z,
							ERC_CoasterAndRailManager.nextX, ERC_CoasterAndRailManager.nextY, ERC_CoasterAndRailManager.nextZ
							);
				ERC_PacketHandler.INSTANCE.sendToServer(packet);

				// ����ɐ�Ƀ��[������������̂ŁA�ۑ����[�����͍폜
				ERC_CoasterAndRailManager.ResetData();
		 	}
			
			return;
		}
		
		/**
		 * ////////////// �T�[�o�[�̓��[���ݒ�v�Z
		*/
		super.onBlockPlacedBy(world, pos, state, placer, stack); 
		//tlRailTest.myisInvalid(); // ?

		onTileEntityInitFirst(world, placer, tlRailTest, x, y, z);
		
		world.setTileEntity(pos, tlRailTest);
//		tlRailTest.onTileSetToWorld_Init();
		tlRailTest.syncData();
		
//		// �O���[�����ݒu����Ă�����A���̏������ɑO��ݒu�������[����TileEntity�̍��W�ݒ� ... 8/11:�N���C�A���g����̃p�P�b�g�������������
		// �R�[�h��ERC_MessageRailConnectRailCtS��

		// ���[����ݒu������BlockRailManager�ɓo�^
//		ERC_BlockRailManager.SetPrevData(x, y, z);
	}
	
	protected void onTileEntityInitFirst(World world, EntityLivingBase player, Wrap_TileEntityRail Wrail, int x, int y, int z)
	{
		TileEntityRailBase rail = Wrail.getRail();
		// �u���b�N�ݒu���̃v���C���[�̌���
		double yaw = Math.toRadians(player.rotationYaw);
		double pit = -Math.toRadians(player.rotationPitch);
		Vec3d metadir = ConvertVec3FromMeta(world.getBlockState(new BlockPos(x, y, z)).getValue(META));
		Vec3d vecDir = new Vec3d(
				-Math.sin(yaw) * (metadir.x !=0?0:1), 
				Math.sin(pit) * (metadir.y !=0?0:1), 
				Math.cos(yaw) * (metadir.z !=0?0:1) );
		
		// �V�K�ݒu�̃��[���ɑ΂��č��W�ݒ�B�@�����̓v���C���[�̌����Ă��������
		rail.SetBaseRailPosition(x, y, z, vecDir, metadir, 15f);
//		rail.SetNextRailPosition(x+(int)(vecDir.xCoord*10), y+(int)(vecDir.yCoord*10), z+(int)(vecDir.zCoord*10));
		rail.SetNextRailVectors(
				//new Vec3d(x+(int)(vecDir.x*10)+0.5, y+(int)(vecDir.y*10)+0.5, z+(int)(vecDir.z*10)+0.5), // Seemed to cause rails to not be straight when placed -FT
				new Vec3d(x+(vecDir.x*10)+0.5, y+(vecDir.y*10)+0.5, z+(vecDir.z*10)+0.5),
				vecDir, 
				rail.getRail().BaseRail.vecUp, 
				0f, 0f,
				rail.getRail().BaseRail.Power,
				-1, -1, -1);
		rail.CalcRailLength(); 
		rail.Init();
	}

	// �T�[�o�[�̂݁@Manager�̓o�^���ύX onBlockDestroyByPlayer����ɌĂ΂��B�@����super��TileEntity���폜���Ă��邽�߁A��������TileEntity����O���[���̏��擾�AManager�̓o�^���ύX
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntityRailBase thistl = ((Wrap_TileEntityRail)(world.getTileEntity(pos))).getRail();
		if(thistl!=null)
		{
			thistl.setBreak(true);
			Wrap_TileEntityRail prev = thistl.getPrevRailTileEntity();
			if(prev!=null)prev.getRail().NextRail.SetPos(-1, -1, -1);
			Wrap_TileEntityRail next = thistl.getNextRailTileEntity();
			if(next!=null)next.getRail().BaseRail.SetPos(-1, -1, -1);
		}
		super.breakBlock(world, pos, state);
	}

//	@Override
//	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int p_149636_6_) 
//	{	
////		ERC_MessageSaveBreakRailStC packet = new ERC_MessageSaveBreakRailStC(
////				ERC_BlockRailManager.prevX, ERC_BlockRailManager.prevY, ERC_BlockRailManager.prevZ,
////				ERC_BlockRailManager.nextX, ERC_BlockRailManager.nextY, ERC_BlockRailManager.nextZ
////				);
////		ERC_PacketHandler.INSTANCE.sendTo(packet,(EntityPlayerMP) player);
//		super.harvestBlock(world, player, x, y, z, p_149636_6_);
//	}

	//	// �u���b�N���j�󂳂ꂽ��Ă΂��@
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int i)
	{	
//		if(!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
//			this.dropBlockAsItem(world, x, y, z, new ItemStack(this));
	}

	//�����_�[�Ŏg������g��Ȃ�������
	public void setBlockBoundsForItemRender()
	{
		//this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.4F, 1.0F);
	}
 
	//�����蔻��B�T�{�e����\�E���T���h���Q�l�ɂ���Ɨǂ��B�R�R�̐ݒ������ƁAonEntityCollidedWithBlock���Ă΂��悤�ɂȂ�
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return boxes[state.getValue(META) & 7];
	}

//	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
//    {
//        if (flag || flag1)
//        {
//            this.setBlockBounds(
//            		((double)x)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
//    				((double)x)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
//    				);
//            super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
//        }
//        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
//    }
 
	//�u���b�N�Ɏ��_�����킹�����ɏo�Ă��鍕�����̃A��
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
	{
		return boxes[state.getValue(META) & 7];
	}
	
	public static boolean isBlockRail(Block block) {
		return block instanceof blockRailBase;
	}
    
    protected Vec3d ConvertVec3FromMeta(int meta)
    {
    	switch(meta&7){
    	case 0:return new Vec3d(0, -1, 0);
    	case 1:return new Vec3d(0, 1, 0);
    	case 2:return new Vec3d(0, 0, -1);
    	case 3:return new Vec3d(0, 0, 1);
    	case 4:return new Vec3d(-1, 0, 0);
    	case 5:return new Vec3d(1, 0, 0);
    	}
		return new Vec3d(0, 0, 0);
    }

	@Override
	public boolean hasTileEntity(IBlockState ibs)
	{
		return true;
	}

	@Override
	public abstract TileEntity createTileEntity(World world, IBlockState state);

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * Gets the {@link IBlockState} to place
	 * @param world The world the block is being placed in
	 * @param pos The position the block is being placed at
	 * @param facing The side the block is being placed on
	 * @param hitX The X coordinate of the hit vector
	 * @param hitY The Y coordinate of the hit vector
	 * @param hitZ The Z coordinate of the hit vector
	 * @param meta The metadata of {@link ItemStack} as processed by {@link Item#getMetadata(int)}
	 * @param placer The entity placing the block
	 * @param hand The player hand used to place this block
	 * @return The state to be placed in the world
	 */
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		switch(facing) {
			case DOWN:
				return this.getDefaultState().withProperty(META, 0);
			case UP:
				return this.getDefaultState().withProperty(META, 1);
			//May be nonsense
			case NORTH:
				return this.getDefaultState().withProperty(META, 2);
			case SOUTH:
				return this.getDefaultState().withProperty(META, 3);
			case WEST:
				return this.getDefaultState().withProperty(META, 4);
			case EAST:
				return this.getDefaultState().withProperty(META, 5);
			default:
				return this.getDefaultState().withProperty(META, 6);
		}
	}
}

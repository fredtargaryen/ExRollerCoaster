package erc.tileEntity;

import java.util.Iterator;

import net.minecraftforge.fml.common.FMLCommonHandler;
import erc._core.ERC_Logger;
import erc._core.ERC_ReturnCoasterRot;
import erc.entity.ERC_EntityCoaster;
import erc.gui.GUIRail;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ModelLoadManager;
import erc.math.ERC_MathHelper;
import erc.message.ERC_MessageRailStC;
import erc.message.ERC_PacketHandler;
import erc.model.ERC_ModelDefaultRail;
import erc.model.Wrap_RailRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static erc.block.blockRailBase.META;

public abstract class TileEntityRailBase extends Wrap_TileEntityRail{
	public Wrap_RailRenderer modelrail; //test
	public int modelrailindex;
	//base
	public DataTileEntityRail BaseRail;
	//next
	public DataTileEntityRail NextRail;
	// �R�[�X�^�[��
	public float Length;
	// �`��֘A�p�����^
	protected int PosNum = 15;
//	public int VertexNum = PosNum*4;
//	Vec3d posArray[] = new Vec3[VertexNum];
	public boolean doesDrawGUIRotaArraw;
	public ResourceLocation RailTexture;
	
	public float fixedParamTTable[] = new float[PosNum];
		
	protected boolean isBreak;
	
	public TileEntityRailBase()
	{
		super();
//		for(int i = 0; i<VertexNum; ++i) posArray[i] = new Vec3d(0.0, 0.0, 0.0);
		for(int i = 0; i<PosNum; ++i) fixedParamTTable[i] =0;
		
		BaseRail = new DataTileEntityRail();
		NextRail = new DataTileEntityRail();

		Length = 1f;
		isBreak = false;
		
		RailTexture = new ResourceLocation("textures/blocks/iron_block.png");
		modelrail = new ERC_ModelDefaultRail();
//		modelrail = ERC_ModelLoadManager.getRailModel(2, 0);
	}
	
	public void Init()
	{
		BaseRail.SetPos(-1, -1, -1);
		NextRail.SetPos(-1, -1, -1);
	}
	
	public World getworld(){return world;}
	public int getXcoord(){return this.pos.getX();}
	public int getYcoord(){return this.pos.getY();}
	public int getZcoord(){return this.pos.getZ();}
	public TileEntityRailBase getRail(){return this;}
	
	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 100000d;
	}

	//@Override
	public boolean myisInvalid() 
	{
		return super.isInvalid();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}
	
	private void SetPrevRailPosition(int x, int y, int z)
	{
		if(x==this.getXcoord() && y==this.getYcoord() && z==this.getZcoord())
		{
			BaseRail.cx=-1;	BaseRail.cy=-1;	BaseRail.cz=-1;
			ERC_Logger.warn("TileEntityRail SetPrevRailPosition : connect oneself");
			return;
		}
		BaseRail.cx = x; BaseRail.cy = y; BaseRail.cz = z;
	}
//	private void SetNextRailPosition(int x, int y, int z)
//	{
//		if(x==xCoord && y==yCoord && z==zCoord)
//		{
//			NextRail.cx=-1;	NextRail.cy=-1;	NextRail.cz=-1;
//			ERC_Logger.warn("TileEntityRail SetNextRailPosition : connect oneself");
//			return;
//		}
//		NextRail.cx = x; NextRail.cy = y; NextRail.cz = z;
//	}

	public Wrap_TileEntityRail getPrevRailTileEntity()
	{
		return (Wrap_TileEntityRail) world.getTileEntity(new BlockPos(BaseRail.cx, BaseRail.cy, BaseRail.cz));
	}
	public Wrap_TileEntityRail getNextRailTileEntity()
	{
		return (Wrap_TileEntityRail) world.getTileEntity(new BlockPos(NextRail.cx, NextRail.cy, NextRail.cz));
	}
	
	public void SetPosNum(int num)
	{
		this.PosNum = num;
		fixedParamTTable = new float[PosNum];
		for(int i = 0; i<PosNum; ++i) fixedParamTTable[i] = 0;
//		CreateNewRailVertexFromControlPoint();
	}
	
	public int GetPosNum(){return this.PosNum;}
	
	public boolean isBreak(){return isBreak;}
	public void setBreak(boolean flag){isBreak = flag;}
	
	@Override
	public void invalidate() {
		// �u���b�N�폜�̐ڑ����[���ێ��e�X�g
		if(world.isRemote)
		{
			double dist = Minecraft.getMinecraft().player.getDistance(this.getXcoord()+0.5, this.getYcoord(), this.getZcoord()+0.5);
			if(6d > dist)
			{
				ERC_CoasterAndRailManager.SetPrevData(BaseRail.cx, BaseRail.cy, BaseRail.cz);
				ERC_CoasterAndRailManager.SetNextData(NextRail.cx, NextRail.cy, NextRail.cz);
			}
		}
		super.invalidate();
	}

	public void SetRailDataFromMessage(ERC_MessageRailStC msg)
	{
    	this.SetPosNum(msg.posnum);	
		/////\\\\\
		Iterator<DataTileEntityRail> it = msg.raillist.iterator();
		// Base
		DataTileEntityRail e = it.next();
		SetBaseRailVectors(e.vecPos, e.vecDir, e.vecUp, e.Power);
		SetBaseRailfUpTwist(e.fUp, e.fDirTwist);
		SetPrevRailPosition(e.cx, e.cy, e.cz);
		// Next
		e = it.next();
		SetNextRailVectors(e.vecPos, e.vecDir, e.vecUp, e.fUp, e.fDirTwist, e.Power, e.cx, e.cy, e.cz);
		/////\\\\\
//    	this.CreateNewRailVertexFromControlPoint();
    	this.CalcRailPosition();
	}
	
	public void SetBaseRailPosition(int x, int y, int z, Vec3d BaseDir, Vec3d up, float power)
	{
		//�@next_n�F�V�����ݒu���ꂽ�u���b�N�̍��W  prev_n�F�O��ݒu�����u���b�N�̍��W
		BaseRail.vecPos = new Vec3d((double)x + 0.5, (double)y + 0.5, (double)z + 0.5);
		SetBaseRailVectors(BaseRail.vecPos, BaseDir, up, power);
	}
	
	public void SetBaseRailVectors(Vec3d posBase, Vec3d dirBase, Vec3d vecup, float power)
	{
		BaseRail.vecPos = new Vec3d(posBase.x, posBase.y, posBase.z);
		BaseRail.vecDir = new Vec3d(dirBase.x, dirBase.y, dirBase.z);
		BaseRail.vecUp = new Vec3d(vecup.x, vecup.y, vecup.z);
		BaseRail.Power = power;
	}
	
	public void SetBaseRailfUpTwist(float up, float twist)
	{
		BaseRail.fUp = up;
		BaseRail.fDirTwist = twist;
	}
	
	public void SetNextRailVectors(TileEntityRailBase nexttile)
	{
		SetNextRailVectors(nexttile.BaseRail, nexttile.getXcoord(), nexttile.getYcoord(), nexttile.getZcoord());//vecBase, nexttile.dirBase, nexttile.vecUp, nexttile.fUp, nexttile.fDirTwist, nexttile.Power);
	}
	public void SetNextRailVectors(DataTileEntityRail rail, int x, int y, int z)
	{
		SetNextRailVectors(rail.vecPos, rail.vecDir, rail.vecUp, rail.fUp, rail.fDirTwist, rail.Power, x, y, z);
	}
	public void SetNextRailVectors(Vec3d vecNext, Vec3d vecDir, Vec3d vecUp, float fUp, float fDirTwist, float Power, int cx, int cy, int cz) {
		this.NextRail.SetData(vecNext, vecDir, vecUp, fUp, fDirTwist, Power, cx, cy, cz);
	}
	
//	public ERC_TileEntityRailBase getOwnRailData()
//	{
//		return this;
//	}
	
	public void AddControlPoint(int n)
	{
		n = n*2-1;
		if( this.PosNum+n > 50) SetPosNum(50);
		else if( this.PosNum+n < 2) SetPosNum(2);
		else SetPosNum(this.PosNum + n);
//		CreateNewRailVertexFromControlPoint();
	}

//	public void CreateNewRailVertexFromControlPoint()
//	{
//		this.VertexNum = this.PosNum*4;
//		posArray = new Vec3[VertexNum];
//		for(int i = 0; i<VertexNum; ++i) posArray[i] = new Vec3d(0.0, 0.0, 0.0);
//		CalcRailPosition();
//	}
	
	public void AddPower(int idx)
	{
		float f=0;
		switch(idx)
		{
		case 0 : f = -1.0f; break;
		case 1 : f = -0.1f; break;
		case 2 : f =  0.1f; break;
		case 3 : f =  1.0f; break;
		}
		if( BaseRail.Power+f > 100f) BaseRail.Power = 100f;
		else if( BaseRail.Power+f < 0.1f) BaseRail.Power = 0.1f;
		else BaseRail.Power += f;
//		CreateNewRailVertexFromControlPoint(); //->	CalcRailPosition();
//		CalcPrevRailPosition();
	}
	
	public void UpdateDirection(GUIRail.editFlag flag, int idx)
	{
		float rot=0;
		switch(idx)
		{
		case 0 : rot = -0.5f; break;
		case 1 : rot = -0.05f; break;
		case 2 : rot =  0.05f; break;
		case 3 : rot =  0.5f; break;
		}
		int meta = world.getBlockState(new BlockPos((int)this.getXcoord(), (int)this.getYcoord(), (int)this.getZcoord())).getValue(META);
		switch(flag)
		{
		case ROTRED : // ���[���ݒu�ʂɑ΂��Đ�����]�@��������
			ConvertVec3FromMeta(meta&7, BaseRail.vecDir, rot); break;
		case ROTGREEN : // ���z�����@fUp��������
			BaseRail.addFUp(rot); break;
		case ROTBLUE : // �Ђ˂�ǉ�
			BaseRail.addTwist(rot); break;
		default:
			break;
		}
	}
	 private Vec3d ConvertVec3FromMeta(int meta, Vec3d dir, float rot)
    {
    	switch(meta){
    	case 0:
    	case 1: return this.rotateAroundY(dir, -rot);
    	case 2:
    	case 3: return this.rotateAroundZ(dir, -rot);
    	case 4:
    	case 5: return this.rotateAroundX(dir, -rot);
			default:
				return dir;
    	}
    }
	
	public void ResetRot()
	{
		BaseRail.resetRot();
	}
	 
	public void Smoothing()
	{
		if(isConnectRail_prev1_next2());
		// Prev��Next�ւ̂��ꂼ��̃x�N�g����Normalize��Sub�����g��DirBase��
		// Prev��Next�ǂ��炩�̃��[���������ꍇ�͖���
		Wrap_TileEntityRail prevtl = BaseRail.getConnectionTileRail(world);
		Wrap_TileEntityRail nexttl = NextRail.getConnectionTileRail(world);
		if(prevtl == null) return;
		if(nexttl == null) return;
		
		//Smoothing��this.vecBase����Prev.vecBase��Next.vecBase�ւ̂��ꂼ��̃x�N�g����Normalize�Fn'-p'��Base.dirBase
//		Vec3d n = BaseRail.vecPos.subtract(nexttl.getRail().BaseRail.vecPos).normalize();
//		Vec3d p = BaseRail.vecPos.subtract(prevtl.getRail().BaseRail.vecPos).normalize();
		Vec3d n = nexttl.getRail().BaseRail.vecPos;
		Vec3d p = prevtl.getRail().BaseRail.vecPos;
		Vec3d tempDir = n.subtract(p).normalize(); //FT p.subtract(n) made smoothing go all weird
		BaseRail.vecDir = tempDir.normalize();
		switch(world.getBlockState(new BlockPos(this.getXcoord(), this.getYcoord(), this.getZcoord())).getValue(META) & 7)
		{
		case 0: case 1: // �㉺	y
			BaseRail.vecDir = new Vec3d(BaseRail.vecDir.x, 0, BaseRail.vecDir.z);
			BaseRail.fUp = (tempDir.y>0?-1:1) * (float) (tempDir.y / Math.sqrt(tempDir.x*tempDir.x+tempDir.z*tempDir.z));
			break;
		case 2: case 3: // ��k	z
			BaseRail.vecDir = new Vec3d(BaseRail.vecDir.x, BaseRail.vecDir.y, 0);
			BaseRail.fUp = (tempDir.z>0?-1:1) * (float) (tempDir.z / Math.sqrt(tempDir.x*tempDir.x+tempDir.y*tempDir.y));
			break;
		case 4: case 5: // ����	x
			BaseRail.vecDir = new Vec3d(0, BaseRail.vecDir.y, BaseRail.vecDir.z);
			BaseRail.fUp = (tempDir.x>0?-1:1) * (float) (tempDir.x / Math.sqrt(tempDir.y*tempDir.y+tempDir.z*tempDir.z));
			break;
		}
		BaseRail.vecDir = BaseRail.vecDir.normalize();
		
		BaseRail.Power = (float) p.subtract(n).lengthVector()/2;
//		BaseRail.Power = ERC_MathHelper.CalcSmoothRailPower(
//    			BaseRail.vecDir, nexttl.getRail().BaseRail.vecDir, 
//    			BaseRail.vecPos, nexttl.getRail().BaseRail.vecPos
//    			);
		CalcRailPosition();
		prevtl.getRail().SetNextRailVectors(this);
		prevtl.getRail().CalcRailPosition();
		BaseRail.Power = (float) p.subtract(n).lengthVector()/2;
//		BaseRail.Power = ERC_MathHelper.CalcSmoothRailPower(BaseRail.vecDir, NextRail.vecDir, BaseRail.vecPos, NextRail.vecPos);

//		prevtl.SetNextRailPosition(vecBase, dirBase, vecUp, Power);
	}
	
	public boolean isConnectRail_prev1_next2()
	{
		return false;
//		if(getPrevRailTileEntity()==null)return false;
//		else
//		{
//			Wrap_TileEntityRail next1 = getNextRailTileEntity();
//			if(next1==null)return false;
//			if(next1.getNextRailTileEntity() == null)return false;
//			return true;
//		}
	}
	
	public void SmoothingSpecial()
	{
		Smoothing();
	}
	
	public void CalcRailPosition()
	{	
//		if(!world.isRemote)return;
		
		////pos
		Vec3d Base = new Vec3d(BaseRail.vecUp.x * 0.5, BaseRail.vecUp.y * 0.5, BaseRail.vecUp.z * 0.5);
		Vec3d Next = NextRail.vecPos.subtract(BaseRail.vecPos); //FT Used to be Base subtract Next but that made things go backwards
		Next = new Vec3d(Next.x + NextRail.vecUp.x * 0.5, Next.y + NextRail.vecUp.y * 0.5, Next.z + NextRail.vecUp.z * 0.5);
		
		////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
		Vec3d DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);
		Vec3d DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);
		
		////pair of rail Vertex
//		Vec3d vecpitch1 = vecUp.crossProduct(dirBase).normalize();
//		Vec3d vecpitch2 = vecNextUp.crossProduct(dirNext).normalize();
		Vec3d vecUp_1 = BaseRail.CalcVec3UpTwist();
		Vec3d vecUp_2 = NextRail.CalcVec3UpTwist();

		// �X�v���C���Ȑ������v�Z����
		Length = 0;
		Vec3d tempPrev = null;
		fixedParamTTable[0] = 0;
		if(modelrail!=null)modelrail.setModelNum(PosNum);
		
		for(int i = 0; i<PosNum; ++i)
		{
//			int j = i*4; // VertexIndex
			float f = (float)i/(float)(PosNum-1);
			
			////spline
			Vec3d center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
			if(i>0)
			{
				Length += center.distanceTo(tempPrev);
				fixedParamTTable[i] = Length;
			}
			tempPrev = center;
		}
		
		calcFixedParamT();//////////////////////////////////////////////////////////////////////////
		
		float ModelLen = Length/(PosNum-1);
		
		for(int i = 0; i<PosNum; ++i){
			
			float f = (float)i/(float)(PosNum-1);
			////fixed spline
//			float lT;
			int T = (int)Math.floor(f * (PosNum-1));
//			if(PosNum-1 <= T) lT = fixedParamTTable[T];
			f = fixedParamTTable[T];
//			else lT = ERC_MathHelper.Lerp(f-(T/(float)(PosNum-1)), fixedParamTTable[T], fixedParamTTable[T+1]);
			Vec3d center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
//			ERC_Logger.info("f-t:"+(f-(T/(float)(PosNum-1))));
					
			Vec3d dir1;
			// ����_�����Ԓn�_�ł���ΑO��̃x�N�g���A�����b�_�ł���ΐ�������x�N�g����p����
			if(f <= 0.01f)
			{
				dir1 = DirxPowb.normalize();
			}
			if(f >= 0.99f)
			{
				dir1 = DirxPown.normalize();
			}
			else
			{
					 dir1 = ERC_MathHelper.Spline((f+0.01f), Base, Next, DirxPowb, DirxPown);
				Vec3d dir2 = ERC_MathHelper.Spline((f-0.01f), Base, Next, DirxPowb, DirxPown);
				dir1 = dir2.subtract(dir1); // dir1 - dir2
			}
			
			////pair of rail Vertex
			Vec3d up = ERC_MathHelper.Slerp(f, vecUp_1, vecUp_2).normalize();
			Vec3d cross = up.crossProduct(dir1);
			cross = cross.normalize().normalize();

			
//			if(j>=posArray.length)
//			{
//				ERC_Logger.warn("index exception");
//				return;
//			}
			if(i == PosNum - 1) cross = new Vec3d(-cross.x, -cross.y, -cross.z);
			if(modelrail!=null)modelrail.construct(i, center, dir1, cross, ModelLen);
//			// ��
//			posArray[j  ].xCoord = center.xCoord - cross.xCoord*t1;
//			posArray[j  ].yCoord = center.yCoord - cross.yCoord*t1;
//			posArray[j  ].zCoord = center.zCoord - cross.zCoord*t1;
//			posArray[j+1].xCoord = center.xCoord - cross.xCoord*t2;
//			posArray[j+1].yCoord = center.yCoord - cross.yCoord*t2;
//			posArray[j+1].zCoord = center.zCoord - cross.zCoord*t2;
//			// �E 
//			posArray[j+2].xCoord = center.xCoord + cross.xCoord*t2;
//			posArray[j+2].yCoord = center.yCoord + cross.yCoord*t2;
//			posArray[j+2].zCoord = center.zCoord + cross.zCoord*t2;
//			posArray[j+3].xCoord = center.xCoord + cross.xCoord*t1;
//			posArray[j+3].yCoord = center.yCoord + cross.yCoord*t1;
//			posArray[j+3].zCoord = center.zCoord + cross.zCoord*t1;
			
//			// �ʒu
//			posArray[j  ] = center;
//			// �p�x
//			Vec3d crossHorz = new Vec3d(0, 1, 0).crossProduct(dir1);
//			Vec3d dir_horz = new Vec3d(dir1.xCoord, 0, dir1.zCoord);
//			posArray[j+1].xCoord = -Math.toDegrees( Math.atan2(dir1.xCoord, dir1.zCoord) );
//			posArray[j+1].yCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir1, dir_horz) * (dir1.yCoord>0?-1f:1f) );
//			posArray[j+1].zCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(cross, crossHorz) * (cross.yCoord>0?1f:-1f) );
//			// ����
//			if(i!=PosNum-1)posArray[j+2].xCoord = (fixedParamTTable[i+1]-fixedParamTTable[i])*Length;

		
		}
//		calcFixedParamT();
	}
	
	
	protected void calcFixedParamT()
	{
		///////////// fixedParamT�C��
		
		// [0,1]��PosNum�����̊Ԋu�����̌v�Z
		float div = Length / (float)(PosNum-1);
//		float divT = 1.0f / (float)PosNum;
		float tempFixed[] = new float[PosNum];
		// ���`��Ԃ�div�̈ʒu��T��
		int I=1;
		for(int i=1; i<PosNum; ++i)
		{
//			ERC_Logger.info("i I div:"+i+" "+I+" "+div*i/Length
//					+ "  fixedParamTTable[I]"+fixedParamTTable[I]/Length
//					+"  fixedParamTTable[I-1]"+fixedParamTTable[I-1]/Length);
			if(div*i < fixedParamTTable[I] && div*i >= fixedParamTTable[I-1])
			{
				float divnum = PosNum - 1f;
				float t = (div*i - fixedParamTTable[I-1]) / (fixedParamTTable[I] - fixedParamTTable[I-1]);
				tempFixed[i] = (I-1)/divnum + t*(1f/divnum);
//				ERC_Logger.info("tempfix[i]:"+tempFixed[i]);
			}
			else 
			{
				if(I<PosNum-1)
				{
					++I;
					--i;
				}
				else
				{
					tempFixed[i] = 1.0f;
				}
			}
		}
		tempFixed[PosNum-1] = 1.0f;
		fixedParamTTable = tempFixed;
//        ERC_Logger.info(""+fixedParamTTable[3]);
	}
	
	// �R�[�X�^�[�̍��W�X�V�ƃv���C���[�J������]�p	�߂�l�̓��[���̌X��
	public double CalcRailPosition2(float t, ERC_ReturnCoasterRot ret, float viewyaw, float viewpitch, boolean riddenflag)
	{	
		//////////////�R�[�X�^�[����v�Z
		
		//FT positions of *rails*, relative to the base (current) rail *block*
		Vec3d Base = new Vec3d(BaseRail.vecUp.x, BaseRail.vecUp.y, BaseRail.vecUp.z).scale(0.5);
		//FT My fix - seems to work
		Vec3d Next = NextRail.vecPos.subtract(BaseRail.vecPos).add(NextRail.vecUp.scale(0.5));
		//FTMotty's original code said
		//FTVec3d Next = BaseRail.vecPos.subtract(NextRail.vecPos).add(NextRail.vecUp.scale(0.5));
		//FTNext = new Vec3d(NextRail.vecUp.x * 1.5, NextRail.vecUp.y * 1.5, NextRail.vecUp.z * 1.5);

		////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
		Vec3d DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);  
		Vec3d DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);  
		
		////pair of rail Vertex
		Vec3d vecUp_1 = BaseRail.CalcVec3UpTwist();
		Vec3d vecUp_2 = NextRail.CalcVec3UpTwist();
	    
		////spline
		float lT=0f;
		if(t < 0) {
			ERC_Logger.warn("tileentityrailbase.calcposition2 : paramT is smaller than 0");
			t = 0;
		}
		int T = (int)Math.floor(t * (PosNum-1));
		if(PosNum-1 <= T) lT = fixedParamTTable[PosNum-1];
		else lT = ERC_MathHelper.Lerp(t*(PosNum-1)-T, fixedParamTTable[T], fixedParamTTable[T+1]);
		t = lT;
		
		ret.Pos = ERC_MathHelper.Spline(t, Base, Next, DirxPowb, DirxPown);

		Vec3d dir1 = null;
		// ����_�����Ԓn�_�ł���ΑO��̃x�N�g���A�����b�_�ł���ΐ�������x�N�g����p����
		if(t <= 0.01f)
		{
//			dir1 = new Vec3d(BaseRail.vecDir.xCoord, BaseRail.vecDir.yCoord, BaseRail.vecDir.zCoord);
			dir1 = DirxPowb.normalize();
		}
		else if(t >= 0.99f)
		{
//			dir1 = new Vec3d(NextRail.vecDir.xCoord, NextRail.vecDir.yCoord, NextRail.vecDir.zCoord);
			dir1 = DirxPown.normalize();
		}
		else
		{
			dir1 = ERC_MathHelper.Spline((t+0.01f), Base, Next, DirxPowb, DirxPown);
			Vec3d dir2 = ERC_MathHelper.Spline((t-0.01f), Base, Next, DirxPowb, DirxPown);
			//FTMAY BE WRONG BUT I DON'T THINK SO
			//dir1 = dir2.subtract(dir1).normalize(); // dir1 - dir2
			dir1 = dir1.subtract(dir2).normalize(); //dir1 - dir2
		}
		
		////pair of rail Vertex
		Vec3d up = ERC_MathHelper.Slerp(t, vecUp_1, vecUp_2).normalize();

		Vec3d cross = up.crossProduct(dir1).normalize();

//		ERC_MathHelper.CalcCoasterRollMatrix(ret, ret.Pos, dir1, up);

		ret.Pos = ret.Pos.addVector(this.getXcoord() + 0.5, this.getYcoord() + 0.5, this.getZcoord() + 0.5);//+ coords of up
		
		ret.Dir = dir1;
		ret.Pitch = cross;
		//////// �v���C���[���WOffset�v�Z
		Vec3d fixUp = dir1.crossProduct(cross);
		ret.offsetX = cross;
		ret.offsetY = fixUp;
		ret.offsetZ = dir1;
		ret.up = fixUp;
		
		
//		if(riddenflag)
		{
			////////////// �v���C���[��]�ʌv�Z
			
			/* memo
			 * ��Vec�E�E�E dir1,up,cross
			 * ���_��]��E�E�E dir_rotView
			 */
			
			// ViewYaw��]�x�N�g���@dir1->dir_rotView, cross->turnCross
			Vec3d dir_rotView = ERC_MathHelper.rotateAroundVector(dir1, fixUp, Math.toRadians(viewyaw));
			Vec3d turnCross = ERC_MathHelper.rotateAroundVector(cross, fixUp, Math.toRadians(viewyaw));
			// ViewPitch��]�x�N�g�� dir1->dir_rotView
			Vec3d dir_rotViewPitch = ERC_MathHelper.rotateAroundVector(dir_rotView, turnCross, Math.toRadians(viewpitch));
			// pitch�p dir_rotViewPitch�̐����x�N�g��
//			Vec3d dir_rotViewPitchHorz = new Vec3d(dir_rotViewPitch.xCoord, 0, dir_rotViewPitch.zCoord);
			// roll�pturnCross�̐����x�N�g���@�e�X�g
			Vec3d crossHorz = new Vec3d(0, 1, 0).crossProduct(dir1);
			if(crossHorz.lengthVector()==0.0)crossHorz=new Vec3d(1, 0, 0);
			Vec3d crossHorzFix = new Vec3d(0, 1, 0).crossProduct(dir_rotViewPitch);
			if(crossHorzFix.lengthVector()==0.0)crossHorzFix=new Vec3d(1, 0, 0);
			
			Vec3d dir_horz = new Vec3d(dir1.x, 0, dir1.z);
			if(dir_horz.lengthVector()==0.0)dir_horz=fixUp;
//			Vec3d dir_WorldUp = new Vec3d(0, 1, 0);
			
			// yaw OK
			ret.yaw = (float) -Math.toDegrees( Math.atan2(dir1.x, dir1.z) );
//			ret.viewYaw = (float) -Math.toDegrees( Math.atan2(dir_rotViewPitch.xCoord, dir_rotViewPitch.zCoord) );

			// pitch OK
			ret.pitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir1, dir_horz) * (dir1.y>=0?-1f:1f) );
//			ret.viewPitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.yCoord>=0?-1f:1f) );
//			if(Float.isNaN(ret.viewPitch))
//				ret.viewPitch=0;
			
			// roll
			ret.roll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(cross, crossHorz) * (cross.y>=0?1f:-1f) );
//			ret.viewRoll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.yCoord>=0?1f:-1f) );
//			if(Float.isNaN(ret.viewRoll))
//				ret.viewRoll=0;
		}
		
		return -dir1.normalize().y;
	}
	
	public float CalcRailLength()
	{	
		////pos
		Vec3d Base = new Vec3d(BaseRail.vecUp.x, BaseRail.vecUp.y, BaseRail.vecUp.z).scale(0.5);
		Vec3d Next = NextRail.vecPos.subtract(BaseRail.vecPos).add(NextRail.vecUp.scale(0.5));
		
		////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
		Vec3d DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);  
		Vec3d DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);  

		// �X�v���C���Ȑ������v�Z����
		Length = 0;
		Vec3d tempPrev = Base;
		fixedParamTTable[0]=0;
		
		for(int i = 0; i<PosNum; ++i)
		{
//			int j = i*4; // VertexIndex
			float f = (float)i/(float)(PosNum-1);
			
			////spline
			Vec3d center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
			if(i>0)
			{
				Length += center.distanceTo(tempPrev);
				fixedParamTTable[i] = Length;
			}
			tempPrev = center;
		}
		
		calcFixedParamT();
		return Length;
	}
	
	public void CalcPrevRailPosition()
	{
		Wrap_TileEntityRail Wprevtile = BaseRail.getConnectionTileRail(world);
		if(Wprevtile == null)
		{
			return;
		}
		TileEntityRailBase prevtile = Wprevtile.getRail();
		prevtile.SetNextRailVectors(this);
//		prevtile.CreateNewRailVertexFromControlPoint();
//		prevtile.CalcRailPosition();
	}
	
	public abstract void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster);

	public void onCoasterEntry(ERC_EntityCoaster coaster) {}
	public void onPassedCoaster(ERC_EntityCoaster EntityCoaster){}
	public void onApproachingCoaster(){}
	public void onDeleteCoaster(){}
//	public void onTileSetToWorld_Init(){}
	
	// ���ꃌ�[���pGUI����֐�
	public void SpecialGUIInit(GUIRail gui){}
	public void SpecialGUISetData(int flag){}
	public String SpecialGUIDrawString(){return "";}
	
	// �ėp���[�����b�Z�[�W�p�̃f�[�^�ǂݏ����֐�
	public void setDataToByteMessage(ByteBuf buf){}
	public void getDataFromByteMessage(ByteBuf buf){}
	
	public ResourceLocation getDrawTexture()
	{
		return this.RailTexture;
	}
	
	public void render(Tessellator tess)
	{
		modelrail.render(tess);	
	}
	
	public void changeRailModelRenderer(int index) //TODO
	{
		modelrailindex = index;
		
//		if(world==null)return;
//		if(world.isRemote)
		if(FMLCommonHandler.instance().getSide().isClient())
		{
			modelrail = ERC_ModelLoadManager.createRailRenderer(index, this);
			modelrail.setModelNum(PosNum);
			CalcRailPosition();
		}
	}
	
	// NBT�̓ǂݎ��B
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);      
    	loadFromNBT(par1NBTTagCompound, "");
    }

    public void loadFromNBT(NBTTagCompound nbt, String tag)
    {
    	//For some reason this TileEntity receives two NBTTagCompounds to read from.
		//The first contains all the necessary info.
		//The second only has the x, y and z positions.
		//Updating from the first compound is fine, but when updating from the second, there is no posnum tag so instead
		//of throwing, 0 is returned.
		//Unfortunately posnum controls the size of an array, which is subsequently indexed into, so a posnum of 0
		//cannot be allowed. I have added a workaround which checks if the posnum tag exists, and only continues if it
		//does, in case the second packet has some usage I'm not aware of.
		if(nbt.hasKey("posnum")) {
			SetPosNum(nbt.getInteger(tag + "posnum"));
			//    	this.VertexNum = PosNum*4;

			readRailNBT(nbt, BaseRail, tag + "");
			readRailNBT(nbt, NextRail, tag + "n");

			modelrailindex = nbt.getInteger(tag + "railmodelindex");
			changeRailModelRenderer(modelrailindex);
			//        this.CreateNewRailVertexFromControlPoint();
			//        if(world.isRemote)
			this.CalcRailPosition();
			//        else this.CalcRailLength();
		}
    }

    // ���[���̏��1���ǂݎ��p
    protected void readRailNBT(NBTTagCompound nbt, DataTileEntityRail rail, String tag)
    {
    	rail.vecPos = readVec3(nbt, tag+"pos");
    	rail.vecDir = readVec3(nbt, tag+"dir");
        rail.vecUp = readVec3(nbt, tag+"up");
        
        rail.fUp 		= nbt.getFloat(tag+"fup");
        rail.fDirTwist 	= nbt.getFloat(tag+"fdt");
        rail.Power 		= nbt.getFloat(tag+"pow");
        
        rail.cx = nbt.getInteger(tag+"cx"); 
        rail.cy = nbt.getInteger(tag+"cy"); 
        rail.cz = nbt.getInteger(tag+"cz"); 
        
     // �������g�Ɍq����̂�h��           
        if(rail.cx==this.getXcoord() && rail.cy==this.getYcoord() && rail.cz==this.getZcoord())
        {
        	rail.cx=-1; rail.cy=-1; rail.cz=-1;
        }
    }
    // NBT�ǂݍ��ݕ⏕
    private Vec3d readVec3(NBTTagCompound nbt, String name)
    {
    	return new Vec3d(nbt.getDouble(name+"x"), nbt.getDouble(name+"y"), nbt.getDouble(name+"z"));
    }
    
    /*
     * �������NBT���������ރ��\�b�h�B
     */
    public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        saveToNBT(par1NBTTagCompound, "");
        return par1NBTTagCompound;
    }

    public void saveToNBT(NBTTagCompound nbt, String tag)
    {
    	nbt.setInteger(tag+"posnum", this.PosNum);
        writeRailNBT(nbt, BaseRail, tag+"");
     	writeRailNBT(nbt, NextRail, tag+"n");
     	nbt.setInteger(tag+"railmodelindex", modelrailindex);
    }

    protected void writeRailNBT(NBTTagCompound nbt, DataTileEntityRail rail, String tag)
    {
    	writeVec3(nbt, rail.vecPos,  tag+"pos");
    	writeVec3(nbt, rail.vecDir,  tag+"dir");
    	writeVec3(nbt, rail.vecUp, 	tag+"up");
        
    	nbt.setFloat(tag+"fup", rail.fUp);
    	nbt.setFloat(tag+"fdt", rail.fDirTwist);
    	nbt.setFloat(tag+"pow", rail.Power);
    	
        nbt.setInteger(tag+"cx", rail.cx); 
        nbt.setInteger(tag+"cy", rail.cy); 
        nbt.setInteger(tag+"cz", rail.cz); 
        
     // �������g�Ɍq����̂�h��           
        if(rail.cx==this.getXcoord() && rail.cy==this.getYcoord() && rail.cz==this.getZcoord())
        {
        	rail.cx=-1; rail.cy=-1; rail.cz=-1;
        }
    }
    // NBT�������ݕ⏕
    private void writeVec3(NBTTagCompound nbt, Vec3d vec, String name)
    {
    	nbt.setDouble(name+"x", vec.x);
    	nbt.setDouble(name+"y", vec.y);
    	nbt.setDouble(name+"z", vec.z);
    }
    

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(this.pos, 1, nbtTagCompound);
	}
 
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
        this.readFromNBT(pkt.getNbtCompound());
    }
	
	//�T�[�o�[���N���C�A���g�֐���_�𑗐M���邽�߂̓����֐� ���M�Ώۃv���C���[�����܂��Ă���Ƃ�
    public void syncData(EntityPlayerMP player)
    {
//    	ERC_MessageRailStC packet = new ERC_MessageRailStC(
//    			xCoord, yCoord, zCoord, PosNum, 
//    			px, py, pz, nx, ny, nz,
//    			BaseRail.vecPos, BaseRail.vecDir, BaseRail.vecUp, 
//    			NextRail.vecPos, NextRail.vecDir, NextRail.vecUp, 
//    			BaseRail.Power, BaseRail.fUp, BaseRail.fDirTwist,
//    			NextRail.Power, NextRail.fUp, NextRail.fDirTwist
//    			);
    	ERC_MessageRailStC packet = new ERC_MessageRailStC(this.getXcoord(), this.getYcoord(), this.getZcoord(), PosNum, modelrailindex);
    	packet.addRail(BaseRail);
    	packet.addRail(NextRail);
	    ERC_PacketHandler.INSTANCE.sendTo(packet, player);
//	    ERC_PacketHandler.INSTANCE.sendToAll(packet);
    }
	
	//���g�𓯊� �v���C���[�S�����Ώہi�̂͂��H�j
	public void syncData()
	{
		ERC_MessageRailStC packet = new ERC_MessageRailStC(this.getXcoord(), this.getYcoord(), this.getZcoord(), PosNum, modelrailindex);
    	packet.addRail(BaseRail);
    	packet.addRail(NextRail);
	    ERC_PacketHandler.INSTANCE.sendToAll(packet);
	}

	public void connectionFromBack(int x, int y, int z)
	{
		// �����ɂȂ���v���͔j��
		if(x==this.getXcoord() && y==this.getYcoord() && z==this.getZcoord())return;
				
		this.SetPrevRailPosition(x, y, z);
    	this.syncData();
	}

	public void connectionToNext(DataTileEntityRail next, int x, int y, int z)
	{
		// �����ɂȂ���v���͔j��
		if(x==this.getXcoord() && y==this.getYcoord() && z==this.getZcoord())return;
		
    	float power = ERC_MathHelper.CalcSmoothRailPower(BaseRail.vecDir, next.vecDir, BaseRail.vecPos, next.vecPos);
		this.BaseRail.Power = power;
    	this.SetNextRailVectors(next,x,y,z);
    	Wrap_TileEntityRail prev = this.getPrevRailTileEntity();
    	if(prev!=null)
    	{
    		TileEntityRailBase r = prev.getRail();
    		r.SetNextRailVectors(this.getRail());
    		r.CalcRailLength();
    		prev.syncData();
    	}
//    	this.CreateNewRailVertexFromControlPoint();
    	this.CalcRailLength();
    	this.syncData();
	}

	private Vec3d rotateAroundX(Vec3d dir, double value)
	{
		double cosval = Math.cos(value);
		double sinval = Math.sin(value);
		return new Vec3d(dir.x,
				dir.y * cosval - dir.z * sinval,
				dir.y * sinval + dir.z * cosval);
	}

	private Vec3d rotateAroundY(Vec3d dir, double value)
	{
		double cosval = Math.cos(value);
		double sinval = Math.sin(value);
		return new Vec3d(dir.x * cosval + dir.z * sinval,
				dir.y,
				-dir.x * sinval + dir.z * cosval);
	}

	private Vec3d rotateAroundZ(Vec3d dir, double value)
	{
		double cosval = Math.cos(value);
		double sinval = Math.sin(value);
		return new Vec3d(dir.x * cosval - dir.y * sinval,
				dir.x * sinval + dir.y * cosval,
				dir.z);
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}
}

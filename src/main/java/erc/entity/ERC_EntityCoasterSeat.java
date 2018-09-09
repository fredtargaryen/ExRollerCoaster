package erc.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc._core.ERC_Logger;
import erc.item.itemSUSHI;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ModelLoadManager.ModelOptions;
import erc.math.ERC_MathHelper;
import erc.message.ERC_MessageCoasterMisc;
import erc.message.ERC_PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ERC_EntityCoasterSeat extends Wrap_EntityCoaster {

	private static final DataParameter<Integer> SEAT_INDEX = EntityDataManager.<Integer>createKey(ERC_EntityCoasterSeat.class, DataSerializers.VARINT);
	private static final DataParameter<Float> OFFSET_X = EntityDataManager.<Float>createKey(ERC_EntityCoasterSeat.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> OFFSET_Y = EntityDataManager.<Float>createKey(ERC_EntityCoasterSeat.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> OFFSET_Z = EntityDataManager.<Float>createKey(ERC_EntityCoasterSeat.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> ROT_X = EntityDataManager.<Float>createKey(ERC_EntityCoasterSeat.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> ROT_Y = EntityDataManager.<Float>createKey(ERC_EntityCoasterSeat.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> ROT_Z = EntityDataManager.<Float>createKey(ERC_EntityCoasterSeat.class, DataSerializers.FLOAT);
	public ERC_EntityCoaster parent;
	private int UpdatePacketCounter = 4;
	boolean canRide = true;
	public boolean updateFlag = false;
	public boolean waitUpdateRiderFlag = false;
	//part of Options -> this.dataManager
//	int seatIndex = -1;
//	public float offsetX;
//	public float offsetY;
//	public float offsetZ;
//	public float rotX;
//	public float rotY;
//	public float rotZ;
	
	public ERC_EntityCoasterSeat(World world) {
		super(world);
//		forceSpawn = true;
		setSize(1.1f, 0.8f);
//		ERC_ManagerPrevTickCoasterSeatSetPos.addSeat(this);
	}

	public ERC_EntityCoasterSeat(World w, ERC_EntityCoaster parent, int i) {
		this(w);
		this.parent = parent;
		setSeatIndex(i);
//		spawnControl = true;
//		ERC_Logger.info("***seat create, x:"+posX+", y:"+posY+", z:"+posZ);
	}

	public void setOptions(ModelOptions op, int idx)
	{
		if(op==null)return;
		if(op.offsetX==null)return;
		if(op.offsetX.length <= idx)return;
		setSize(op.size[idx], op.size[idx]);
		if(world.isRemote)return;
		setOffsetX(op.offsetX[idx]);
		setOffsetY(op.offsetY[idx]);
		setOffsetZ(op.offsetZ[idx]);  
		setRotX(op.rotX[idx]);
		setRotY(op.rotY[idx]);
		setRotZ(op.rotZ[idx]);
		canRide = op.canRide;
	}

	@Override
	protected void entityInit()
	{
		
		this.dataManager.register(SEAT_INDEX, -1);
		this.dataManager.register(OFFSET_X, 0f);
		this.dataManager.register(OFFSET_Y, 0f);
		this.dataManager.register(OFFSET_Z, 0f);
		this.dataManager.register(ROT_X, 0f);
		this.dataManager.register(ROT_Y, 0f);
		this.dataManager.register(ROT_Z, 0f);
	}
	
    protected void setSize(float w, float h)
    {
//    	w*=10.0;h*=10.0;
        if (w != this.width || h != this.height)
        {
            this.width = w;// + 40f;
            this.height = h;
            this.setEntityBoundingBox(new AxisAlignedBB(-w/2 + this.posX, +h/2 + this.posY, -w/2 + this.posZ,
														+w/2 + this.posX, +h/2 + this.posY, +w/2 + this.posZ));
        }
    }
    
	public boolean canBeCollidedWith()
    {
        return true;
    }
	
    public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
    	if(parent==null)return true;
    	return parent.attackEntityFrom(ds, p_70097_2_);
//    	parent.setDead();
//    	this.setPosition(posX, posY, posZ);
//    	return true;
    }
    
	public boolean canBeRidden()
    {
		if(world.isRemote)return false;
        return canRide; // true : ����
    }
	
    // �E�N���b�N���ꂽ�炭��
    @Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
    	if(parent==null)return true;
    	if(parent.requestConnectCoaster(player))return true;
    	if(isRiddenSUSHI(player))return true;
    	if(requestRidingMob(player))return true;
    	if(!canBeRidden())return true;
    	
    	//��������Ă�@�{�@�v���C���[�������Ă�@�{�@�E�N���b�N�����v���C���[�ƈႤ�v���C���[�������Ă�
        Entity passenger = this.getControllingPassenger();
		if (passenger != null && passenger instanceof EntityPlayer && passenger != player)
        {
            return true;
        }
        //����������Ă�@�{�@�E�N���b�N�����v���C���[�ȊO�̉���������Ă�
        else if (passenger != null && passenger != player)
        {
        	//���낷
        	passenger.dismountRidingEntity();
            return true;
        }
        //����������Ă�@������������Ȃ�
        else if (passenger != null)
        {
        	return true;
        }
        else
        {
            if (!this.world.isRemote)
            {
            	ERC_CoasterAndRailManager.resetViewAngles();
                player.startRiding(this);
            }
            return true;
        }
    }
		
    protected boolean isRiddenSUSHI(EntityPlayer player)
	{
		if(player.getHeldItemMainhand()==null)return false;
		if(player.getHeldItemMainhand().getItem() instanceof itemSUSHI)
		{
			if(!world.isRemote)
			{
				entitySUSHI e = new entitySUSHI(world,posX,posY,posZ);
				world.spawnEntity(e);	
				e.startRiding(this);
				if(!player.capabilities.isCreativeMode)player.getHeldItemMainhand().grow(-1);
			}
			player.swingArm(player.getActiveHand());
			return true;
		}
		return false;
	}
	
	protected boolean requestRidingMob(EntityPlayer player)
	{
		if(world.isRemote)return false;
		ItemStack is = player.getHeldItemMainhand();
		if(is==null)return false;
		if(is.getItem() instanceof ItemMonsterPlacer)
		{
			Entity entity = ItemMonsterPlacer.spawnCreature(world, ItemMonsterPlacer.getNamedIdFrom(is), posX, posY, posZ);
			entity.startRiding(this);
			if (!player.capabilities.isCreativeMode)is.grow(-1);
			player.swingArm(player.getActiveHand());
			return true;
		}
		if(is.getItem() instanceof ItemLead)
		{
	        double d0 = 7.0D;
			@SuppressWarnings("unchecked")
			List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(posX-d0, posY-d0, posZ-d0, posX+d0, posY+d0, posZ+d0));
	        if (list != null)
	        {
	            Iterator<EntityLiving> iterator = list.iterator();
	            while (iterator.hasNext())
	            {
	                EntityLiving entityliving = iterator.next();

	                if (entityliving.getLeashed() && entityliving.getLeashHolder() == player)
	                {
	                	entityliving.startRiding(this);
	                    entityliving.clearLeashed(true, !player.capabilities.isCreativeMode);
	                    player.swingArm(player.getActiveHand());
	                    return true;
	                }
	            }
	        }
		}
		return false;
	}
	
	@Override
	public void setDead() {
//		ERC_Logger.debugInfo("seat is dead ... id:"+this.getEntityId());
		super.setDead();
	}
	
	@Override
	public void onUpdate() 
	{
//		if(worldObj.isRemote)ERC_Logger.debugInfo("end seat onUpdate");
//		setDead();
		if(updateInit())return;
		if(updateFlag==parent.updateFlag)
		{
//			ERC_Logger.debugInfo("seat stay");
		} 	// �Q�D�ҋ@
		else
		{
//			ERC_Logger.debugInfo("seat update");
			_onUpdate();						// �U�D�e���ゾ����X�V����
		}

		/**
		 * FTHACK
		 * Problem fixed:
		 * Sitting in the coaster doesn't always work (1 and 2)
		 *  * Player updates when seat updateFlag == coaster updateFlag
		 *    * They alternate but rarely match
		 */
		//updateFlag = !updateFlag;
	}

	public void _onUpdate() 
	{
//		if(worldObj.isRemote)ERC_Logger.debugInfo("end seat _onUpdate");
//		if(updateInit())return;
		syncToClient();
		savePrevData();
		double ox = getOffsetX();
		double oy = getOffsetY();
		double oz = getOffsetZ();
		this.setPosition(
				parent.posX + parent.ERCPosMat.offsetX.x*ox + parent.ERCPosMat.offsetY.x*oy + parent.ERCPosMat.offsetZ.x*oz, 
				parent.posY + parent.ERCPosMat.offsetX.y*ox + parent.ERCPosMat.offsetY.y*oy + parent.ERCPosMat.offsetZ.y*oz, 
				parent.posZ + parent.ERCPosMat.offsetX.z*ox + parent.ERCPosMat.offsetY.z*oy + parent.ERCPosMat.offsetZ.z*oz);

		if(waitUpdateRiderFlag)updateRiderPosition2(this.getControllingPassenger());
	}

	protected void syncToClient()
	{
		if(this.UpdatePacketCounter--<=0)
		{
			UpdatePacketCounter = 40;
			if(!world.isRemote)
			{
				if(parent!=null)
				{
					ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
					ERC_PacketHandler.INSTANCE.sendToAll(packet);
//					parent.resetSeat(getSeatIndex(), this);
//					ERC_Logger.info("Server teach client parentid");
				}
				else
				{
//					if(parent.resetSeat(getSeatIndex(), this))
//						setDead();
				}
			}
			else // client
			{
//				parent.resetSeat(getSeatIndex(), this);
			}
		}
	}
	
	protected boolean updateInit()
	{
		if(parent==null)
		{
			if(!world.isRemote)
			{
//				if(searchParent())return false;
//				else 
				ERC_Logger.debugInfo("seat log : parent is null.");
				if(!isDead)setDead();
			}
			return true; 
		}
		if(parent.isDead)
		{
			if(!world.isRemote)if(!isDead)setDead();
			return true;
		}
		if(!world.isRemote && getSeatIndex() == -1) // �����O�Ȃǂ̍ăX�|�[���Őe���ăX�|�[�������Ȃ������ꍇfalse(server�̂�)
		{
			if(!isDead)setDead();
			return true;
		}
		return false;
	}
	
	protected void savePrevData()
    {
    	this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;             
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;       
        this.prevRotationRoll = this.rotationRoll;
    }
	
	public boolean searchParent()
	{
		return false;
//		double x = posX - getOffsetX();
//		double y = posY - getOffsetY();
//		double z = posZ - getOffsetZ();
//		double s = Math.max(Math.abs(getOffsetX()), Math.abs(getOffsetY()));
//		s = Math.max(s, Math.abs(getOffsetZ()));
//		@SuppressWarnings("unchecked")
//		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x-s, y-s, z-s, x+s, y+s, z+s));
//		for(Entity e : list)
//		{
//			if(e instanceof ERC_EntityCoaster)
//			{
//				parent = (ERC_EntityCoaster) e;
//				parent.resetSeat(getSeatIndex(), this);
//				return true;
//			}
//		}
//		return false;
	}
	
	public double getMountedYOffset()
    {
        return (double)this.height * 0.4;
    }
	
	@Override
	public void updatePassenger(Entity passenger)
	{
		if(parent == null) {
			return;
		}
		if(updateFlag!=parent.updateFlag)
		{
//			ERC_Logger.debugInfo("seat rider stay");
			waitUpdateRiderFlag = true;
		} 	// �Q�D�ҋ@
		else
		{
//			ERC_Logger.debugInfo("seat rider update");
			updateRiderPosition2(passenger);					
			// �U�D�e���ゾ����X�V����
		}
	}
	public void updateRiderPosition2(Entity passenger)
	{
//		updateRiderPosFlag = true;
//		ERC_Logger.info("entityseat::updateRilderPosition");
		if(parent==null)return;
    	if (passenger != null)
        {
    		waitUpdateRiderFlag = false;
    		// �����]
//    		if(worldObj.isRemote)ERC_Logger.debugInfo("seat updateRiderposition");
    		Vec3d vx = parent.ERCPosMat.offsetX;
    		Vec3d vy = parent.ERCPosMat.offsetY;
    		Vec3d vz = parent.ERCPosMat.offsetZ;
    		// Z����]
    		vx = ERC_MathHelper.rotateAroundVector(vx, vz, getRotZ());
    		vy = ERC_MathHelper.rotateAroundVector(vy, vz, getRotZ());
    		// Y����]
    		vx = ERC_MathHelper.rotateAroundVector(vx, parent.ERCPosMat.offsetY, getRotY());
    		vz = ERC_MathHelper.rotateAroundVector(vz, parent.ERCPosMat.offsetY, getRotY());
    		// X����]
    		vy = ERC_MathHelper.rotateAroundVector(vy, parent.ERCPosMat.offsetX, getRotX());
    		vz = ERC_MathHelper.rotateAroundVector(vz, parent.ERCPosMat.offsetX, getRotX());
    		{
    			////////////// �v���C���[��]�ʌv�Z
    			// ViewYaw��]�x�N�g���@dir1->dir_rotView, cross->turnCross
    			Vec3d dir_rotView = ERC_MathHelper.rotateAroundVector(vz, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
    			Vec3d turnCross = ERC_MathHelper.rotateAroundVector(vx, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
    			// ViewPitch��]�x�N�g�� dir1->dir_rotView
    			Vec3d dir_rotViewPitch = ERC_MathHelper.rotateAroundVector(dir_rotView, turnCross, Math.toRadians(ERC_CoasterAndRailManager.rotationViewPitch));
    			// pitch�p dir_rotViewPitch�̐����x�N�g��
    			Vec3d dir_rotViewPitchHorz = new Vec3d(dir_rotViewPitch.x, 0, dir_rotViewPitch.z);
    			// roll�pturnCross�̐����x�N�g��
    			Vec3d crossHorzFix = new Vec3d(0, 1, 0).crossProduct(dir_rotViewPitch);
    			if(crossHorzFix.lengthVector()==0.0)crossHorzFix=new Vec3d(1, 0, 0);
		
    			// yaw OK
    			 rotationYaw = (float) -Math.toDegrees( Math.atan2(dir_rotViewPitch.x, dir_rotViewPitch.z) );

    			// pitch OK
    			rotationPitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.y>=0?-1f:1f) );
    			if(Float.isNaN(rotationPitch))
    				rotationPitch=0;
    			
    			// roll
    			rotationRoll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.y>=0?1f:-1f) );
    			if(Float.isNaN(rotationRoll))
    				rotationRoll=0;
    		}
    		prevRotationYaw = ERC_MathHelper.fixrot(rotationYaw, prevRotationYaw);
    		prevRotationPitch = ERC_MathHelper.fixrot(rotationPitch, prevRotationPitch);
    		prevRotationRoll = ERC_MathHelper.fixrot(rotationRoll, prevRotationRoll);
          
    		passenger.rotationYaw = this.rotationYaw;
    		passenger.rotationPitch = this.rotationPitch;
    		passenger.prevRotationYaw = this.prevRotationYaw;
    		passenger.prevRotationPitch = this.prevRotationPitch; 
//    		passenger.rotationYaw = 0;
//    		passenger.rotationPitch = -ERC_CoasterAndRailManager.rotationViewPitch;
    		
    		double toffsety = passenger.getYOffset();
//            passenger.setPosition(
//            		this.posX + vy.xCoord*toffsety, 
//            		this.posY + vy.yCoord*toffsety,
//            		this.posZ + vy.zCoord*toffsety
//            		);
//    		double ox = getOffsetX();
//    		double oy = getOffsetY();
//    		double oz = getOffsetZ();
//            passenger.setPosition(
//    				parent.posX + vy.xCoord*toffsety + parent.ERCPosMat.offsetX.xCoord*ox + parent.ERCPosMat.offsetY.xCoord*oy + parent.ERCPosMat.offsetZ.xCoord*oz, 
//    				parent.posY + vy.yCoord*toffsety + parent.ERCPosMat.offsetX.yCoord*ox + parent.ERCPosMat.offsetY.yCoord*oy + parent.ERCPosMat.offsetZ.yCoord*oz, 
//    				parent.posZ + vy.zCoord*toffsety + parent.ERCPosMat.offsetX.zCoord*ox + parent.ERCPosMat.offsetY.zCoord*oy + parent.ERCPosMat.offsetZ.zCoord*oz);
            passenger.setPosition(
    				this.posX + vy.x*toffsety,
    				this.posY + vy.y*toffsety,
    				this.posZ + vy.z*toffsety);
            
            passenger.motionX = this.parent.Speed * parent.ERCPosMat.Dir.x * 1;
            passenger.motionY = this.parent.Speed * parent.ERCPosMat.Dir.y * 1;
            passenger.motionZ = this.parent.Speed * parent.ERCPosMat.Dir.z * 1;
//            ERC_Logger.info("" + riddenByEntity.motionX + riddenByEntity.motionY + riddenByEntity.motionZ );
			
            if(world.isRemote && passenger instanceof EntityLivingBase)
            {
            	EntityLivingBase el = (EntityLivingBase) passenger;
            	el.renderYawOffset = parent.ERCPosMat.yaw; 
            	if(passenger == Minecraft.getMinecraft().player)
            		el.rotationYawHead = ERC_CoasterAndRailManager.rotationViewYaw + el.renderYawOffset;
//            	el.head
            }
            
        }
//    	ERC_CoasterAndRailManager.setRotRoll(rotationRoll, prevRotationRoll);
	}        
	  
    @SideOnly(Side.CLIENT)
    public void setAngles(float deltax, float deltay)
    {
//    	ERC_CoasterAndRailManager.setAngles(deltax, deltay);
    }
    
	//@Override
	//public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    //{
    	//�d�l�Ƃ��ĉ��������@�T�[�o�[����̋K���Entity�����Ŏg���Ă���A�����𖳌��ɂ��邽��
//		ERC_Logger.debugInfo("catch!");
//		super.setPositionAndRotation2(x, y, z, yaw, pit, p_70056_9_);
    //}
	
//	public float getRoll(float partialTicks)
//	{
//		return offsetRot + parent.prevRotationRoll + (parent.rotationRoll - parent.prevRotationRoll)*partialTicks;
//	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		setSeatIndex(nbt.getInteger("seatindex"));
		setOffsetX(nbt.getFloat("seatoffsetx"));
		setOffsetY(nbt.getFloat("seatoffsety"));
		setOffsetZ(nbt.getFloat("seatoffsetz"));
		setRotX(nbt.getFloat("seatrotx"));   
		setRotY(nbt.getFloat("seatroty"));   
		setRotZ(nbt.getFloat("seatrotz"));   
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) 
	{
		nbt.setInteger("seatindex", getSeatIndex());
		nbt.setFloat("seatoffsetx", getOffsetX());
		nbt.setFloat("seatoffsety", getOffsetY());
		nbt.setFloat("seatoffsetz", getOffsetZ());
		nbt.setFloat("seatrotx", getRotX());   
		nbt.setFloat("seatroty", getRotY());   
		nbt.setFloat("seatrotz", getRotZ());   
	}


	public void SyncCoasterMisc_Send(ByteBuf buf, int flag)
	{
		switch(flag)
		{
		case 3 : //CtS �\��
			break;
		case 4 : //StC �e���N���ɋ�����
			buf.writeInt(parent.getEntityId());
			break;
		}
	}
	public void SyncCoasterMisc_Receive(ByteBuf buf, int flag)
	{
		switch(flag)
		{
		case 3:
			ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
			ERC_PacketHandler.INSTANCE.sendToAll(packet);
//			ERC_Logger.info("server repost parentID to client");
			break;
		case 4 :
			int parentid = buf.readInt();
			parent = (ERC_EntityCoaster) world.getEntityByID(parentid);
			if(parent==null){
				ERC_Logger.warn("parent id is Invalid.  id:"+parentid);
				return;
			}
			parent.addSeat(this, getSeatIndex());
//			ERC_Logger.info("client get parent");
			return;
		}
	}

	/////////////////////
	//FT RIDING METHODS//
	/////////////////////
	@Override
	protected void addPassenger(Entity p)
	{
		super.addPassenger(p);
//		if(this.world.isRemote)
//		{
//			MinecraftForge.EVENT_BUS.register(this);
//		}
	}

	@Override
	public void removePassenger(Entity passenger)
	{
		super.removePassenger(passenger);
//		if(this.world.isRemote)
//		{
//			MinecraftForge.EVENT_BUS.unregister(this);
//		}
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
//		if (event.phase == TickEvent.Phase.START) {
//			// player available is: mc.thePlayer, need to null check if you crash during world start-up
//			// update player rotation (i.e. render view) based on your conditions
//			GlStateManager.pushMatrix();
//            //Yaw = coaster yaw + player yaw
//            //Pitch = coaster pitch + player pitch
//            //Roll = Coaster roll
//			try {
//				GlStateManager.rotate(180.0F - this.parent.ERCPosMat.getFixedYaw(event.renderTickTime), 0.0F, 1.0F, 0.0F);
//				GlStateManager.rotate(this.parent.ERCPosMat.getFixedPitch(event.renderTickTime), -1f, 0f, 0f);
//				GlStateManager.rotate(-this.parent.ERCPosMat.getFixedRoll(event.renderTickTime), 0f, 0f, 1f);
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//		else {
//			//END
//			GlStateManager.popMatrix();
//		}
	}
	////////////////////////////
	//END OF FT RIDING METHODS//
    ////////////////////////////
	////////////////////////////////////////this.dataManager
	public int getSeatIndex()
	{
		return this.dataManager.get(SEAT_INDEX);
	}
	public void setSeatIndex(int idx)
	{
		this.dataManager.set(SEAT_INDEX, idx);
	}
	
	public float getOffsetX()
	{
		return this.dataManager.get(OFFSET_X);
	}
	public void setOffsetX(float offsetx)
	{
		this.dataManager.set(OFFSET_X, offsetx);
	}

	public float getOffsetY()
	{
		return this.dataManager.get(OFFSET_Y);
	}
	public void setOffsetY(float offsety)
	{
		this.dataManager.set(OFFSET_Y, offsety);
	}
	
	public float getOffsetZ()
	{
		return this.dataManager.get(OFFSET_Z);
	}
	public void setOffsetZ(float offsetz)
	{
		this.dataManager.set(OFFSET_Z, offsetz);
	}
	
	public float getRotX()
	{
		return this.dataManager.get(ROT_X);
	}
	public void setRotX(float rot)
	{
		this.dataManager.set(ROT_X, rot);
	}
	
	public float getRotY()
	{
		return this.dataManager.get(ROT_Y);
	}
	public void setRotY(float rot)
	{
		this.dataManager.set(ROT_Y, rot);
	}
	
	public float getRotZ()
	{
		return this.dataManager.get(ROT_Z);
	}
	public void setRotZ(float rot)
	{
		this.dataManager.set(ROT_Z, rot);
	}
}

package erc.message;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erc._core.ERC_Logger;
import erc.entity.ERC_EntityCoaster;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class ERC_MessageCoasterCtS implements IMessage, IMessageHandler<ERC_MessageCoasterCtS, IMessage>{

	// �N���C�A���g����̃��[�����W�p�p�����[�^�֘A���b�Z�[�W
	public int entityID;
	public float paramT;
	public double speed;
	// ���ݏ���Ă��郌�[���̍��W
	public int x;
	public int y;
	public int z;
//	// ���f���`��I�v�V����
//	public int modelID;
//	public ModelOptions ops;
	
	public ERC_MessageCoasterCtS(){/*ops = new ModelOptions();*/}
	
	public ERC_MessageCoasterCtS(int id, float t, double v, int x, int y, int z)
	{
		super();
		
	    this.paramT = t;
	    this.entityID = id;
	    this.speed = v;
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    
//	    this.modelID = ID;
//	    this.ops = op;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(this.paramT);
		buf.writeInt(this.entityID);
		buf.writeDouble(this.speed);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		
//		buf.writeInt(this.modelID);
//		ops.WriteBuf(buf);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.paramT = buf.readFloat();
	    this.entityID = buf.readInt();
	    this.speed = buf.readDouble();
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    
//	    this.modelID = buf.readInt(); 
//	    ops.ReadBuf(buf);
    }
		
	@Override
    public IMessage onMessage(ERC_MessageCoasterCtS message, MessageContext ctx)
    {
		World world = ctx.getServerHandler().player.world;
		ERC_EntityCoaster coaster = (ERC_EntityCoaster)world.getEntityByID(message.entityID);
		if(coaster == null)return null;
		if(message.paramT > -50f)
		{
			coaster.setParamT(message.paramT);
			coaster.Speed = message.speed;
			coaster.setRail( ((Wrap_TileEntityRail) world.getTileEntity(new BlockPos(message.x, message.y, message.z))).getRail() );
//			coaster.setModel(message.modelID);
		}
		else
		{
			ERC_Logger.warn("MessageCoasterCtS : this code must not call.");
//			coaster.setModel(message.modelID);
//			coaster.setModelOptions(message.ops);
		}
        return null;
    }
    
}
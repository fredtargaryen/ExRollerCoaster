package erc.message;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_ModelLoadManager.ModelOptions;
import io.netty.buffer.ByteBuf;

public class ERC_MessageCoasterStC implements IMessage, IMessageHandler<ERC_MessageCoasterStC, IMessage>{

	// �N���C�A���g����̃��[�����W�p�p�����[�^�֘A���b�Z�[�W
	public int entityID;
	public float paramT;
	public double speed;
	// ���ݏ���Ă��郌�[���̍��W
	public int x;
	public int y;
	public int z;
	// ���f���`��I�v�V����
	public int modelID;
	public ModelOptions ops;
	
	public ERC_MessageCoasterStC(){ops = new ModelOptions();}
	
	public ERC_MessageCoasterStC(int id, float t, double v, int x, int y, int z, int modelid, ModelOptions op)
	{
		this.paramT = t;
	    this.entityID = id;
	    this.speed = v;
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    
	    this.modelID = modelid;
	    this.ops = op;
	}
	
//	public ERC_MessageCoasterStC(int id, float t, double v, int x, int y, int z, int connectparentID)
//	{
//	    this.paramT = t;
//	    this.entityID = id;
//	    this.speed = v;
//	    this.x = x;
//	    this.y = y;
//	    this.z = z;
////	    this.connectparentID = connectparentID;
//  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(this.paramT);
		buf.writeInt(this.entityID);
		buf.writeDouble(this.speed);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		
		buf.writeInt(this.modelID);
		ops.WriteBuf(buf);
//		buf.writeInt(this.connectparentID);
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
	    
	    this.modelID = buf.readInt();
	    ops.ReadBuf(buf);
//	    this.connectparentID = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(ERC_MessageCoasterStC message, MessageContext ctx)
    {
		IThreadListener client = Minecraft.getMinecraft();
		client.addScheduledTask(() -> {
			Minecraft mc = (Minecraft) client;
			ERC_EntityCoaster coaster = (ERC_EntityCoaster) mc.world.getEntityByID(message.entityID);
			if (coaster != null) {
				coaster.setParamFromPacket(message.paramT, message.speed, message.x, message.y, message.z);
				coaster.setModelOptions(message.modelID, message.ops);

//				if(message.connectparentID > -1)
//				{
//			 		ERC_EntityCoaster parent = (ERC_EntityCoaster)FMLClientHandler.instance().getClient().theWorld.getEntityByID(message.connectparentID);
//			 		if(parent == null)
//			 		{
//				 		coaster.killCoaster();
//				 		return null;
//					}
//			 		((ERC_EntityCoasterConnector)coaster).setParentPointer(parent);
//			 		parent.connectionCoaster((ERC_EntityCoasterConnector) coaster);
//			 		((ERC_EntityCoasterConnector)coaster).setConnectParentFlag(-1);
//				}
			}
		});
        return null;
    }
}

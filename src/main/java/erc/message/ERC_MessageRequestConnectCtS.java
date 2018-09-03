package erc.message;

import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erc.entity.ERC_EntityCoasterConnector;
import io.netty.buffer.ByteBuf;

public class ERC_MessageRequestConnectCtS implements IMessage, IMessageHandler<ERC_MessageRequestConnectCtS, IMessage>{

	public int playerEntityID;
	public int CoasterID;
	
	public ERC_MessageRequestConnectCtS(){}
	
	public ERC_MessageRequestConnectCtS(int playerid, int coasterid)
	{
		playerEntityID = playerid;
		CoasterID = coasterid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.playerEntityID);
		buf.writeInt(this.CoasterID);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.playerEntityID = buf.readInt();
		this.CoasterID = buf.readInt();
    }	    	

	@Override
    public IMessage onMessage(ERC_MessageRequestConnectCtS message, MessageContext ctx)
    {
    	final IThreadListener serverListener = ctx.getServerHandler().player.getServerWorld();
    	serverListener.addScheduledTask(() -> {
			ERC_EntityCoasterConnector coaster = (ERC_EntityCoasterConnector) ((World) serverListener).getEntityByID(message.CoasterID);
			if(coaster != null) coaster.receiveConnectionRequestFromClient(message.playerEntityID);
		});
    	return null;
    }
}
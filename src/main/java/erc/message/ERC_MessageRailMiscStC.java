package erc.message;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;

public class ERC_MessageRailMiscStC implements IMessage, IMessageHandler<ERC_MessageRailMiscStC, IMessage>{
		
	int x;
	int y;
	int z;
	Wrap_TileEntityRail rail;

	public ERC_MessageRailMiscStC(){}
	
	public ERC_MessageRailMiscStC(Wrap_TileEntityRail r)
	{
	    this.rail = r;
	    this.x = rail.getXcoord();
	    this.y = rail.getYcoord();
	    this.z = rail.getZcoord();
  	}
	
	@Override
    public void toBytes(ByteBuf buf)
    {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		rail.setDataToByteMessage(buf);
    }
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		Wrap_TileEntityRail rail = (Wrap_TileEntityRail)FMLClientHandler.instance().getClient().world.getTileEntity(new BlockPos(x, y, z));
		if(rail==null)return;
		rail.getDataFromByteMessage(buf);
    }
	
	
	@Override
    public IMessage onMessage(ERC_MessageRailMiscStC message, MessageContext ctx)
    {
        return null;
    }
    
}
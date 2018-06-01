package erc.tileEntity;

import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityRailDetector extends TileEntityRailBase{
	
	boolean outputFlag;
	
	public TileEntityRailDetector()
	{
		super();
		outputFlag = false;
//		RailTexture = new ResourceLocation("textures/blocks/stone.png");
	}
	
	public void changeOutput()
	{
		outputFlag = !outputFlag;
	}
	public void setOutput(boolean flag)
	{
		outputFlag = flag;
	}
	public boolean getFlag()
	{
		return outputFlag;
	}
	
	public void SpecialRailProcessing(ERC_EntityCoaster coaster)
	{
		if(!outputFlag && !(coaster instanceof ERC_EntityCoasterConnector))
		{
			// �o�͊J�n
			Block block = world.getBlockState(this.pos).getBlock();
			
			outputFlag = true;
			ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(this));
			BlockPos downPos = this.pos.down();
			world.neighborChanged(this.pos, block, this.pos.up());
			world.neighborChanged(this.pos, block, downPos);
			world.neighborChanged(this.pos, block, this.pos.east());
			world.neighborChanged(this.pos, block, this.pos.west());
			world.neighborChanged(this.pos, block, this.pos.south());
			world.neighborChanged(this.pos, block, this.pos.north());
			world.neighborChanged(downPos, block, this.pos);
			world.neighborChanged(downPos, block, downPos.down());
			world.neighborChanged(downPos, block, downPos.east());
			world.neighborChanged(downPos, block, downPos.west());
			world.neighborChanged(downPos, block, downPos.south());
			world.neighborChanged(downPos, block, downPos.north());
	        world.playSound(null, this.pos.add(0.5, 0.5, 0.5), SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);
		}
	}

	@Override
	public void onPassedCoaster(ERC_EntityCoaster coaster) 
	{
		if(!(coaster instanceof ERC_EntityCoasterConnector))
		{
			stopOutput();
		}
	}
	
	public void onDeleteCoaster()
	{
		stopOutput();
	}
	
	private void stopOutput()
	{
		// �o�͒�~
		outputFlag = false;
		Block block = world.getBlockState(this.pos).getBlock();
		BlockPos downPos = this.pos.down();
		world.neighborChanged(this.pos, block, this.pos.up());
		world.neighborChanged(this.pos, block, downPos);
		world.neighborChanged(this.pos, block, this.pos.east());
		world.neighborChanged(this.pos, block, this.pos.west());
		world.neighborChanged(this.pos, block, this.pos.south());
		world.neighborChanged(this.pos, block, this.pos.north());
		world.neighborChanged(downPos, block, this.pos);
		world.neighborChanged(downPos, block, downPos.down());
		world.neighborChanged(downPos, block, downPos.east());
		world.neighborChanged(downPos, block, downPos.west());
		world.neighborChanged(downPos, block, downPos.south());
		world.neighborChanged(downPos, block, downPos.north());
        world.playSound(null, this.pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);

	}
	
	public void setDataToByteMessage(ByteBuf buf)
	{
		buf.writeBoolean(this.outputFlag);
	}
	public void getDataFromByteMessage(ByteBuf buf)
	{
		outputFlag = buf.readBoolean();
	}

	@Override
	public World getWorldObj() {
		return this.world;
	}
}
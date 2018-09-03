package erc.tileEntity;

import erc.block.blockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import erc.entity.ERC_EntityCoaster;
import erc.gui.GUIRail;
import erc.gui.GUIRail.editFlag;
import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileEntityRailConstVelocity extends TileEntityRailBase{
	
	float constVelosityParam;
	boolean toggleflag;
	
	public TileEntityRailConstVelocity()
	{
		super();
		toggleflag = false;
		constVelosityParam = 0.07f;
		RailTexture = new ResourceLocation("textures/blocks/cobblestone.png");
	}

	public boolean getToggleFlag()
	{
		return toggleflag;
	}
	public void setToggleFlag(boolean flag)
	{
		toggleflag = flag;
	}
	public void changeToggleFlag()
	{
		toggleflag = !toggleflag;
	}

	public void setconstVelosityParam(float f)
	{
		constVelosityParam = f;
	}
	public float getconstVelosityParam()
	{
		return constVelosityParam;
	}
	
	public void SpecialRailProcessing(ERC_EntityCoaster coaster)
	{
		coaster.Speed -= constVelosityParam * (toggleflag?1f:0f);
		coaster.Speed *= 0.9;
		if(coaster.Speed < 0.008)coaster.Speed = 0;
		coaster.Speed += constVelosityParam * (toggleflag?1f:0f);
	}

	public void setDataToByteMessage(ByteBuf buf)
	{
		buf.writeBoolean(this.toggleflag);
		buf.writeFloat(constVelosityParam);
	}
	public void getDataFromByteMessage(ByteBuf buf)
	{
		toggleflag = buf.readBoolean();
		constVelosityParam = buf.readFloat();
	}

	@Override
	public World getWorldObj() {
		return this.world;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		toggleflag = nbt.getBoolean("const:toggleflag");
		constVelosityParam = nbt.getFloat("constvel");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("const:toggleflag", toggleflag);
		nbt.setFloat("constvel", constVelosityParam);
		return nbt;
	}
	
	// GUI
    public void SpecialGUIInit(GUIRail gui)
    {
    	gui.addButton4("Const Velocity Param", editFlag.SPECIAL);
    }
    public void SpecialGUISetData(int flag)
    {
    	switch(flag)
    	{
    	case 0 : constVelosityParam += -0.1;   break;
    	case 1 : constVelosityParam += -0.01;  break;
    	case 2 : constVelosityParam +=  0.01;  break;
    	case 3 : constVelosityParam +=  0.1;   break;
    	}
    	if(constVelosityParam > 5.0f)constVelosityParam = 0.1f;
    	else if(constVelosityParam < -5.0f) constVelosityParam = -5.0f;
    	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(this));
    }
    @Override
    public String SpecialGUIDrawString()
    {
    	return String.format("%02.1f", (constVelosityParam * 10f));
    }

	@Override
	public void render(Tessellator tess)
	{
		GlStateManager.disableLighting();
		float col = toggleflag?1.0f:0.3f;
		GL11.glColor4f(col, col, col, 1.0F);
		super.render(tess);
		GlStateManager.enableLighting();
	}

	/**
	 * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
	 * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
	 *
	 * @param world Current world
	 * @param pos Tile's world position
	 * @param oldState The old ID of the block
	 * @param newState The new ID of the block (May be the same)
	 * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
	 */
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		//return super.shouldRefresh(world, pos, oldState, newState);
		return !(oldState.getBlock() == newState.getBlock() && (oldState.getValue(blockRailBase.META) & 7) == (newState.getValue(blockRailBase.META) & 7));
	}
}

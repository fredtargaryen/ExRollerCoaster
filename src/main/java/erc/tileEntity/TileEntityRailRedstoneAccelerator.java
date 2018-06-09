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

public class TileEntityRailRedstoneAccelerator extends TileEntityRailBase{
	
	float accelParam = 0.04f;
	boolean toggleflag;
	
	public TileEntityRailRedstoneAccelerator()
	{
		super();
		toggleflag = false;
		RailTexture = new ResourceLocation("textures/blocks/redstone_block.png");
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
	
	public void setAccelParam(float f)
	{
		accelParam = f;
	}
	public float getAccelBase()
	{
		return accelParam;
	}
	
	public void SpecialRailProcessing(ERC_EntityCoaster coaster)
	{
		// ����
		if(toggleflag)
		{
			coaster.Speed += accelParam;
		}
		// ����
		else
		{
			coaster.Speed *= 0.8;
			if(coaster.Speed < 0.008)coaster.Speed = 0;
		}
	}

	
	public void setDataToByteMessage(ByteBuf buf)
	{
		buf.writeBoolean(this.toggleflag);
		buf.writeFloat(accelParam);
	}
	public void getDataFromByteMessage(ByteBuf buf)
	{
		toggleflag = buf.readBoolean();
		accelParam = buf.readFloat();
	}

	@Override
	public World getWorldObj() {
		return this.world;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		toggleflag = nbt.getBoolean("red:toggleflag");
		accelParam = nbt.getFloat("red:accelparam");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("red:toggleflag", toggleflag);
		nbt.setFloat("red:accelparam", accelParam);
		return nbt;
	}
	
    // GUI
    public void SpecialGUIInit(GUIRail gui)
    {
    	gui.addButton4("Accel Param", editFlag.SPECIAL);
    }
    public void SpecialGUISetData(int flag)
    {
    	switch(flag)
    	{
    	case 0 : accelParam += -0.01;   break;
    	case 1 : accelParam += -0.001;  break;
    	case 2 : accelParam +=  0.001;  break;
    	case 3 : accelParam +=  0.01;   break;
    	}
    	if(accelParam > 0.1f)accelParam = 0.1f;
    	else if(accelParam < -0.1f) accelParam = -0.1f;
    	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(this));
    }
    @Override
    public String SpecialGUIDrawString()
    {
    	return String.format("%02.1f", (accelParam * 100f));
    }
    
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

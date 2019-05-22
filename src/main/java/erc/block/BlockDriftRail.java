package erc.block;

import erc.tileEntity.TileEntityRailDrift;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDriftRail extends blockRailBase {
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRailDrift();
    }
}

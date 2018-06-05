package erc.block;

import erc.tileEntity.TileEntityNonGravityRail;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by MOTTY on 2017/09/30.
 */
public class BlockNonGravityRail extends blockRailBase {
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityNonGravityRail();
    }
}


package erc.gui;

import net.minecraftforge.fml.common.network.IGuiHandler;
import erc._core.ERC_Core;
import erc.gui.container.DefContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class ERC_GUIHandler implements IGuiHandler {
	
	/*�T�[�o�[���̏���*/
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
        case ERC_Core.GUIID_RailBase :
            return new DefContainer(x, y, z, null);
        }
        return null;
    }
    
    /*�N���C�A���g���̏���*/
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
        case ERC_Core.GUIID_RailBase :
        	return new GUIRail(x, y, z);
        }
        return null;
    }
}

package erc._core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ERC_CreateCreativeTab extends CreativeTabs{
	
	private Item IconItem;
	
	public ERC_CreateCreativeTab(String label, Item icon)
	{
		super(label);
		IconItem = icon;
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem()
	{
		return new ItemStack(IconItem);
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel()
	{
		return "ExRollerCoaster";
	}
}

package erc._core;

import erc._mc.block.rail.*;
import erc._mc.item.*;
import erc._mc.tileentity.TileEntityRailModelConstructor;
import mochisystems._core._Core;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import erc._mc.item.ItemCoaster;
import erc._mc.entity.EntityCoaster;
import erc._mc.entity.EntityCoasterSeat;
import erc._mc.entity.entitySUSHI;
import erc.gui.ERC_GUIHandler;
import mochisystems.handler.TickEventHandler;
import erc._mc.block.*;
import erc.network.ERC_PacketHandler;
import erc._core.proxy.IProxy;
import erc._mc.tileentity.TileEntityRail;
import erc.model.BlockInvisibleRail;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

@Mod( 
		modid = ERC_Core.MODID, 
		name = "Ex Roller Coaster", 
		version = ERC_Core.VERSION,
		dependencies = "required-after:Forge@[10.12.1.1090,);required-after:"+_Core.MODID+"@[1.0,)",
		useMetadata = true
		)
public class ERC_Core {
	public static final String MODID = "erc";
	public static final String VERSION = "2.0beta";

	
	//proxy////////////////////////////////////////
	@SidedProxy(clientSide = "erc._core.proxy.ERC_ClientProxy", serverSide = "erc._core.proxy.ERC_ServerProxy")
	public static IProxy proxy;

	//Blocks/////////////////////////////////////////
	public static Block railNormal = new BlockRail().setTextureBase(Blocks.iron_block);
	public static Block railAccel = new BlockAccelRail().setTextureBase(Blocks.redstone_block);
	public static Block railConst = new BlockConstVelocityRail().setTextureBase(Blocks.stone);
	public static Block railDetect = new BlockDetectorRail().setTextureBase(Blocks.quartz_block);
	public static Block railBranch = new BlockBranchRail().setTextureBase(Blocks.iron_block);
	public static Block railInvisible = new BlockInvisibleRail().setTextureBase(Blocks.glass);
	public static Block railNonGravity = new BlockNonGravityRail().setTextureBase(Blocks.portal);

	public static Block railModelConstructor = new BlockRailModelConstructor();
	public static ItemBlock ItemRailModelChanger = new ItemBlock(railModelConstructor);

	//special block renderer ID
	public static int blockRailRenderId;
//	public static int blockFerrisSupporterRenderID;
	
	// items /////////////////////////////////////////
	public static Item itemBasePipe = new Item();
	public static Item itemWrench = new ERC_ItemWrench();
	public static Item itemCoaster = new ItemCoaster();
	public static Item ItemSwitchRailModel = new ERC_ItemSwitchingRailModel();
	public static Item ItemRailBlockModelChanger = new ItemRailModelChanger();
	public static Item ItemSUSHI = new itemSUSHI();
	public static Item ItemStick = new ERC_ItemWrenchPlaceBlock();
	public static Item ItemSmoothAll = new ERC_ItemSmoothAll();
	
	//GUI/////////////////////////////////////////
	@Mod.Instance(ERC_Core.MODID)
    public static ERC_Core INSTANCE;
//    public static Item sampleGuiItem;
    public static final int GUIID_RailBase = 0;
	public static final int GUIID_RailModelConstructor = 1;
	public static final int GUIID_CoasterModelConstructor = 2;
//    public static final int GUIID_FerrisBasketConstructor = 2;
//    public static final int GUIID_FerrisCore = 3;
    
	////////////////////////////////////////////////////////////////
	//Creative Tab
	public static ERC_CreateCreativeTab ERC_Tab = new ERC_CreateCreativeTab("ExRC", itemCoaster);
	
	////////////////////////////////////////////////////////////////
	// TickEventProxy
	public static TickEventHandler tickEventHandler = null;
	
	////////////////////////////////////////////////////////////////
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		ERC_Logger.info("Start preInit");

		blockRailRenderId = proxy.getNewRenderType();
		ERC_PacketHandler.init();
		
		// Register TileEntity
		GameRegistry.registerTileEntity(TileEntityRailModelConstructor.class, "ERC:RailModelConstructor");
		GameRegistry.registerTileEntity(TileEntityRail.Normal.class, "ERC:TileEntityRail");
		GameRegistry.registerTileEntity(TileEntityRail.Accel.class, "ERC:TileEntityRailRedAcc");
		GameRegistry.registerTileEntity(TileEntityRail.Const.class, "ERC:TileEntityRailconstvel");
		GameRegistry.registerTileEntity(TileEntityRail.Detector.class, "ERC:TileEntityRailDetector");
		GameRegistry.registerTileEntity(TileEntityRail.Branch.class, "ERC:TileEntityRailBranch");
		GameRegistry.registerTileEntity(TileEntityRail.Invisible.class, "ERC:TileEntityInvisible");
		GameRegistry.registerTileEntity(TileEntityRail.AntiGravity.class, "ERC:TileEntityNonGravity");

		proxy.preInit();

		ERC_Logger.info("End preInit");
	}

	
	@EventHandler
	public void Init(FMLInitializationEvent e)
	{
		ERC_Logger.info("Start Init");

		proxy.init();
		
		//Register Entity
		int eid=100;
		EntityRegistry.registerModEntity(EntityCoaster.class, "erc:coaster", eid++, this, 400, 10, true);
		EntityRegistry.registerModEntity(EntityCoasterSeat.class, "erc:coaster:seat", eid++, this, 400, 20, true);
		EntityRegistry.registerModEntity(entitySUSHI.class, "erc:SUSHI", eid++, this, 200, 50, true);
//		EntityRegistry.registerModEntity(ERC_EntityCoasterMonodentate.class, "erc:coaster:mono", eid++, this, 200, 10, true);
//		EntityRegistry.registerModEntity(ERC_EntityCoasterDoubleSeat.class, "erc:coaster:double", eid++, this, 200, 10, true);
//		EntityRegistry.registerModEntity(ERC_EntityCoasterConnector.class, "erc:coaster:connect", eid++, this, 200, 10, true);

		// Register Items
		InitBlock_RC();
		InitItem_RC();
		
		// Register Recipe
		InitItemRecipe();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ERC_GUIHandler());
		
		ERC_Logger.info("End Init");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
	}
	////////////////////////////////////////////////////////////////
	
	private void InitBlock_RC()
	{
		railNormal
			.setBlockName("railNormal")
			.setBlockTextureName("iron_block")
			.setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railNormal, "ERC.Rail");
		
		railAccel
			.setBlockName("railAccel")
			.setBlockTextureName("redstone_block")
			.setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railAccel, "ERC.RailAccel");
		
		railConst
			.setBlockName("railConstVelocity")
			.setBlockTextureName("obsidian")
			.setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railConst, "ERC.RailConst");
		
		railDetect
			.setBlockName("railDetector")
			.setBlockTextureName("quartz_block_chiseled_top")
			.setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railDetect, "ERC.RailDetector");
		
		railBranch
			.setBlockName("railBranch")
			.setBlockTextureName("lapis_block")
			.setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railBranch, "ERC.RailBranch");
		
		railInvisible
			.setBlockName("railInvisible")
			.setBlockTextureName("glass")
			.setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railInvisible, "ERC.RailInvisible");

		railNonGravity
            .setBlockName("railNonGravity")
            .setBlockTextureName("portal")
            .setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railNonGravity, "ERC.RailNonGravity");

        railModelConstructor
            .setBlockName("TileEntityRailModelConstructor")
            .setBlockTextureName("TileEntityRailModelConstructor")
            .setCreativeTab(ERC_Tab);
        GameRegistry.registerBlock(railModelConstructor, "ERC.TileEntityRailModelConstructor");
	}
	
	private void InitItem_RC()
	{
		itemBasePipe.setCreativeTab(ERC_Tab);
		itemBasePipe.setUnlocalizedName("RailPipe");
		itemBasePipe.setTextureName(MODID+":railpipe");
		GameRegistry.registerItem(itemBasePipe, "railpipe");
		
		itemWrench.setCreativeTab(ERC_Tab);
		itemWrench.setUnlocalizedName("Wrench");
		itemWrench.setTextureName(MODID+":wrench_c1");
		itemWrench.setMaxStackSize(1);
		GameRegistry.registerItem(itemWrench, "Wrench");
		
		ItemStick.setCreativeTab(ERC_Tab);
		ItemStick.setUnlocalizedName("BlockPlacer");
		ItemStick.setTextureName(MODID+":wrench_p");
		ItemStick.setMaxStackSize(1);
		GameRegistry.registerItem(ItemStick, "ItemWrenchPlaceBlock");
		
		itemCoaster.setCreativeTab(ERC_Tab);
		itemCoaster.setUnlocalizedName("Coaster");
		itemCoaster.setTextureName(MODID+":coaster");
		itemCoaster.setMaxStackSize(10);
		GameRegistry.registerItem(itemCoaster, "Coaster");

//		ItemCoasterConnector.setCreativeTab(ERC_Tab);
//		ItemCoasterConnector.setUnlocalizedName("CoasterConnector");
//		ItemCoasterConnector.setTextureName(MODID+":coaster_c");
//		ItemCoasterConnector.setMaxStackSize(10);
//		GameRegistry.registerItem(ItemCoasterConnector, "CoasterConnector");

//		ItemCoasterMono.setCreativeTab(ERC_Tab);
//		ItemCoasterMono.setUnlocalizedName("CoasterMono");
//		ItemCoasterMono.setTextureName(MODID+":coaster");
//		ItemCoasterMono.setMaxStackSize(10);
//		GameRegistry.registerItem(ItemCoasterMono, "CoasterMono");
		
		ItemSwitchRailModel.setCreativeTab(ERC_Tab);
		ItemSwitchRailModel.setUnlocalizedName("SwitchRailModel");
//		ItemSwitchRailModel.setTextureName(MODID+":switchrail");
		ItemSwitchRailModel.setMaxStackSize(1);
		GameRegistry.registerItem(ItemSwitchRailModel, "SwitchRailModel");


		ItemRailBlockModelChanger
				.setUnlocalizedName("SwitchBlockRailModel")
				.setTextureName(MODID+":switchblockrailmodel")
				.setMaxStackSize(1);
		GameRegistry.registerItem(ItemRailBlockModelChanger, "SwitchBlockRailModel");

		ItemSUSHI.setCreativeTab(ERC_Tab);
		ItemSUSHI.setUnlocalizedName("ERCSUSHI");
		ItemSUSHI.setTextureName(MODID+":SUSHI");
		GameRegistry.registerItem(ItemSUSHI, "ItemSUSHI");
		
		
		ItemSmoothAll.setCreativeTab(ERC_Tab);
		ItemSmoothAll.setUnlocalizedName("ERCSmoothAll");
		ItemSmoothAll.setTextureName(MODID+":SmoothAll");
		GameRegistry.registerItem(ItemSmoothAll, "ItemSmoothAll");
	}

	
	private void InitItemRecipe()
	{
		GameRegistry.addRecipe(new ItemStack(itemBasePipe,2,0),
                " L ",
                "L L",
                " L ",
                'L',Items.iron_ingot
        );

		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot,2,0),
                "L",
                'L', itemBasePipe
        );
		
		GameRegistry.addRecipe(new ItemStack(railNormal,10,0),
				"P P",
				"PBP",
				"P P",
				'P', itemBasePipe,
				'B',Blocks.iron_block
		);

		GameRegistry.addRecipe(new ItemStack(railAccel,1,0),
				"R",
				"r",
				'R',railNormal,
				'r',Items.redstone
		);
		
		GameRegistry.addRecipe(new ItemStack(railConst,1,0),
				"R",
				"O",
				'R',railNormal,
				'O',Blocks.obsidian
		);
		
		GameRegistry.addRecipe(new ItemStack(railDetect,1,0),
				"R",
				"D",
				'R',railNormal,
				'D',Blocks.stone_pressure_plate
		);
		
		GameRegistry.addRecipe(new ItemStack(railBranch,1,0),
				"RLR",
				" R ",
				'L',Blocks.lever,
				'R',railNormal
		);
		
		GameRegistry.addRecipe(new ItemStack(railInvisible,1,0),
				"R",
				"G",
				'R',railNormal,
				'G',Blocks.glass
		);
		

		GameRegistry.addRecipe(new ItemStack(itemWrench,1,0),
				"PI ",
				"II ",
				"  I",
				'P', itemBasePipe,
				'I',Items.iron_ingot
		);
		
		GameRegistry.addRecipe(new ItemStack(ItemStick,1,0),
				"D  ",
				" P ",
				"  I",
				'D',Blocks.dirt,
				'P', itemBasePipe,
				'I',Items.stick
		);
		
		GameRegistry.addRecipe(new ItemStack(ItemSmoothAll,1,0),
				"B  ",
				" P ",
				"  I",
				'B',Items.book,
				'P', itemBasePipe,
				'I',Items.stick
		);
		// SUSHI
		GameRegistry.addRecipe(new ItemStack(ItemSUSHI,1,0),
				"F",
				"M",
				'F',Items.fish,
				'M',Items.wheat
		);
		
		GameRegistry.addRecipe(new ItemStack(itemCoaster,1,0),
				"I I",
				"PIP",
				'P', itemBasePipe,
				'I',Items.iron_ingot
		);
		
//		GameRegistry.addRecipe(new ItemStack(ItemCoasterMono,1,0),
//				"C",
//				"R",
//				'C', itemCoaster,
//				'R',Items.redstone
//		);
//
//		GameRegistry.addRecipe(new ItemStack(ItemCoasterConnector,1,0),
//				"FC",
//				'F',Blocks.tripwire_hook,
//				'C', itemCoaster
//		);
		
		
		GameRegistry.addRecipe(new ItemStack(ItemSwitchRailModel,1,0),
				" L ",
                "LRL",
                " L ",
				'R',Blocks.rail,
				'L', itemBasePipe
		);
		
		
		GameRegistry.addRecipe(new ItemStack(railNormal,10,0),
				"R R",
                "R R",
                "R R",
				'R',Blocks.rail
		);
		
		GameRegistry.addShapelessRecipe(new ItemStack(entryTicket,64),
				       "",
				       "Y",
				       "P",
				       'Y', new ItemStack(Items.dye, 1, 11),
				       'P', Items.paper
				       );
	}

	public static InputStream GetInModPackageFileStream(ResourceLocation resource) throws IOException
	{
        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
        return res.getInputStream();
	}
}

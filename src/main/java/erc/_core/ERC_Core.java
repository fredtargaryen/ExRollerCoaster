package erc._core;

import java.io.File;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import erc.block.*;
import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.entity.ERC_EntityCoasterDoubleSeat;
import erc.entity.ERC_EntityCoasterMonodentate;
import erc.entity.ERC_EntityCoasterSeat;
import erc.entity.entitySUSHI;
import erc.gui.ERC_GUIHandler;
import erc.handler.ERC_TickEventHandler;
import erc.item.ERC_ItemCoaster;
import erc.item.ERC_ItemCoasterConnector;
import erc.item.ERC_ItemCoasterMonodentate;
import erc.item.ERC_ItemSmoothAll;
import erc.item.ERC_ItemSwitchingRailModel;
import erc.item.ERC_ItemWrench;
import erc.item.ERC_ItemWrenchPlaceBlock;
import erc.item.itemSUSHI;
import erc.message.ERC_PacketHandler;
import erc.proxy.IProxy;
import erc.tileEntity.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod( 
		modid = ERC_Core.MODID, 
		name = "Ex Roller Coaster", 
		version = ERC_Core.VERSION,
		dependencies = "required-after:Forge@[10.12.1.1090,)",
		useMetadata = true
		)
public class ERC_Core {
	public static final String MODID = ERC_CONST.DOMAIN;
	public static final String VERSION = "1.41";

	
	//proxy////////////////////////////////////////
	@SidedProxy(clientSide = "erc.proxy.ERC_ClientProxy", serverSide = "erc.proxy.ERC_ServerProxy")
	public static IProxy proxy;
	
	//Blocks/////////////////////////////////////////
	public static Block railNormal = new blockRailNormal();
	public static Block railRedAccel = new blockRailRedstoneAccelerator();
	public static Block railConst = new blockRailConstVelocity();
	public static Block railDetect = new blockRailDetector();
	public static Block railBranch = new blockRailBranch();
	public static Block railInvisible = new blockRailInvisible();
	public static Block railNonGravity = new BlockNonGravityRail();
	
	//special block renderer ID
	public static int blockRailRenderId;
//	public static int blockFerrisSupporterRenderID;
	
	// items /////////////////////////////////////////
	public static Item ItemBasePipe = new Item();
	public static Item ItemWrench = new ERC_ItemWrench();
	public static Item ItemCoaster = new ERC_ItemCoaster();
	public static Item ItemCoasterConnector = new ERC_ItemCoasterConnector();
	public static Item ItemCoasterMono = new ERC_ItemCoasterMonodentate();
	public static Item ItemSwitchRail = new ERC_ItemSwitchingRailModel();
	public static Item ItemSUSHI = new itemSUSHI();
	public static Item entryTicket = new itemEntryTicket();
	public static Item ItemStick = new ERC_ItemWrenchPlaceBlock();
	public static Item ItemSmoothAll = new ERC_ItemSmoothAll();
	
	//GUI/////////////////////////////////////////
	@Mod.Instance(ERC_Core.MODID)
    public static ERC_Core INSTANCE;
//    public static Item sampleGuiItem;
    public static final int GUIID_RailBase = 0;
//    public static final int GUIID_FerrisConstructor = 1;
//    public static final int GUIID_FerrisBasketConstructor = 2;
//    public static final int GUIID_FerrisCore = 3;
    
	////////////////////////////////////////////////////////////////
	//Creative Tab
	public static ERC_CreateCreativeTab ERC_Tab = new ERC_CreateCreativeTab("ExRC", ItemBasePipe);
	
	////////////////////////////////////////////////////////////////
	// TickEventProxy
	public static ERC_TickEventHandler tickEventHandler = null;
	
	////////////////////////////////////////////////////////////////
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		ERC_Logger.info("Start preInit");

		blockRailRenderId = proxy.getNewRenderType();
		ERC_PacketHandler.init();
		
		// Register TileEntity
		GameRegistry.registerTileEntity(TileEntityRailBase.class, "ERC:TileEntityRailBase");
		GameRegistry.registerTileEntity(TileEntityRailNormal.class, "ERC:TileEntityRail");
		GameRegistry.registerTileEntity(TileEntityRailRedstoneAccelerator.class, "ERC:TileEntityRailRedAcc");
		GameRegistry.registerTileEntity(TileEntityRailConstVelosity.class, "ERC:TileEntityRailconstvel");
		GameRegistry.registerTileEntity(TileEntityRailDetector.class, "ERC:TileEntityRailDetector");
		GameRegistry.registerTileEntity(TileEntityRailBranch2.class, "ERC:TileEntityRailBranch");
		GameRegistry.registerTileEntity(TileEntityRailInvisible.class, "ERC:TileEntityInvisible");
		GameRegistry.registerTileEntity(TileEntityNonGravityRail.class, "ERC:TileEntityNonGravity");

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
		EntityRegistry.registerModEntity(ERC_EntityCoaster.class, "erc:coaster", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(ERC_EntityCoasterMonodentate.class, "erc:coaster:mono", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(ERC_EntityCoasterDoubleSeat.class, "erc:coaster:double", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(ERC_EntityCoasterSeat.class, "erc:coaster:seat", eid++, this, 400, 20, true);
		EntityRegistry.registerModEntity(ERC_EntityCoasterConnector.class, "erc:coaster:connect", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(entitySUSHI.class, "erc:SUSHI", eid++, this, 200, 50, true);

		//Register Items
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
		
		railRedAccel
			.setBlockName("railRedAccel")
			.setBlockTextureName("redstone_block")
			.setCreativeTab(ERC_Tab);
		GameRegistry.registerBlock(railRedAccel, "ERC.RailAccel");
		
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
	}
	
	private void InitItem_RC()
	{
		ItemBasePipe.setCreativeTab(ERC_Tab);
		ItemBasePipe.setUnlocalizedName("RailPipe");
		ItemBasePipe.setTextureName(MODID+":railpipe");
		GameRegistry.registerItem(ItemBasePipe, "railpipe");
		
		ItemWrench.setCreativeTab(ERC_Tab);
		ItemWrench.setUnlocalizedName("Wrench");
		ItemWrench.setTextureName(MODID+":wrench_c1");
		ItemWrench.setMaxStackSize(1);
		GameRegistry.registerItem(ItemWrench, "Wrench");
		
		ItemStick.setCreativeTab(ERC_Tab);
		ItemStick.setUnlocalizedName("BlockPlacer");
		ItemStick.setTextureName(MODID+":wrench_p");
		ItemStick.setMaxStackSize(1);
		GameRegistry.registerItem(ItemStick, "ItemWrenchPlaceBlock");
		
		ItemCoaster.setCreativeTab(ERC_Tab);
		ItemCoaster.setUnlocalizedName("Coaster");
		ItemCoaster.setTextureName(MODID+":coaster");
		ItemCoaster.setMaxStackSize(10);
		GameRegistry.registerItem(ItemCoaster, "Coaster");

		ItemCoasterConnector.setCreativeTab(ERC_Tab);
		ItemCoasterConnector.setUnlocalizedName("CoasterConnector");
		ItemCoasterConnector.setTextureName(MODID+":coaster_c");
		ItemCoasterConnector.setMaxStackSize(10);
		GameRegistry.registerItem(ItemCoasterConnector, "CoasterConnector");

		ItemCoasterMono.setCreativeTab(ERC_Tab);
		ItemCoasterMono.setUnlocalizedName("CoasterMono");
		ItemCoasterMono.setTextureName(MODID+":coaster");
		ItemCoasterMono.setMaxStackSize(10);
		GameRegistry.registerItem(ItemCoasterMono, "CoasterMono");
		
		ItemSwitchRail.setCreativeTab(ERC_Tab);
		ItemSwitchRail.setUnlocalizedName("SwitchRailModel");
//		ItemSwitchRail.setTextureName(MODID+":switchrail");
		ItemSwitchRail.setMaxStackSize(1);
		GameRegistry.registerItem(ItemSwitchRail, "SwitchRailModel");
		
		ItemSUSHI.setCreativeTab(ERC_Tab);
		ItemSUSHI.setUnlocalizedName("ERCSUSHI");
		ItemSUSHI.setTextureName(MODID+ ":" + SUSHI");
		GameRegistry.registerItem(ItemSUSHI, "ItemSUSHI");
		entryTicket.setCreativeTab(ERC_Tab);
		entryTicket.setunlocalizedName("entryTicket");
		entryTicket.setTextureName(MODID+":"+ "entryTicket");
		GameRegistry.registerItem(entryTicket, "entryTicket");
		
		
		ItemSmoothAll.setCreativeTab(ERC_Tab);
		ItemSmoothAll.setUnlocalizedName("ERCSmoothAll");
		ItemSmoothAll.setTextureName(MODID+":SmoothAll");
		GameRegistry.registerItem(ItemSmoothAll, "ItemSmoothAll");
	}

	
	private void InitItemRecipe()
	{

		GameRegistry.addRecipe(new ItemStack(ItemBasePipe,2,0),
                " L ",
                "L L",
                " L ",
                'L',Items.iron_ingot
        );
		
		
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot,2,0),
                "L",
                'L',ItemBasePipe
        );
				
		
		GameRegistry.addRecipe(new ItemStack(railNormal,10,0),
				"P P",
				"PBP",
				"P P",
				'P',ItemBasePipe,
				'B',Blocks.iron_block
		);

		GameRegistry.addRecipe(new ItemStack(railRedAccel,1,0),
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
		

		GameRegistry.addRecipe(new ItemStack(ItemWrench,1,0),
				"PI ",
				"II ",
				"  I",
				'P',ItemBasePipe,
				'I',Items.iron_ingot
		);
		
		GameRegistry.addRecipe(new ItemStack(ItemStick,1,0),
				"D  ",
				" P ",
				"  I",
				'D',Blocks.dirt,
				'P',ItemBasePipe,
				'I',Items.stick
		);
		
		GameRegistry.addRecipe(new ItemStack(ItemSmoothAll,1,0),
				"B  ",
				" P ",
				"  I",
				'B',Items.book,
				'P',ItemBasePipe,
				'I',Items.stick
		);
		// SUSHI
		GameRegistry.addRecipe(new ItemStack(ItemSUSHI,1,0),
				"F",
				"M",
				'F',Items.fish,
				'M',Items.wheat
		);
		
		GameRegistry.addRecipe(new ItemStack(ItemCoaster,1,0),
				"I I",
				"PIP",
				'P',ItemBasePipe,
				'I',Items.iron_ingot
		);
		
		GameRegistry.addRecipe(new ItemStack(ItemCoasterMono,1,0),
				"C",
				"R",
				'C',ItemCoaster,
				'R',Items.redstone
		);
		
		GameRegistry.addRecipe(new ItemStack(ItemCoasterConnector,1,0),
				"FC",
				'F',Blocks.tripwire_hook,
				'C',ItemCoaster
		);
		
		
		GameRegistry.addRecipe(new ItemStack(ItemSwitchRail,1,0),
				" L ",
                "LRL",
                " L ",
				'R',Blocks.rail,
				'L',ItemBasePipe
		);
		
		
		GameRegistry.addRecipe(new ItemStack(railNormal,10,0),
				"R R",
                "R R",
                "R R",
				'R',Blocks.rail
		);
		
		GameRegistry.addShapelessRecipe(new ItemStack(entryTicket, 64),
		new Object[] { Items.paper, new ItemStack(Items.dye, 1, 11) });
	}
}

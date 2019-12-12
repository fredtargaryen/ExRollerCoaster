package erc.gui;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import erc._core.ERC_CONST;
import erc.gui.container.DefContainer;
import erc.manager.ERC_CoasterAndRailManager;
import erc.message.ERC_MessageRailGUICtS;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GUIRail extends GuiContainer {
	
	public enum editFlag{
		CONTROLPOINT, POW, ROTRED, ROTGREEN, ROTBLUE, SMOOTH, RESET, SPECIAL, RailModelIndex
	}
	
    private static final ResourceLocation TEXTURE = new ResourceLocation(ERC_CONST.DOMAIN, "textures/gui/gui.png");
    private static int buttonid;
    private static final int buttonidoffset = 0;
    private boolean clickedTileExists;
    
    int offsetx;
    int offsety;
    
    class GUIName{
		String name;
		int x;
		int y;
		int flag;
		int baseID;
		GUIName(String str,int x, int y, editFlag flag, int base){name=str; this.x=x; this.y=y; this.flag = flag.ordinal(); this.baseID = base;}
//		Function<float, float> func
	}
    Map<Integer, GUIName> GUINameMap = new HashMap<Integer, GUIName>();
    
    
    public GUIRail(int x, int y, int z)
    {
        super(new DefContainer(x, y, z, null));
    	xSize = 100;
    	ySize = 230;
    }
 
    // GUI���J�����ьĂ΂�鏉�����֐�
	@Override
	public void initGui()
    {
		super.initGui();
		GUINameMap.clear();
		this.guiLeft = this.width*7/8 - xSize/2;
		buttonid = buttonidoffset; // 0
		// �{�^���o�^
		offsetx = this.guiLeft + 20;
		offsety = this.guiTop;
//		int offset = 4;
//        int length = 27;
        
        addButton2("ControlPoint", editFlag.CONTROLPOINT);
        addButton4("Power", editFlag.POW);
        
        addButton4("Rotation", editFlag.ROTRED);
        addButton4("", editFlag.ROTGREEN, 14);
        addButton4("", editFlag.ROTBLUE, 14);
        
		addButton1(60, 13, "smooth", editFlag.SMOOTH);
		addButton1(60, 13, "Reset rot", editFlag.RESET);

		if(ERC_CoasterAndRailManager.clickedTileForGUI == null) {
            this.clickedTileExists = false;
        } else {
		    this.clickedTileExists = true;
            ERC_CoasterAndRailManager.clickedTileForGUI.SpecialGUIInit(this);
        }
    }
	@SuppressWarnings("unchecked")
    public void addButton1(int lenx, int leny, String str, editFlag flag)
    {
		offsety+=17;
    	this.buttonList.add(new GuiButtonExt(buttonid++, offsetx, offsety, lenx, leny, str));
    	GUINameMap.put(buttonid-1, new GUIName("",-100,-100,flag, -1));
    }
	@SuppressWarnings("unchecked")
	public void addButton2(String str, editFlag flag)
    {
		offsety+=27;
    	this.buttonList.add(new GuiButtonExt(buttonid++, offsetx, offsety, 18, 13, "-"));
    	this.buttonList.add(new GuiButtonExt(buttonid++, offsetx+42, offsety, 18, 13, "+"));
    	GUIName data = new GUIName(str,5,offsety-10-guiTop,flag,buttonid-2);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    }
	
	public void addButton4(String str, editFlag flag)
	{
		addButton4(str, flag, 27);
	}
    @SuppressWarnings("unchecked")
    public void addButton4(String str, editFlag flag, int yshift)
    {
    	offsety+=yshift;
    	this.buttonList.add(new GuiButtonExt(buttonid++, offsetx-13 , offsety, 17, 13, "<<"));
		this.buttonList.add(new GuiButtonExt(buttonid++, offsetx+6, offsety, 13, 13, "<"));
		this.buttonList.add(new GuiButtonExt(buttonid++, offsetx+41, offsety, 13, 13, ">"));
		this.buttonList.add(new GuiButtonExt(buttonid++, offsetx+56, offsety, 17, 13, ">>"));
		GUIName data = new GUIName(str,5,offsety-10-guiTop,flag,buttonid-4);
		GUINameMap.put(buttonid-4, data);
    	GUINameMap.put(buttonid-3, data);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    }
    
    
    @Override
	public void onGuiClosed()
    {
		super.onGuiClosed();
		ERC_CoasterAndRailManager.CloseRailGUI();
	}
    
	/*GUI�̕������̕`�揈��*/
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
       	
//        int offset = 4;
//        int length = 27;
//        this.fontRendererObj.drawString("Control Point", 	5, offset, 0x404040);
//        this.fontRendererObj.drawString("Power", 			5, offset+1*length, 0x404040);
//        this.fontRendererObj.drawString("Rotation Red", 	5, offset+2*length, 0x404040);
//        this.fontRendererObj.drawString("Rotation Green", 	5, offset+3*length, 0x404040);
//        this.fontRendererObj.drawString("Rotation Blue", 	5, offset+4*length, 0x404040);
//        
        for(GUIName g :  GUINameMap.values())
        {
        	this.fontRenderer.drawString(g.name,g.x,g.y,0x404040);
        }
        
        if(this.clickedTileExists) {
            drawString(this.fontRenderer, "" + ERC_CoasterAndRailManager.clickedTileForGUI.GetPosNum(), 43, 29, 0xffffff);
            drawString(this.fontRenderer, String.format("% 2.1f", ERC_CoasterAndRailManager.clickedTileForGUI.BaseRail.Power), 37, 56, 0xffffff);
            drawString(this.fontRenderer, ERC_CoasterAndRailManager.clickedTileForGUI.SpecialGUIDrawString(), 42, 172, 0xffffff);
        }
    }
 
    /*GUI�̔w�i�̕`�揈��*/
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ)
    {
        if (this.mc.world != null)
        {
            this.drawGradientRect(this.width*3/4, 0, this.width, this.height, -1072689136, -804253680);
        }
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.ERCRail_drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
    }
    
    public void ERCRail_drawTexturedModalRect(int x, int y, int z, int v, int width, int height)
    {
        float f = 1f/(float)width;
        float f1 = 1f/(float)height;
        BufferBuilder wr = Tessellator.getInstance().getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(z + 0) * f), (double)((float)(v + height) * f1)).endVertex();
        wr.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex(((double)((float)(z + width) * f)), (double)((float)(v + height) * f1)).endVertex();
        wr.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(z + width) * f), (double)((float)(v + 0) * f1)).endVertex();
        wr.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(z + 0) * f), (double)((float)(v + 0) * f1)).endVertex();
        Tessellator.getInstance().draw();
    }
 
    /*GUI���J���Ă��鎞�ɃQ�[���̏������~�߂邩�ǂ����B*/
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

	@Override
	protected void actionPerformed(GuiButton button) {
        if(this.clickedTileExists) {
            TileEntityRailBase entity = ERC_CoasterAndRailManager.clickedTileForGUI;

            GUIName obj = GUINameMap.get(button.id);
            int data = (button.id - obj.baseID);
            ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS((int) entity.getXcoord(), (int) entity.getYcoord(), (int) entity.getZcoord(),
                    obj.flag, data);
            ERC_PacketHandler.INSTANCE.sendToServer(packet);
        }
	}
    
    
}

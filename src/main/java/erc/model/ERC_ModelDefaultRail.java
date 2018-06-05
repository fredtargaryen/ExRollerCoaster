package erc.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;

import erc._core.ERC_Logger;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class ERC_ModelDefaultRail extends Wrap_RailRenderer {
	
	private int PosNum;
	Vec3d posArray[];
	Vec3d normalArray[];
	
	public ERC_ModelDefaultRail(){} //���[�h����t�@�C�������w��C���X�^���X�������ۂł���H
	
	public ERC_ModelDefaultRail(OBJModel Obj, ResourceLocation Tex){}
	
//	public void render(double x, double y, double z, double yaw, double pitch, double roll, double length) 
//	{
// 		GL11.glPushMatrix();
//		GL11.glTranslated(x, y, z);
//// 		if(coaster.ERCPosMat != null)
////		{
//// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
////		}
//		GL11.glRotated(yaw, 0, -1, 0);
// 		GL11.glRotated(pitch,1, 0, 0);
// 		GL11.glRotated(roll, 0, 0, 1);
//
//		GL11.glScaled(1.0, 1.0, length);
//		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
//		this.renderModel();
//		GL11.glPopMatrix();
//	}

	public void setModelNum(int PosNum_org)
	{
		PosNum = PosNum_org;
		posArray = new Vec3d[PosNum_org*4];
		normalArray = new Vec3d[PosNum_org];
		for(int i=0;i<PosNum*4;++i)posArray[i] = new Vec3d(0, 0, 0);
		for(int i=0;i<PosNum;++i)normalArray[i] = new Vec3d(0, 0, 0);
	}
	
	public void construct(int idx, Vec3d Pos, Vec3d Dir, Vec3d Cross, float exParam)
	{
		int j = idx*4;
		double t1 = 0.4 + 0.1;
		double t2 = 0.4 - 0.1;
		
		if(j>=posArray.length)
		{
			ERC_Logger.warn("ERC_DefaultRailModel : index exception");
			return;
		}
		
		// ��
		posArray[j  ] = new Vec3d(	Pos.x - Cross.x*t1,
									Pos.y - Cross.y*t1,
									Pos.z - Cross.z*t1);
		posArray[j+1] = new Vec3d(	Pos.x - Cross.x*t2,
									Pos.y - Cross.y*t2,
									Pos.z - Cross.z*t2);
		// �E 
		posArray[j+2] = new Vec3d(	Pos.x + Cross.x*t2,
									Pos.y + Cross.y*t2,
									Pos.z + Cross.z*t2);
		posArray[j+3] = new Vec3d(	Pos.x + Cross.x*t1,
									Pos.y + Cross.y*t1,
									Pos.z + Cross.z*t1);
		
		normalArray[idx] = Dir.crossProduct(Cross).normalize();
	}

	public void render(Tessellator tess)
	{
		float turnflag = 0f;
		BufferBuilder bb = tess.getBuffer();
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
		
		for(int i = 0; i<PosNum; ++i)
		{
			int index = i*4;
			bb.pos(posArray[index].x, posArray[index].y, posArray[index].z).tex(0.0d, turnflag).endVertex();
			bb.pos(posArray[index+1].x, posArray[index+1].y, posArray[index+1].z).tex(1.0d, turnflag).endVertex();
			turnflag = turnflag>0?0f:1f;
			//Original method call was tess.setNormal. How to replace this?-FT
			//bb.putNormal((float)normalArray[i].x, (float)normalArray[i].y, (float)normalArray[i].z);
		}
		tess.draw();
		turnflag = 0f;
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
		for(int i = 0; i<PosNum; ++i)
		{
			int index = i*4+2;
			bb.pos(posArray[index].x, posArray[index].y, posArray[index].z).tex(0.0d, turnflag).endVertex();
			bb.pos(posArray[index+1].x, posArray[index+1].y, posArray[index+1].z).tex(1.0d, turnflag).endVertex();
			turnflag = turnflag>0?0f:1f;
			//Original method call was tess.setNormal. How to replace this?-FT
			//bb.putNormal((float)normalArray[i].x, (float)normalArray[i].y, (float)normalArray[i].z);
		}
		tess.draw();
	}
}
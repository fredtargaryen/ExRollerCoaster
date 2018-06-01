package erc.model;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.FMLClientHandler;
import erc.math.ERC_MathHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class ERC_ModelAddedRail extends Wrap_RailRenderer {
	
	private OBJModel modelRail;
	private ResourceLocation TextureResource;
	private int ModelNum;
	private Vec3d[] pos;
	private Vec3d[] rot;
	private float[] Length;
	
	@SuppressWarnings("unused")
	private ERC_ModelAddedRail(){} //���[�h����t�@�C�������w��C���X�^���X�������ۂł���H
	
	public ERC_ModelAddedRail(OBJModel Obj, ResourceLocation Tex)
	{
		modelRail = Obj;
		TextureResource = Tex;
	}
	
	private void renderModel() 
	{
		//modelRail.renderAll();
	}
	
	public void render(double x, double y, double z, double yaw, double pitch, double roll, double length) 
	{
 		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
// 		if(coaster.ERCPosMat != null)
//		{
// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
//		}
		GL11.glRotated(yaw, 0, -1, 0);
 		GL11.glRotated(pitch,1, 0, 0);
 		GL11.glRotated(roll, 0, 0, 1);

		GL11.glScaled(1.0, 1.0, length);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
		this.renderModel();
		GL11.glPopMatrix();
	}

	public void setModelNum(int PosNum_org)
	{
		ModelNum = PosNum_org-1;
		pos = new Vec3d[ModelNum];
		rot = new Vec3d[ModelNum];
		Length = new float[ModelNum];
		for(int i=0;i<ModelNum;++i)pos[i] = new Vec3d(0, 0, 0);
		for(int i=0;i<ModelNum;++i)rot[i] = new Vec3d(0, 0, 0);
	}
	
	public void construct(int idx, Vec3d Pos, Vec3d Dir, Vec3d Cross, float exParam)
	{
		if(idx>=ModelNum)return;
		// �ʒu
		pos[idx] = Pos;
		// �p�x
		Vec3d crossHorz = new Vec3d(0, 1, 0).crossProduct(Dir);
		Vec3d dir_horz = new Vec3d(Dir.x, 0, Dir.z);
		rot[idx] = new Vec3d(	-Math.toDegrees( Math.atan2(Dir.x, Dir.z)),
								Math.toDegrees( ERC_MathHelper.angleTwoVec3(Dir, dir_horz) * (Dir.y>0?-1f:1f)),
								Math.toDegrees( ERC_MathHelper.angleTwoVec3(Cross, crossHorz) * (Cross.y>0?1f:-1f) ));
		// ����
		Length[idx] = exParam;
	}

	public void render(Tessellator tess)
	{
		for(int i=0;i<ModelNum;++i)
		render(pos[i].x, pos[i].y, pos[i].z,
				rot[i].x, rot[i].y, rot[i].z, Length[i]);
	}
}
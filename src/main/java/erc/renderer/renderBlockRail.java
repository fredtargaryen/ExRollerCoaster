//package erc.renderer;
//
//import org.lwjgl.opengl.GL11;
//import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import erc._core.ERC_Core;
//import net.minecraft.block.Block;
//import net.minecraft.client.renderer.RenderBlocks;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.world.IBlockAccess;
//
//
//@SideOnly(Side.CLIENT)
//public class renderBlockRail implements ISimpleBlockRenderingHandler
//{
//	//�C���x���g������
//	@Override
//	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
//	{
//		if (modelId == this.getRenderId())
//		{
//			Tessellator tessellator = Tessellator.instance;
//
//			//�R�R��������ƃu���b�N�̑傫�����ς��
//			renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);
//			//�`��ʒu�̒����B�R�R��������ƁA���S�Ƀ����_�[�����Ă�����A�V�ׂ�
//			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
//			//���R:�c �A�R�s�y���@RenderBlocks�݂Ă�
//			tessellator.startDrawingQuads();
//			tessellator.setNormal(0.0F, -1.0F, 0.0F);
//			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
//			tessellator.draw();
//			tessellator.startDrawingQuads();
//			tessellator.setNormal(0.0F, 1.0F, 0.0F);
//			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
//			tessellator.draw();
//			tessellator.startDrawingQuads();
//			tessellator.setNormal(0.0F, 0.0F, -1.0F);
//			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
//			tessellator.draw();
//			tessellator.startDrawingQuads();
//			tessellator.setNormal(0.0F, 0.0F, 1.0F);
//			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
//			tessellator.draw();
//			tessellator.startDrawingQuads();
//			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
//			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
//			tessellator.draw();
//			tessellator.startDrawingQuads();
//			tessellator.setNormal(1.0F, 0.0F, 0.0F);
//			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
//			tessellator.draw();
//			//�`��ʒu�̒����B�V�񂾌�͂��ЂÂ�
//			//��̃��c��GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
//			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
//		}
//	}
//
//	//���[���h�ł̃����_�[
//	@Override
//	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
//	{
//		if (modelId == this.getRenderId())
//		{
//			//�R�R��������΃u���b�N�̑傫�����ς��B
//			//renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);
//			int meta = world.getBlockMetadata(x, y, z);
//			switch(meta&7){
//			case 0: // ��
//				renderer.setRenderBounds(0.2F, 0.7F, 0.2F, 0.8F, 1.0F, 0.8F);
//	            break;
//			case 1: // ��
//				renderer.setRenderBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.3F, 0.8F);
//	            break;
//			case 2: // ��
//				renderer.setRenderBounds(0.2F, 0.2F, 0.7F, 0.8F, 0.8F, 1.0F);
//	            break;
//			case 3: // �k
//				renderer.setRenderBounds(0.2F, 0.2F, 0.0F, 0.8F, 0.8F, 0.3F);
//	            break;
//			case 4: // ��
//				renderer.setRenderBounds(0.7F, 0.2F, 0.2F, 1.0F, 0.8F, 0.8F);
//	            break;
//			case 5: // ��
//				renderer.setRenderBounds(0.0F, 0.2F, 0.2F, 0.3F, 0.8F, 0.8F);
//	            break;
//	        default:
//	        	renderer.setRenderBounds(0.4F, 0.4F, 0.4F, 0.6F, 0.6F, 0.6F);
//	            break;
//			}
//			renderer.renderStandardBlock(block, x, y, z);
//			return true;
//		}
//		return false;
//	}
//
//	//�C���x���g���̃����_�[���ʓ|�������Ȃ�A�R�R��false�ɁB�e�N�X�`�������\�������悤�ɂȂ�
//	@Override
//	public boolean shouldRender3DInInventory(int modelId)
//	{
//		return true;
//	}
//
//	//�����_�[ID��Ԃ�
//	@Override
//	public int getRenderId()
//	{
//		return ERC_Core.blockRailRenderId;
//	}
//
//}
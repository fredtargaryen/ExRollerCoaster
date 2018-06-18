package erc.renderer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3f;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Placeholder object renderer, until I find something better.
 * Assuming the object uses only triangle polygons.
 * Using GL_TRIANGLES only renders some of the triangles.
 * GL_TRIANGLE_STRIP makes the model look more complete, but is obviously still ugly.
 */
public class ModelRenderer {
    /**
     * Assumes a complete OBJModel with triangle polygons, so it can use GL_TRIANGLES to render
     * Necessary transforms must be done before calling this.
     * @param model
     */
    public static void renderObj(OBJModel model) {
        //Prepare to draw
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //Yes it's deprecated but I don't know any alternatives
        Collection<OBJModel.Group> groups = model.getMatLib().getGroups().values();
        Iterator<OBJModel.Group> iterGroup = groups.iterator();
        vertexbuffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        while(iterGroup.hasNext()) {
            OBJModel.Group g = iterGroup.next();
            Set<OBJModel.Face> faces = g.getFaces();
            Iterator<OBJModel.Face> iterFace = faces.iterator();
            while(iterFace.hasNext()) {
                OBJModel.Face f = iterFace.next();
                OBJModel.Vertex[] vertices = f.getVertices();
                Vector3f pos;
                OBJModel.Normal norm;
                OBJModel.TextureCoordinate uv;
                for(int i = 0; i < vertices.length; ++i) {
                    pos = vertices[i].getPos3();
                    norm = vertices[i].getNormal();
                    uv = vertices[i].getTextureCoordinate();
                    //OBJModel: May need to use 1 - uv.v instead
                    vertexbuffer.pos(pos.x, pos.y, pos.z).tex(uv.u, uv.v).normal(norm.x, norm.y, norm.z).endVertex();
                }
            }
        }

        tessellator.draw();
        //Clear up
        GlStateManager.enableLighting();
    }
}

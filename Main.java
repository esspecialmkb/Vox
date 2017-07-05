/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.Control;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Michael B.
 */
public class Chunk implements Control{
    Vector3f chunkPos;
    AbstractBlock[][][] blocks;
    int chunkX = 0;
    int chunkY = 0;
    int chunkZ = 0;
    Geometry chunkGeo; //REPLACE NODE WITH GEOMETRY
    Node node;
    Mesh blockMesh;
    VoxMesh chunkMesh;

    int chunkSize = 4;
    //ADD FLAG FOR CHUNK MESH UPDATES
    boolean meshUpdate;
    int chunkStatus;
    //MATERIAL FOR CHUNK
    Material mat;

    //METHOD TO CREATE CHUNK MESH
    // <<CANDIDATE FOR MULTITHREADING>>
    //The scene graph is updated here
    public void updateChunk(float tpf){
        Vector3Int blockPos = new Vector3Int(0,0,0);
        meshUpdate = false;
        if(chunkStatus == 0){
            chunkMesh.initMesh();

        }else{
            chunkMesh.resetMesh();
            chunkGeo.removeFromParent();
        }

        for(int x = 0; x< chunkSize; x++){
            for(int y = 0; y< chunkSize; y++){
                for(int z = 0; z< chunkSize; z++){
                    chunkMesh = blocks[x][y][z].buildBlockFaces(this, new Vector3f(x, y, z));
                }
            }
        }

        blockMesh = chunkMesh.buildBlockMesh();

        if(chunkStatus == 0){
            chunkGeo = new Geometry("Chunk",blockMesh);
            chunkGeo.setMaterial(mat);
            node.attachChild(chunkGeo);                
            chunkStatus = 1;
        }else{
            chunkGeo.setMesh(blockMesh);
            chunkGeo.setMaterial(mat);
            node.attachChild(chunkGeo);
        }
    }
    
    // The initChunkPosition method allows us to define the chunk's location
    public void initChunkPosition(int x, int y, int z, AssetManager assetManager){
        chunkX = x * chunkSize;
        chunkY = y * chunkSize;
        chunkZ = z * chunkSize;
        
        initChunk(assetManager);
    }

    public void initChunk(AssetManager assetManager){
        blocks = new AbstractBlock[chunkSize][chunkSize][chunkSize];
        for(int x=0;x<chunkSize;x++){
            for(int y=0;y<chunkSize;y++){
                for(int z=0;z<chunkSize;z++){
                    if(y == 0){
                        blocks[x][y][z] = new BasicBlock();
                    }else{
                        blocks[x][y][z] = new AirBlock();
                    }

                }
            }
        }

        //blocks[0][0][0] = new BasicBlock();

        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);

        chunkMesh = new VoxMesh();
        //chunkMesh.initMesh();
        node = new Node();
        node.addControl(this);
        
        node.setLocalTranslation(chunkX, chunkY, chunkZ);
        meshUpdate = true;
        chunkStatus = 0;
    }

    public AbstractBlock getBlock(Vector3f blocPos){
        //if((inRange(blocPos.x ) == true) && (inRange(blocPos.y ) == true) && (inRange(blocPos.z ) == true)){
        if(blocPos.x >= 0  && blocPos.x < 4){
            if(blocPos.y >= 0  && blocPos.y < 4){
                if(blocPos.z >= 0  && blocPos.z < 4){
                    return blocks[(int)blocPos.x][(int)blocPos.y][(int)blocPos.z];
                }
            }
        }
              
        //}
        return new AirBlock();
    }

    public boolean inRange(float blocPos){
        if(((blocPos < chunkSize) && (blocPos > 0)) == true){
            return true;
        }
        return false;
    }
    
    // The convertLocalPos method will take global coordinates and convert them
    //into local coordinates
    public Vector3f convertLocalPos(float x, float y, float z){
        Vector3f result = new Vector3f(x % this.chunkSize, y % this.chunkSize, z % this.chunkSize);
        return result;
    }
    
    // The inChunkRange method tests to see if the position that we are checking is 
    //actually within this chunk
    // If the position is not in this chunk, then we will need to access all of the chunks
    public boolean inChunkRange(float x, float y, float z){
        if(((Math.floor(x / chunkSize)) == chunkX)){
            if(((Math.floor(y / chunkSize)) == chunkY)){
                if(((Math.floor(z / chunkSize)) == chunkZ)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSpatial(Spatial spatial) {
        //ADDING TEST IMPLEMENTATION TO SETSPATIAL METHOD
        if(spatial != null){
            //Initialize
        }else{
            //Cleanup
        }
    }

    public void update(float tpf) {
        if(meshUpdate == true){
            updateChunk(tpf);
        }
    }

    public void render(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

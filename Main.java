package mygame;

import com.jme3.app.SimpleApplication;
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
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * test
 * @author Michael Bradford
 */
public class Main extends SimpleApplication {
    //OLD MEMBERS
    Vector3f blocPos;
    //ArrayList<Vector3f> positions;
    //ArrayList<Vector2f> tCoords;
    //ArrayList<Short> indexList;
    //ArrayList<Float> normals;
    short offset;
    
    Mesh blockMesh;
    Geometry geo;
    
    //NEW MEMBERS
    Chunk chunk;
    
    public class Vector3Int{
        public int x;
        public int y;
        public int z;
        
        public Vector3Int(int vx, int vy, int vz){
            x = vx;
            y = vy;
            z = vz;
        }
        public Vector3Int set(int vx, int vy, int vz){
            x = vx;
            y = vy;
            z = vz;
            return this;
        }
        public Vector3f toFloat(){
            return new Vector3f(x,y,z);
        }
    }
    
    public class VoxMesh{
        ArrayList<Vector3f> positions;
        ArrayList<Vector2f> tCoords;
        ArrayList<Short> indexList;
        ArrayList<Float> normals;
        short offset;
        
        //INIT ARRAYLISTS
        public void initMesh(){
            positions = new ArrayList();
            indexList = new ArrayList();
            normals = new ArrayList();
            tCoords = new ArrayList();
            offset = 0;
        }
        //CLEAR ARRAYLISTS
        public void resetMesh(){
            positions.clear();
            tCoords.clear();
            indexList.clear();
            normals.clear();
            offset = 0;
        }
        
        public void updateIndex(){
            offset = (short)positions.size();
        }
        
        public void addVertex(Vector3f pos){
            positions.add(pos);
        }
        public void addTriangle(short a, short b, short c){
            indexList.add((short)(a));
            indexList.add((short)(b));
            indexList.add((short)(c));
            //offset = (short) positions.size();
        }
        public void addNormal(float nx, float ny, float nz){
            normals.add(nx);
            normals.add(ny);
            normals.add(nz);
        }
        
        public Mesh buildBlockMesh() {
            Mesh mesh = new Mesh();

            Vector3f[] pVertices = new Vector3f[positions.size()];
            Iterator<Vector3f> positionsIterator = positions.iterator();
            for(int i=0;positionsIterator.hasNext();i++){
                pVertices[i] = positionsIterator.next();
            }

            short[] indices = new short[indexList.size()];
            Iterator<Short> indicesIterator = indexList.iterator();
            for(int i=0;indicesIterator.hasNext();i++){
                indices[i] = indicesIterator.next();
            }

            //float[] fNormals = new float[normals.size()]; 
            Vector3f[] pNormals = new Vector3f[normals.size()/3];
            Iterator<Float> normalsIterator = normals.iterator();
            for(int i=0;normalsIterator.hasNext();i++){
                pNormals[i] = new Vector3f(normalsIterator.next(),normalsIterator.next(),normalsIterator.next());
            }
            //Vector2f[] pTCoors = new Vector2f[tCoords.size()];

            //mesh.setDynamic();



            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pVertices));
            mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createShortBuffer(indices));
            mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(pNormals));
            //mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(pTCoors));
            mesh.updateBound();
            mesh.updateCounts();

            System.out.println(mesh.getTriangleCount() + " Tris, " + mesh.getVertexCount() + " Verts");
            return mesh;
        }
    }
    
    public interface AbstractBlock{
        boolean isSolid = false;
        boolean isFloor = false;
        
        enum Direction{
            Up,
            Down,
            Left,
            Right,
            Front,
            Back
        }
        
        public VoxMesh buildBlockFaces(Chunk chunk, Vector3f blockPos);
        public VoxMesh genTopFace(VoxMesh chunk, Vector3f blocPos);
        public VoxMesh genBottomFace(VoxMesh chunk, Vector3f blocPos);
        public VoxMesh genFrontFace(VoxMesh chunk, Vector3f blocPos);
        public VoxMesh genBackFace(VoxMesh chunk, Vector3f blocPos);
        public VoxMesh genRightFace(VoxMesh chunk, Vector3f blocPos);
        public VoxMesh genLeftFace(VoxMesh chunk, Vector3f blocPos);
        public boolean isSolid(Direction direction);
    }
    
    public class BasicBlock implements AbstractBlock{
        
        @Override
        public VoxMesh buildBlockFaces(Chunk chunk, Vector3f blockPos){
            //NEW LINES
            if( chunk.getBlock(blockPos.add(0,1,0)).isSolid(Direction.Down) == false ){ //-> TOP
                genTopFace(chunk.chunkMesh, blockPos);
            }
            
            if( chunk.getBlock(blockPos.add(0,-1,0)).isSolid(Direction.Up) == false ){ //-> BOTTOM
                genBottomFace(chunk.chunkMesh, blockPos);
            }

            if( chunk.getBlock(blockPos.add(1,0,0)).isSolid(Direction.Right) == false ){ //-> LEFT
                genLeftFace(chunk.chunkMesh, blockPos);
            }

            if( chunk.getBlock(blockPos.add(-1,0,0)).isSolid(Direction.Left) == false ){ //-> RIGHT
                genRightFace(chunk.chunkMesh, blockPos);
            }

            if( chunk.getBlock(blockPos.add(0,0,1)).isSolid(Direction.Back) == false ){ // -> FRONT
                genFrontFace(chunk.chunkMesh, blockPos);
            }

            if( chunk.getBlock(blockPos.add(0,0,-1)).isSolid(Direction.Front) == false ){ //-> BACK
                genBackFace(chunk.chunkMesh, blockPos);
            }
            
            return chunk.chunkMesh;
        }
        @Override
        public VoxMesh genTopFace(VoxMesh chunk, Vector3f blocPos){
            chunk.addVertex(blocPos.add(0,1,1));
            chunk.addVertex(blocPos.add(1,1,1));
            chunk.addVertex(blocPos.add(0,1,0));
            chunk.addVertex(blocPos.add(1,1,0));
            
            chunk.addTriangle((short)(2 + chunk.offset), (short)(0 + chunk.offset), (short)(1 + chunk.offset));
            chunk.addTriangle((short)(1 + chunk.offset), (short)(3 + chunk.offset), (short)(2 + chunk.offset));
            
            //  Add the number of verts we added to offset
            chunk.updateIndex();

            //  Vertex Normals
            chunk.addNormal(0f, 1f, 0f);
            chunk.addNormal(0f, 1f, 0f);

            //  Texture Co-ordinates
            chunk.tCoords.add(new Vector2f(0,1));
            chunk.tCoords.add(new Vector2f(1,1));
            chunk.tCoords.add(new Vector2f(1,0));
            chunk.tCoords.add(new Vector2f(0,0));
            
            return chunk;
        };
        @Override
        public VoxMesh genBottomFace(VoxMesh chunk, Vector3f blocPos){
            //  Vertex positions of the face
            chunk.addVertex(blocPos.add(0,0,1));
            chunk.addVertex(blocPos.add(1,0,1));
            chunk.addVertex(blocPos.add(0,0,0));
            chunk.addVertex(blocPos.add(1,0,0));

            //  Triangle Indices
            chunk.addTriangle((short)(1 + chunk.offset), (short)(0 + chunk.offset), (short)(2 + chunk.offset));
            chunk.addTriangle((short)(2 + chunk.offset), (short)(3 + chunk.offset), (short)(1 + chunk.offset));

            //  Add the number of verts we added to offset
            chunk.updateIndex();

            //  Vertex Normals
            chunk.addNormal(0f, -1f, 0f);

            //  Texture Co-ordinates
            chunk.tCoords.add(new Vector2f(0,1));
            chunk.tCoords.add(new Vector2f(1,1));
            chunk.tCoords.add(new Vector2f(1,0));
            chunk.tCoords.add(new Vector2f(0,0));
            
            return chunk;
        };
        @Override
        public VoxMesh genFrontFace(VoxMesh chunk, Vector3f blocPos){
            //  Vertex positions of the face
            chunk.addVertex(blocPos.add(0,1,1));
            chunk.addVertex(blocPos.add(1,1,1));
            chunk.addVertex(blocPos.add(0,0,1));
            chunk.addVertex(blocPos.add(1,0,1));

            //  Triangle Indices
            chunk.addTriangle((short)(1 + chunk.offset), (short)(0 + chunk.offset), (short)(2 + chunk.offset));
            chunk.addTriangle((short)(2 + chunk.offset), (short)(3 + chunk.offset), (short)(1 + chunk.offset));

            //  Add the number of verts we added to offset
            chunk.updateIndex();

            //  Vertex Normals
            chunk.addNormal(0f, 0f, 1f);

            //  Texture Co-ordinates
            chunk.tCoords.add(new Vector2f(0,1));
            chunk.tCoords.add(new Vector2f(1,1));
            chunk.tCoords.add(new Vector2f(1,0));
            chunk.tCoords.add(new Vector2f(0,0));
            
            return chunk;
        };
        @Override
        public VoxMesh genBackFace(VoxMesh chunk, Vector3f blocPos){
            //  Vertex positions of the face
            chunk.addVertex(blocPos.add(0,1,0));
            chunk.addVertex(blocPos.add(1,1,0));
            chunk.addVertex(blocPos.add(0,0,0));
            chunk.addVertex(blocPos.add(1,0,0));

            //  Triangle Indices
            chunk.addTriangle((short)(2 + chunk.offset), (short)(0 + chunk.offset), (short)(1 + chunk.offset));
            chunk.addTriangle((short)(1 + chunk.offset), (short)(3 + chunk.offset), (short)(2 + chunk.offset));

            //  Add the number of verts we added to offset
            chunk.updateIndex();

            //  Vertex Normals
            chunk.addNormal(0f, 0f, -1f);

            //  Texture Co-ordinates
            chunk.tCoords.add(new Vector2f(0,1));
            chunk.tCoords.add(new Vector2f(1,1));
            chunk.tCoords.add(new Vector2f(1,0));
            chunk.tCoords.add(new Vector2f(0,0));
            
            return chunk;
        };
        @Override
        public VoxMesh genRightFace(VoxMesh chunk, Vector3f blocPos){
            //  Vertex positions of the face
            chunk.addVertex(blocPos.add(0,1,1));
            chunk.addVertex(blocPos.add(0,1,0));
            chunk.addVertex(blocPos.add(0,0,1));
            chunk.addVertex(blocPos.add(0,0,0));

            //  Triangle Indices
            chunk.addTriangle((short)(2 + chunk.offset), (short)(0 + chunk.offset), (short)(1 + chunk.offset));
            chunk.addTriangle((short)(1 + chunk.offset), (short)(3 + chunk.offset), (short)(2 + chunk.offset));

            //  Add the number of verts we added to offset
            chunk.updateIndex();

            //  Vertex Normals
            chunk.addNormal(-1f, 0f, 0f);
            
            //  Texture Co-ordinates
            chunk.tCoords.add(new Vector2f(0,1));
            chunk.tCoords.add(new Vector2f(1,1));
            chunk.tCoords.add(new Vector2f(1,0));
            chunk.tCoords.add(new Vector2f(0,0));
            
            return chunk;
        };
        @Override
        public VoxMesh genLeftFace(VoxMesh chunk, Vector3f blocPos){
            //  Vertex positions of the face
            chunk.addVertex(blocPos.add(1,1,1));
            chunk.addVertex(blocPos.add(1,1,0));
            chunk.addVertex(blocPos.add(1,0,1));
            chunk.addVertex(blocPos.add(1,0,0));

            //  Triangle Indices
            chunk.addTriangle((short)(1 + chunk.offset), (short)(0 + chunk.offset), (short)(2 + chunk.offset));
            chunk.addTriangle((short)(2 + chunk.offset), (short)(3 + chunk.offset), (short)(1 + chunk.offset));

            //  Add the number of verts we added to offset
            chunk.updateIndex();

            //  Vertex Normals
            chunk.addNormal(1f, 0f, 0f);

            //  Texture Co-ordinates
            chunk.tCoords.add(new Vector2f(0,1));
            chunk.tCoords.add(new Vector2f(1,1));
            chunk.tCoords.add(new Vector2f(1,0));
            chunk.tCoords.add(new Vector2f(0,0));
            
            return chunk;
        };

        public boolean isSolid(Direction direction) {
            /*switch(direction){
                case Up:
                    return true;
                case Down:
                    return true;
                case Left:
                    return true;
                case Right:
                    return true;
                case Front:
                    return true;
                case Back:
                    return true;
            }*/
            return true;
        }
    }
    
    public class AirBlock implements AbstractBlock{

        public VoxMesh buildBlockFaces(Chunk chunk, Vector3f blockPos) {
            return chunk.chunkMesh;
        }
        public VoxMesh genTopFace(VoxMesh chunk, Vector3f blocPos) {
            return chunk;
        }
        public VoxMesh genBottomFace(VoxMesh chunk, Vector3f blocPos) {
            return chunk;
        }
        public VoxMesh genFrontFace(VoxMesh chunk, Vector3f blocPos) {
            return chunk;
        }
        public VoxMesh genBackFace(VoxMesh chunk, Vector3f blocPos) {
            return chunk;
        }
        public VoxMesh genRightFace(VoxMesh chunk, Vector3f blocPos) {
            return chunk;
        }
        public VoxMesh genLeftFace(VoxMesh chunk, Vector3f blocPos) {
            return chunk;
        }
        public boolean isSolid(Direction direction) {
            return false;
        }
        
    }
    
    public class Chunk implements Control{
        Vector3f chunkPos;
        AbstractBlock[][][] blocks;
        Geometry chunkGeo; //REPLACE NODE WITH GEOMETRY
        Node node;
        Mesh blockMesh;
        VoxMesh chunkMesh;
        
        ArrayList<Vector3f> positions;
        ArrayList<Vector2f> tCoords;
        ArrayList<Short> indexList;
        ArrayList<Float> normals;
        short offset;
        
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
        
        public void initChunk(){
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
            meshUpdate = true;
            chunkStatus = 0;
        }
        
        public AbstractBlock getBlock(Vector3f blocPos){
            if(inRange(blocPos.x ) && inRange(blocPos.y) && inRange(blocPos.z )){
	          return blocks[(int)blocPos.x][(int)blocPos.y][(int)blocPos.z];
            }
            return new AirBlock();
        }
        
        public boolean inRange(float blocPos){
            if((blocPos < chunkSize) && (blocPos > 0)){
                return true;
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
        
        public void addVertex(Vector3f pos){
            positions.add(pos);
        }
        public void addTriangle(short a, short b, short c){
            indexList.add((short)(a + offset));
            indexList.add((short)(b + offset));
            indexList.add((short)(c + offset));
            offset = (short) positions.size();
        }
        public void addNormal(float nx, float ny, float nz){
            normals.add(nx);
            normals.add(ny);
            normals.add(nz);
        }
        
        //REFACTOR buildBlockMesh()
        public Mesh buildChunkMesh() {
            Mesh mesh = new Mesh();

            Vector3f[] pVertices = new Vector3f[chunkMesh.positions.size()];
            Iterator<Vector3f> positionsIterator = chunkMesh.positions.iterator();
            for(int i=0;positionsIterator.hasNext();i++){
                pVertices[i] = positionsIterator.next();
            }

            short[] indices = new short[chunkMesh.indexList.size()];
            Iterator<Short> indicesIterator = chunkMesh.indexList.iterator();
            for(int i=0;indicesIterator.hasNext();i++){
                indices[i] = indicesIterator.next();
            }

            //float[] fNormals = new float[normals.size()]; 
            Vector3f[] pNormals = new Vector3f[chunkMesh.normals.size()/3];
            Iterator<Float> normalsIterator = chunkMesh.normals.iterator();
            for(int i=0;normalsIterator.hasNext();i++){
                pNormals[i] = new Vector3f(normalsIterator.next(),normalsIterator.next(),normalsIterator.next());
            }
            //Vector2f[] pTCoors = new Vector2f[tCoords.size()];

            //mesh.setDynamic();



            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pVertices));
            mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createShortBuffer(indices));
            mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(pNormals));
            //mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(pTCoors));
            mesh.updateBound();
            mesh.updateCounts();

            //System.out.println(mesh.getTriangleCount() + " Tris, " + mesh.getVertexCount() + " Verts");
            return mesh;
        }
    }
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    
    /*
    public Mesh buildBlockMesh() {
        Mesh mesh = new Mesh();
        
        Vector3f[] pVertices = new Vector3f[positions.size()];
        Iterator<Vector3f> positionsIterator = positions.iterator();
        for(int i=0;positionsIterator.hasNext();i++){
            pVertices[i] = positionsIterator.next();
        }
        
        short[] indices = new short[indexList.size()];
        Iterator<Short> indicesIterator = indexList.iterator();
        for(int i=0;indicesIterator.hasNext();i++){
            indices[i] = indicesIterator.next();
        }
        
        //float[] fNormals = new float[normals.size()]; 
        Vector3f[] pNormals = new Vector3f[normals.size()/3];
        Iterator<Float> normalsIterator = normals.iterator();
        for(int i=0;normalsIterator.hasNext();i++){
            pNormals[i] = new Vector3f(normalsIterator.next(),normalsIterator.next(),normalsIterator.next());
        }
        //Vector2f[] pTCoors = new Vector2f[tCoords.size()];
        
        //mesh.setDynamic();
        
        
        
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pVertices));
        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createShortBuffer(indices));
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(pNormals));
        //mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(pTCoors));
        mesh.updateBound();
        mesh.updateCounts();
        
        //System.out.println(mesh.getTriangleCount() + " Tris, " + mesh.getVertexCount() + " Verts");
        return mesh;
    } */

    @Override
    public void simpleInitApp() {
        //MOVED OLD CODE
        chunk = new Chunk();
        chunk.initChunk();
        rootNode.attachChild(chunk.node);
        flyCam.setMoveSpeed(20);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    /*
    public void oldInitApp(){
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        //geom.setMaterial(mat);

        //rootNode.attachChild(geom);
        
        blocPos = new Vector3f(0,-4,0);
        
        positions = new ArrayList();
        indexList = new ArrayList();
        normals = new ArrayList();
        tCoords = new ArrayList();
        offset = 0;
        
        genBlockFrontFace(blocPos);
        genBlockBackFace(blocPos);
        genBlockTopFace(blocPos);
        genBlockBottomFace(blocPos);
        genBlockRightFace(blocPos);
        genBlockLeftFace(blocPos);
              
        blockMesh = buildBlockMesh();
        geo = new Geometry( "Block",blockMesh);
        mat.getAdditionalRenderState().setWireframe(true);
        geo.setMaterial(mat);
        rootNode.attachChild(geo);
        flyCam.setMoveSpeed(20);
    }
    
    public void genBlockTopFace(Vector3f blockPos){
        //  Vertex positions of the face
        positions.add(blocPos.add(0,1,1));
        positions.add(blocPos.add(1,1,1));
        positions.add(blocPos.add(0,1,0));
        positions.add(blocPos.add(1,1,0));
        
        //  Triangle Indices
        indexList.add((short)(2 + offset));  //First Triangle Index
        indexList.add((short)(0 + offset));
        indexList.add((short)(1 + offset));
        
        indexList.add((short)(1 + offset));  //Second Triangle Index
        indexList.add((short)(3 + offset));
        indexList.add((short)(2 + offset));
        
        //  Add the number of verts we added to offset
        offset = (short) (positions.size());
        
        //  Vertex Normals
        for(int lNorm = 0; lNorm<4; lNorm++){
            normals.add(0f);
            normals.add(1f);
            normals.add(0f);
        }
        
        //  Texture Co-ordinates
        tCoords.add(new Vector2f(0,1));
        tCoords.add(new Vector2f(1,1));
        tCoords.add(new Vector2f(1,0));
        tCoords.add(new Vector2f(0,0));
        
        //System.out.println("Top Face: Index Count = " + offset);
    }
    
    public void genBlockBottomFace(Vector3f blockPos){
        //  Vertex positions of the face
        positions.add(blocPos.add(0,0,1));
        positions.add(blocPos.add(1,0,1));
        positions.add(blocPos.add(0,0,0));
        positions.add(blocPos.add(1,0,0));
        
        //  Triangle Indices
        indexList.add((short)(1 + offset));  //First Triangle Index
        indexList.add((short)(0 + offset));
        indexList.add((short)(2 + offset));
        
        indexList.add((short)(2 + offset));  //Second Triangle Index
        indexList.add((short)(3 + offset));
        indexList.add((short)(1 + offset));
        
        //  Add the number of verts we added to offset
        offset = (short) (positions.size());
        
        //  Vertex Normals
        for(int lNorm = 0; lNorm<4; lNorm++){
            normals.add(0f);
            normals.add(-1f);
            normals.add(0f);
        }
        
        //  Texture Co-ordinates
        tCoords.add(new Vector2f(0,1));
        tCoords.add(new Vector2f(1,1));
        tCoords.add(new Vector2f(1,0));
        tCoords.add(new Vector2f(0,0));
        
        //System.out.println("Bottom Face: Index Count = " + offset);
    }
    
    public void genBlockFrontFace(Vector3f blockPos){
        //  Vertex positions of the face
        positions.add(blocPos.add(0,1,1));
        positions.add(blocPos.add(1,1,1));
        positions.add(blocPos.add(0,0,1));
        positions.add(blocPos.add(1,0,1));
        
        //  Triangle Indices
        indexList.add((short)(1 + offset));  //First Triangle Index
        indexList.add((short)(0 + offset));
        indexList.add((short)(2 + offset));
        
        indexList.add((short)(2 + offset));  //Second Triangle Index
        indexList.add((short)(3 + offset));
        indexList.add((short)(1 + offset));
        
        //  Add the number of verts we added to offset
        offset = (short) (positions.size());
        
        //  Vertex Normals
        for(int lNorm = 0; lNorm<4; lNorm++){
            normals.add(0f);
            normals.add(0f);
            normals.add(1f);
        }
        
        //  Texture Co-ordinates
        tCoords.add(new Vector2f(0,1));
        tCoords.add(new Vector2f(1,1));
        tCoords.add(new Vector2f(1,0));
        tCoords.add(new Vector2f(0,0));
        
        //System.out.println("Front Face: Index Count = " + offset);
    }
    
    public void genBlockBackFace(Vector3f blockPos){
        //  Vertex positions of the face
        positions.add(blocPos.add(0,1,0));
        positions.add(blocPos.add(1,1,0));
        positions.add(blocPos.add(0,0,0));
        positions.add(blocPos.add(1,0,0));
        
        //  Triangle Indices
        indexList.add((short)(2 + offset));  //First Triangle Index
        indexList.add((short)(0 + offset));
        indexList.add((short)(1 + offset));
        
        indexList.add((short)(1 + offset));  //Second Triangle Index
        indexList.add((short)(3 + offset));
        indexList.add((short)(2 + offset));
        
        //  Add the number of verts we added to offset
        offset = (short) (positions.size());
        
        //  Vertex Normals
        for(int lNorm = 0; lNorm<4; lNorm++){
            normals.add(0f);
            normals.add(0f);
            normals.add(-1f);
        }
        
        //  Texture Co-ordinates
        tCoords.add(new Vector2f(0,1));
        tCoords.add(new Vector2f(1,1));
        tCoords.add(new Vector2f(1,0));
        tCoords.add(new Vector2f(0,0));
        
        //System.out.println("Back Face: Index Count = " + offset);
    }
    
    public void genBlockRightFace(Vector3f blockPos){
        //  Vertex positions of the face
        positions.add(blocPos.add(0,1,1));
        positions.add(blocPos.add(0,1,0));
        positions.add(blocPos.add(0,0,1));
        positions.add(blocPos.add(0,0,0));
        
        //  Triangle Indices
        indexList.add((short)(2 + offset));  //First Triangle Index
        indexList.add((short)(0 + offset));
        indexList.add((short)(1 + offset));
        
        indexList.add((short)(1 + offset));  //Second Triangle Index
        indexList.add((short)(3 + offset));
        indexList.add((short)(2 + offset));
        
        //  Add the number of verts we added to offset
        offset = (short) (positions.size());
        
        //  Vertex Normals
        for(int lNorm = 0; lNorm<4; lNorm++){
            normals.add(-1f);
            normals.add(0f);
            normals.add(0f);
        }
        
        //  Texture Co-ordinates
        tCoords.add(new Vector2f(0,1));
        tCoords.add(new Vector2f(1,1));
        tCoords.add(new Vector2f(1,0));
        tCoords.add(new Vector2f(0,0));
        
        //System.out.println("Right Face: Index Count = " + offset);
    }
    
    public void genBlockLeftFace(Vector3f blockPos){
        //  Vertex positions of the face
        positions.add(blocPos.add(1,1,1));
        positions.add(blocPos.add(1,1,0));
        positions.add(blocPos.add(1,0,1));
        positions.add(blocPos.add(1,0,0));
        
        //  Triangle Indices
        indexList.add((short)(1 + offset));  //First Triangle Index
        indexList.add((short)(0 + offset));
        indexList.add((short)(2 + offset));
        
        indexList.add((short)(2 + offset));  //Second Triangle Index
        indexList.add((short)(3 + offset));
        indexList.add((short)(1 + offset));
        
        //  Add the number of verts we added to offset
        offset = (short) (positions.size());
        
        //  Vertex Normals
        for(int lNorm = 0; lNorm<4; lNorm++){
            normals.add(1f);
            normals.add(0f);
            normals.add(0f);
        }
        
        //  Texture Co-ordinates
        tCoords.add(new Vector2f(0,1));
        tCoords.add(new Vector2f(1,1));
        tCoords.add(new Vector2f(1,0));
        tCoords.add(new Vector2f(0,0));
        
        //System.out.println("Left Face: Index Count = " + offset);
    } */
}

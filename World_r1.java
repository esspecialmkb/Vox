/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Michael B.
 */
public class ChunkTest extends SimpleApplication {
    Chunk chunk;
    World world;
    
    public class World extends AbstractAppState {
        public ConcurrentHashMap<Vector3Int,Chunk> chunks;
        public int chunkSize;
        public Node node;   // Use world node for raycasting
        
        // GetBlock
        public AbstractBlock getBlock(int x, int y, int z){
            Chunk c = getChunk(x,y,z);
            if(c != null){
                return c.getBlock( new Vector3f(x,y,z));
            }
            return new AirBlock();
        }
        
        // getChunk takes a world position, and returns a chunk, if it exists
        public Chunk getChunk(int x, int y, int z){
            int chunkX = (int) Math.floor(x / chunkSize);
            int chunkY = (int) Math.floor(y / chunkSize);
            int chunkZ = (int) Math.floor(z / chunkSize);
            
            Vector3Int v = new Vector3Int(chunkX,chunkY,chunkZ);
            
            if(chunks.containsKey(v)){
                return chunks.get(v);
            }
            
            return null;
        }
        
        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);
            
            node = new Node("Vox World");
            
            chunks = new ConcurrentHashMap<Vector3Int,Chunk>(16,0.75f,2);
            chunkSize = 4;
            
            for(int x = -2; x<3; x++){
                for(int z = -2; z<3; z++){
                    Chunk tChunk = new Chunk();
                    tChunk.initChunkPosition(x,0,z,app.getAssetManager());
                    node.attachChild(tChunk.node);
                    chunks.put(new Vector3Int(x,0,z), tChunk);
                }
            }
            
            rootNode.attachChild(node);
            
            //New code for input
            inputManager.addMapping("My Action",new KeyTrigger(KeyInput.KEY_SPACE));
            
            inputManager.addListener(actionListener, "My Action");
            inputManager.addListener(analogListener, "My Action");
            
            System.out.println("World initialize");
        }

        @Override
        public void stateAttached(AppStateManager stateManager) {
            System.out.println("World State Attached");
        }

        @Override
        public void stateDetached(AppStateManager stateManager) {
            System.out.println("World State Attached");
        }

        @Override
        public void update(float tpf) {
        }

        @Override
        public void render(RenderManager rm) {
        }

        @Override
        public void postRender(){
        }

        @Override
        public void cleanup() {
            System.out.println("World cleanup");
            inputManager.removeListener(actionListener);
            inputManager.removeListener(analogListener);
            chunks.clear();
            
            super.cleanup();
        }
        
        private ActionListener actionListener = new ActionListener(){
            public void onAction(String name, boolean pressed, float tpf){
                System.out.println(name + " = " + pressed);
                
            }
        };
        
        public AnalogListener analogListener = new AnalogListener() {
            public void onAnalog(String name, float value, float tpf) {
                System.out.println(name + " = " + value);
            }
        };
    }
    
    public static void main(String[] args) {
        ChunkTest app = new ChunkTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       /* Initialize the game scene here */
        /*
        chunk = new Chunk();
        chunk.initChunk(assetManager);
        rootNode.attachChild(chunk.node);
        */
        world = new World();
        stateManager.attach(world);
        flyCam.setMoveSpeed(20);
    }

    @Override
    public void simpleUpdate(float tpf) {
       /* Interact with game events in the main loop */
    }
    
    

}

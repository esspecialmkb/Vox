/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Michael B.
 */
public class ChunkTest extends SimpleApplication {
    Chunk chunk;
    World world;
    
    public class World extends AbstractAppState{
        public ConcurrentHashMap<Vector3Int,Chunk> chunks;
        public int chunkSize;
        
        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);
            
            chunks = new ConcurrentHashMap<Vector3Int,Chunk>();
            chunkSize = 4;
            
            for(int x = -2; x<3; x++){
                for(int z = -2; z<3; z++){
                    Chunk tChunk = new Chunk();
                    tChunk.initChunkPosition(x,0,z,app.getAssetManager());
                    rootNode.attachChild(tChunk.node);
                    chunks.put(new Vector3Int(x,0,z), tChunk);
                }
            }
            
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
            chunks.clear();
            
            super.cleanup();
        }
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

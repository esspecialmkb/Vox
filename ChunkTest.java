/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mygame;

import com.jme3.app.SimpleApplication;

/**
 *
 * @author Michael B.
 */
public class ChunkTest extends SimpleApplication {
    Chunk chunk;
    public static void main(String[] args) {
        ChunkTest app = new ChunkTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       /* Initialize the game scene here */
        chunk = new Chunk();
        chunk.initChunk(assetManager);
        rootNode.attachChild(chunk.node);
        flyCam.setMoveSpeed(20);
    }

    @Override
    public void simpleUpdate(float tpf) {
       /* Interact with game events in the main loop */
    }
    
    

}

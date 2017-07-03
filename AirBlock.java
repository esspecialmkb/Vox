/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author Michael B.
 */
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
        public boolean isSolid(AbstractBlock.Direction direction) {
            return false;
        }
        
    }

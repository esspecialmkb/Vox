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
    public boolean isSolid(AbstractBlock.Direction direction);
}

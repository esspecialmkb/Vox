/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author Michael B.
 */
public class BasicBlock implements AbstractBlock{
    
    @Override
    public VoxMesh buildBlockFaces(Chunk chunk, Vector3f blockPos){
        //NEW LINES
        System.out.println("BasicBlock Faces " + blockPos);
        String debugOutput = "";
        if( (chunk.getBlock(blockPos.add(0,1,0)).isSolid(Direction.Down)) == false ){ //-> TOP
            debugOutput += "Top ";
            genTopFace(chunk.chunkMesh, blockPos);
        }

        if( (chunk.getBlock(blockPos.add(0,-1,0)).isSolid(Direction.Up)) == false ){ //-> BOTTOM
            debugOutput += "Bottom ";
            genBottomFace(chunk.chunkMesh, blockPos);
        }

        if( (chunk.getBlock(blockPos.add(1,0,0)).isSolid(Direction.Right)) == false ){ //-> LEFT
            debugOutput += "Left ";
            genLeftFace(chunk.chunkMesh, blockPos);
        }

        if( (chunk.getBlock(blockPos.add(-1,0,0)).isSolid(Direction.Left)) == false ){ //-> RIGHT
            debugOutput += "Right ";
            genRightFace(chunk.chunkMesh, blockPos);
        }

        if( (chunk.getBlock(blockPos.add(0,0,1)).isSolid(Direction.Back)) != true ){ // -> FRONT
            debugOutput += "Front ";
            genFrontFace(chunk.chunkMesh, blockPos);
        }

        if( (chunk.getBlock(blockPos.add(0,0,-1)).isSolid(Direction.Front)) == false ){ //-> BACK
            debugOutput += "Back ";
            genBackFace(chunk.chunkMesh, blockPos); 
        }

        System.out.println(debugOutput);
        
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

    public boolean isSolid(AbstractBlock.Direction direction) {
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

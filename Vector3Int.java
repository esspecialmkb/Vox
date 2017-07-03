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

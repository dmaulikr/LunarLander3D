package net.erikhk.lunarlander3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by erikhk on 28/1/2016.
 */
public class Spaceship {

    Model m,fire;
    Mat4 T;
    Mat4 Ro;
    Bitmap bm;

    public static final float gravity = -0.0006f;
    public boolean haslanded = false;
    public boolean hascrashed = false;
    public boolean isthrusting = false;
    public float fuel = 100.0f;
    public float thrust = .0007f*2f;
    public Vec3 speed = new Vec3();
    public Vec3 pos = new Vec3();
    public Vec3 acc = new Vec3();

    public Vec3 phone_n = new Vec3();
    public Vec3 init_phone_n = new Vec3();

    public Spaceship(Context c)
    {
        m = new Model(c, R.raw.spaceship_verts, R.raw.spaceship_normals, R.raw.spaceship_texture,
                R.drawable.texture, GLES20.GL_TEXTURE0);

        fire = new Model(c, R.raw.plane_verts, R.raw.plane_normals);
        //T = VecMath.T(0,25f,0);
        T = VecMath.IdentityMatrix();
        Ro = VecMath.IdentityMatrix();
        pos = new Vec3(8f, 10f, 8f);
    }

    public void DrawModel()
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m.textureHandle[0]);
        Mat4 rot;
        if(!haslanded) {
            Vec3 n = new Vec3(0, -1, 0);
            Vec3 cross = VecMath.CrossProduct(MainActivity.phone_n, MainActivity.init_phone_n);
            phone_n = MainActivity.phone_n;
            init_phone_n = MainActivity.init_phone_n;
            rot = VecMath.Mult(T, VecMath.ArbRotate(VecMath.Normalize(cross), MainActivity.phone_ang));
            GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(rot.m));
            Ro = rot;
        }
        else
        {
            rot = T;
            GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(rot.m));
            //Ro = rot;
        }


        //GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(Ro.m));
        m.DrawModel();

        if(isthrusting) {
            GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_exhaustf"), 1);
            Mat4 Tf = VecMath.Mult(rot, VecMath.T(0, -.8f, 0));
            GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(Tf.m));
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glDepthMask(false);
            fire.DrawModel();
            GLES20.glDepthMask(true);
            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_exhaustf"), 0);
        }
    }

    public void move()
    {
/*
        Vec3 n = new Vec3(0,-1,0);
        Vec3 cross = VecMath.CrossProduct(MainActivity.phone_n, MainActivity.init_phone_n);

        Ro = VecMath.ArbRotate(VecMath.Normalize(cross), MainActivity.phone_ang);

        phone_n = MainActivity.phone_n;
        init_phone_n = MainActivity.init_phone_n;
        Mat4 rot = VecMath.Mult(T, Ro);
        GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(rot.m));
*/

        Vec3 world_n = new Vec3(init_phone_n.x - phone_n.x, init_phone_n.y-phone_n.y, init_phone_n.z - phone_n.z);
        Vec3 xy = new Vec3(world_n.x, world_n.y, 0);
        Vec3 xz = new Vec3((phone_n.x-init_phone_n.x), 0, (phone_n.z-init_phone_n.z));
        Vec3 y = new Vec3(0,init_phone_n.y,0);
        Vec3 x = new Vec3(init_phone_n.x,0,0);


        GLES20.glUniform3f(GLES20.glGetUniformLocation(Shader.program, "spaceship_pos"), pos.x, pos.y, pos.z);

        float ang = MainActivity.phone_ang;

        if(isthrusting) {
            /*
            acc.x = -thrust * (float)Math.sin(MainActivity.phone_ang);
            acc.y = thrust * (float)Math.cos(MainActivity.phone_ang);
            acc.z = -thrust * (float)Math.sin(phi);
            */
            if(fuel > 0.0f)
            fuel -= .3;
            GLES20.glUniform1f(GLES20.glGetUniformLocation(Shader.program, "fuel"), fuel);
            acc.x = thrust * xz.x/5.0f;
            //acc.y = thrust*(1 - (float)Math.sqrt( (xz.x)*(xz.x) + (xz.z)*(xz.z)  )/10.0f);
            acc.y = thrust * (float)Math.cos(ang);
            acc.z = thrust * xz.z/5.0f;

        }

        else
            acc.y = gravity;

        speed.x += acc.x;
        speed.y += acc.y;
        speed.z += acc.z;

        pos.x += speed.x;
        pos.y += speed.y;
        pos.z += speed.z;

        T = VecMath.T(pos);

        speed.x *= 0.97f;
        speed.z *= 0.97f;

    }


    public FloatBuffer makefloatbuffer(float[] array)
    {
        FloatBuffer floatbuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatbuff.put(array).position(0);

        return floatbuff;
    }

}

package net.erikhk.lunarlander3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by erikhk on 27/1/2016.
 */
public class VecMath {

    public static Mat4 ArbRotate(Vec3 axis, float fi)
    {
        Vec3 x, y, z;
        Mat4 m = new Mat4();
        Mat4 R = new Mat4();
        Mat4 Rt, Raxel;

        if (axis.x < 0.1) // Below some small value
            if (axis.x > -0.1)
                if (axis.y < 0.1)
                    if (axis.y > -0.1)
                    {
                        if (axis.z > 0)
                        {
                            m = Rz(fi);
                            return m;
                        }
                        else
                        {
                            m = Rz(-fi);
                            return m;
                        }
                    }

        x = Normalize(axis);
        z = SetVector(0, 0, 1); // Temp z
        y = Normalize(CrossProduct(z, x)); // y' = z^ x x'
        z = CrossProduct(x, y); // z' = x x y

        R.m[0] = x.x; R.m[1] = x.y; R.m[2] = x.z;  R.m[3] = 0.0f;
        R.m[4] = y.x; R.m[5] = y.y; R.m[6] = y.z;  R.m[7] = 0.0f;
        R.m[8] = z.x; R.m[9] = z.y; R.m[10] = z.z;  R.m[11] = 0.0f;

        R.m[12] = 0.0f; R.m[13] = 0.0f; R.m[14] = 0.0f;  R.m[15] = 1.0f;


    Rt = Transpose(R); // Transpose = Invert -> felet ej i Transpose, och det Šr en ortonormal matris

    Raxel = Rx(fi); // Rotate around x axis

    // m := Rt * Rx * R
    m = Mult(Mult(Rt, Raxel), R);

        return m;
    }


    public static Vec3 Normalize(Vec3 a)
    {
        float norm;
        Vec3 result = new Vec3();

        norm = (float)Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
        result.x = a.x / (norm+.01f);
        result.y = a.y / (norm+.01f);
        result.z = a.z / (norm+.01f);
        return result;

    }

    /*
    public static Vec3 Vec3(Mat4 a)
    {
        return new Vec3(a.m[3], a.m[7], a.m[11]);
    }
    */

    public static Vec3 VectorSub(Vec3 a, Vec3 b)
    {
        Vec3 result = new Vec3();

        result.x = a.x - b.x;
        result.y = a.y - b.y;
        result.z = a.z - b.z;
        return result;
    }

    public static Vec3 VectorAdd(Vec3 a, Vec3 b)
    {
        Vec3 result = new Vec3();

        result.x = a.x + b.x;
        result.y = a.y + b.y;
        result.z = a.z + b.z;
        return result;
    }


    public static Vec3 CrossProduct(Vec3 a, Vec3 b)
    {
        Vec3 result = new Vec3();

        result.x = a.y*b.z - a.z*b.y;
        result.y = a.z*b.x - a.x*b.z;
        result.z = a.x*b.y - a.y*b.x;

        return result;
    }

    public static Vec3 SetVector(float x, float y, float z)
    {
        return new Vec3(x,y,z);

    }

    public static Mat4 Rx(float a)
    {
        Mat4 m = new Mat4();
        m = IdentityMatrix();
        m.m[5] = (float)Math.cos(a);
        m.m[9] = (float)Math.sin(a);
        m.m[6] = -m.m[9]; //sin(a);
        m.m[10] = m.m[5]; //cos(a);
        return m;
    }

    public static Mat4 Ry(float a)
    {
        Mat4 m = new Mat4();
        m = IdentityMatrix();
        m.m[0] = (float)Math.cos(a);
            m.m[8] = (float)Math.sin(a);
        m.m[2] = -m.m[8]; //sin(a);
        m.m[10] = m.m[0]; //cos(a);
        return m;
    }

    public static Mat4 Rz(float a)
    {
        Mat4 m;
        m = IdentityMatrix();
        m.m[0] = (float)Math.cos(a);

            m.m[4] = (float)Math.sin(a);
        m.m[1] = -m.m[4]; //sin(a);
        m.m[5] = m.m[0]; //cos(a);
        return m;
    }


    public static Mat4 IdentityMatrix()
    {
        Mat4 m = new Mat4();
        int i;

        for (i = 0; i <= 15; i++)
            m.m[i] = 0;
        for (i = 0; i <= 3; i++)
            m.m[i * 5] = 1; // 0,5,10,15
        return m;
    }

    public static Mat4 InvertMat4(Mat4 a) {
        Mat4 b = new Mat4();

        float c = a.m[0], d = a.m[1], e = a.m[2], g = a.m[3],
                f = a.m[4], h = a.m[5], i = a.m[6], j = a.m[7],
                k = a.m[8], l = a.m[9], o = a.m[10], m = a.m[11],
                n = a.m[12], p = a.m[13], r = a.m[14], s = a.m[15],
                A = c * h - d * f,
                B = c * i - e * f,
                t = c * j - g * f,
                u = d * i - e * h,
                v = d * j - g * h,
                w = e * j - g * i,
                x = k * p - l * n,
                y = k * r - o * n,
                z = k * s - m * n,
                C = l * r - o * p,
                D = l * s - m * p,
                E = o * s - m * r,
                q = 1 / (A * E - B * D + t * C + u * z - v * y + w * x);
        b.m[0] = (h * E - i * D + j * C) * q;
        b.m[1] = (-d * E + e * D - g * C) * q;
        b.m[2] = (p * w - r * v + s * u) * q;
        b.m[3] = (-l * w + o * v - m * u) * q;
        b.m[4] = (-f * E + i * z - j * y) * q;
        b.m[5] = (c * E - e * z + g * y) * q;
        b.m[6] = (-n * w + r * t - s * B) * q;
        b.m[7] = (k * w - o * t + m * B) * q;
        b.m[8] = (f * D - h * z + j * x) * q;
        b.m[9] = (-c * D + d * z - g * x) * q;
        b.m[10] = (n * v - p * t + s * A) * q;
        b.m[11] = (-k * v + l * t - m * A) * q;
        b.m[12] = (-f * C + h * y - i * x) * q;
        b.m[13] = (c * C - d * y + e * x) * q;
        b.m[14] = (-n * u + p * B - r * A) * q;
        b.m[15] = (k * u - l * B + o * A) * q;
        return b;
    }


    public static Mat4 Mult(Mat4 a, Mat4 b) // m = a * b
    {
        Mat4 m = new Mat4();

        int x, y;
        for (x = 0; x <= 3; x++)
            for (y = 0; y <= 3; y++)

                    m.m[y*4 + x] =	a.m[y*4+0] * b.m[0*4+x] +
                            a.m[y*4+1] * b.m[1*4+x] +
                            a.m[y*4+2] * b.m[2*4+x] +
                            a.m[y*4+3] * b.m[3*4+x];

        return m;
    }


    public static Vec3 MultVec3(Mat4 a, Vec3 b) // result = a * b
    {
        Vec3 r = new Vec3();

            r.x = a.m[0]*b.x + a.m[1]*b.y + a.m[2]*b.z + a.m[3];
            r.y = a.m[4]*b.x + a.m[5]*b.y + a.m[6]*b.z + a.m[7];
            r.z = a.m[8]*b.x + a.m[9]*b.y + a.m[10]*b.z + a.m[11];

        return r;
    }

    public static float Norm(Vec3 a)
    {
        float result;

        result = (float)Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
        return result;
    }


    public static Mat4 Transpose(Mat4 m)
    {
        Mat4 a = new Mat4();

        a.m[0] = m.m[0]; a.m[4] = m.m[1]; a.m[8] = m.m[2];      a.m[12] = m.m[3];
        a.m[1] = m.m[4]; a.m[5] = m.m[5]; a.m[9] = m.m[6];      a.m[13] = m.m[7];
        a.m[2] = m.m[8]; a.m[6] = m.m[9]; a.m[10] = m.m[10];    a.m[14] = m.m[11];
        a.m[3] = m.m[12]; a.m[7] = m.m[13]; a.m[11] = m.m[14];    a.m[15] = m.m[15];

        return a;
    }


    public static Mat4 frustum(float left, float right, float bottom, float top,
                 float znear, float zfar)
    {
        float temp, temp2, temp3, temp4;
        Mat4 matrix = new Mat4();

        temp = 2.0f * znear;
        temp2 = right - left;
        temp3 = top - bottom;
        temp4 = zfar - znear;
        matrix.m[0] = temp / temp2; // 2*near/(right-left)
        matrix.m[1] = 0.0f;
        matrix.m[2] = 0.0f;
        matrix.m[3] = 0.0f;
        matrix.m[4] = 0.0f;
        matrix.m[5] = temp / temp3; // 2*near/(top - bottom)
        matrix.m[6] = 0.0f;
        matrix.m[7] = 0.0f;
        matrix.m[8] = (right + left) / temp2; // A = r+l / r-l
        matrix.m[9] = (top + bottom) / temp3; // B = t+b / t-b
        matrix.m[10] = (-zfar - znear) / temp4; // C = -(f+n) / f-n
        matrix.m[11] = -1.0f;
        matrix.m[12] = 0.0f;
        matrix.m[13] = 0.0f;
        matrix.m[14] = (-temp * zfar) / temp4; // D = -2fn / f-n
        matrix.m[15] = 0.0f;

        matrix = Transpose(matrix);

        return matrix;
    }


    public static Mat4 T(float tx, float ty, float tz)
    {
        Mat4 m;
        m = IdentityMatrix();
        m.m[3] = tx;
        m.m[7] = ty;
        m.m[11] = tz;
        return m;
    }

    public static Mat4 T(Vec3 v)
    {
        return T(v.x, v.y, v.z);
    }

    public static Mat4 S(float sx, float sy, float sz)
    {
        Mat4 m;
        m = IdentityMatrix();
        m.m[0] = sx;
        m.m[5] = sy;
        m.m[10] = sz;
        return m;
    }


    public static Mat4 lookAtv(Vec3 p, Vec3 l, Vec3 v)
    {
        Vec3 n = new Vec3();
        Vec3 u = new Vec3();

        n = Normalize(VectorSub(p, l));
        u = Normalize(CrossProduct(v, n));
        v = CrossProduct(n, u);

        Mat4 rot = new Mat4(u.x, u.y, u.z, 0,
                v.x, v.y, v.z, 0,
                n.x, n.y, n.z, 0,
                0,   0,   0,   1);
        Mat4 trans = new Mat4();
        trans = T(-p.x, -p.y, -p.z);
        return Mult(rot, trans);
    }


    public static Vec3 ScalarMult(Vec3 a, float s)
    {
        Vec3 result = new Vec3();

        result.x = a.x * s;
        result.y = a.y * s;
        result.z = a.z * s;

        return result;
    }

    public static Mat4 lookAt(float px, float py, float pz,
                float lx, float ly, float lz,
                float vx, float vy, float vz)
    {
        Vec3 p = new Vec3();
        Vec3 l = new Vec3();
        Vec3 v = new Vec3();

        p = SetVector(px, py, pz);
        l = SetVector(lx, ly, lz);
        v = SetVector(vx, vy, vz);

        return lookAtv(p, l, v);
    }

    public static float DistanceXZ(Vec3 a, Vec3 b)
    {
        return (float)Math.sqrt( (a.x-b.x)*(a.x-b.x) + (a.z-b.z)*(a.z-b.z) );
    }


    public static float DotProduct(Vec3 a, Vec3 b)
    {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static FloatBuffer makefloatbuffer(float[] array)
    {
        FloatBuffer floatbuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatbuff.put(array).position(0);

        return floatbuff;
    }

}

package net.letscode.worldbridge.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class AdvancedNbtCompound extends NbtCompound {
    public Vec3d getVec3d(String key) {
        NbtCompound element = this.getCompound(key);
        try{
            if(element.getString("type").equals("vec3d")) {
                return new Vec3d(element.getDouble("x"), element.getDouble("y"), element.getDouble("z"));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return Vec3d.ZERO;
    }

    public Vec3i getVec3i(String key) {
        NbtCompound element = this.getCompound(key);

        try{
            if(element.getString("type").equals("vec3i")) {
                return new Vec3i(element.getInt("x"), element.getInt("y"), element.getInt("z"));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return Vec3i.ZERO;
    }

    public float[] getFloatArray(String key) {
        NbtCompound element = this.getCompound(key);

        try{
            if(element.getString("type").equals("floatArray")) {
                float[] array = new float[element.getInt("length")];
                for(int i = 0;i<array.length;i++) {
                    array[i] = element.getFloat(String.valueOf(i));
                }
                return array;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new float[]{};
    }
}

package org.tud.kuka.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtil {

	public static byte [] long2ByteArray (long value)
	{
	    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
	}

	public static byte [] float2ByteArray (float value)
	{  
	     return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array();
	}
	
	public static int [] long2IntArray (long value)
	{
	    return byteArray2IntArray(long2ByteArray(value));
	}

	public static int [] float2IntArray (float value)
	{  
		return byteArray2IntArray(float2ByteArray(value));
	}
	public static int[] byteArray2IntArray(byte[] array) {
		int[] ret = new int[array.length];
	    for(int i = 0; i< ret.length;i++) {
	    	if(array[i] < 0) ret[i] = array[i]+256;
	    	else ret[i] = array[i];
	    }
	    return ret;
	}
	public static int array2Int(int[] array) {
		if(array.length != 4) return 0;
	    return   array[3] & 0xFF |
	            (array[2] & 0xFF) << 8 |
	            (array[1] & 0xFF) << 16 |
	            (array[0] & 0xFF) << 24;
	}
	public static float array2Float(int[] array) {
		if(array.length != 4) return 0;
		return Float.intBitsToFloat( array[0] ^ array[1]<<8 ^ array[2]<<16 ^ array[3]<<24 );
	}
}

package org.tud.kuka.util;

import java.util.LinkedList;
import java.util.List;

import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.SceneGraphObject;
import com.kuka.roboticsAPI.geometricModel.World;

public class FrameUtil {

	public static List<ObjectFrame> getAllFrames(boolean recursive) {
		return getAllFrames(World.Current, recursive);
	}
	public static List<ObjectFrame> getAllFrames(SceneGraphObject baseObject, boolean recursive) {
		List<ObjectFrame> frames = new LinkedList<ObjectFrame>();
		for(ObjectFrame f:baseObject.getAllFrames()) {
			if(!f.getName().isEmpty() ) {
				int count = f.getPath().length() - f.getPath().replace("/", "").length();
				if(count == 1) frames.add(f);
				if(count > 1 && recursive) frames.add(f);
				
			}
		}
		return frames;
	}
}

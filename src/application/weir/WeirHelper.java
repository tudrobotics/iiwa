package application.weir;

import java.util.Arrays;

import org.tud.kuka.socket.RobotCommandCartesianMessage;
import org.tud.kuka.socket.RobotCommandJointMessage;
import org.tud.kuka.socket.RobotCommandRotationMessage;

import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.World;
import com.kuka.roboticsAPI.geometricModel.math.Matrix;
import com.kuka.roboticsAPI.geometricModel.math.MatrixTransformation;
import com.kuka.roboticsAPI.geometricModel.math.Vector;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;

public class WeirHelper {

	private static final Vector transformationVector = Vector.of(0.0, 0.0, 0.0);
	
	public static final CartesianImpedanceControlMode createCartesianImpedanceControlMode() {
		CartesianImpedanceControlMode mode = new CartesianImpedanceControlMode();
		mode.parametrize(CartDOF.X).setStiffness(100);
		mode.parametrize(CartDOF.Y).setStiffness(100);
		mode.parametrize(CartDOF.Z).setStiffness(100);
		mode.parametrize(CartDOF.A).setStiffness(150);
		mode.parametrize(CartDOF.B).setStiffness(150);
		mode.parametrize(CartDOF.C).setStiffness(150);
		mode.parametrize(CartDOF.X).setDamping(0.8);
		mode.parametrize(CartDOF.Y).setDamping(0.8);
		mode.parametrize(CartDOF.Z).setDamping(0.8);
		return mode;
	}
	
	public static final Frame createNewTransformationFrame() {
		Frame parent = new Frame();
		parent.setParent(World.Current.getRootFrame());
		parent.setX(0.0);
		parent.setY(0.0);
		parent.setZ(0.0);
		parent.setAlphaRad(0.0);
		parent.setBetaRad(0.0);
		parent.setGammaRad(0.0);
		return parent;
	}
	
	public static final JointPosition createJointPosition(RobotCommandJointMessage message) {
		if(Math.abs(message.getA0()) > axisRadRange[0]) message.setA0(Math.rint(message.getA0()/message.getA0())* axisRadRange[0]);
		if(Math.abs(message.getA0()) > axisRadRange[1]) message.setA0(Math.rint(message.getA1()/message.getA1())* axisRadRange[1]);
		if(Math.abs(message.getA0()) > axisRadRange[2]) message.setA0(Math.rint(message.getA2()/message.getA2())* axisRadRange[2]);
		if(Math.abs(message.getA0()) > axisRadRange[3]) message.setA0(Math.rint(message.getA3()/message.getA3())* axisRadRange[3]);
		if(Math.abs(message.getA0()) > axisRadRange[4]) message.setA0(Math.rint(message.getA4()/message.getA4())* axisRadRange[4]);
		if(Math.abs(message.getA0()) > axisRadRange[5]) message.setA0(Math.rint(message.getA5()/message.getA5())* axisRadRange[5]);
		if(Math.abs(message.getA0()) > axisRadRange[6]) message.setA0(Math.rint(message.getA6()/message.getA6())* axisRadRange[6]);
		return new JointPosition(message.getA0(),
					message.getA1(),
					message.getA2(), 
					message.getA3(), 
					message.getA4(),
					message.getA5(),
					message.getA6());
	}

	public static Frame createFrame(Frame frame, RobotCommandCartesianMessage message) {
		frame.setX(message.getX());
		frame.setY(message.getY());
		frame.setZ(message.getZ());
		frame.setAlphaRad(message.getAlphaRad());
		frame.setBetaRad(message.getBetaRad());
		frame.setGammaRad(message.getGammaRad());
		return frame;
	}
	
	public static final Frame createRotationFrame(RobotCommandRotationMessage message) {
		Frame frame = createNewTransformationFrame();
		Matrix transformationMatrix = Matrix.ofRowFirst(message.getRot00(), message.getRot01(), message.getRot02(),
				message.getRot10(),message.getRot11(),message.getRot12(), 
				message.getRot20(),message.getRot21(),message.getRot22());
		MatrixTransformation transformation = MatrixTransformation.of(transformationVector, transformationMatrix);
		frame.transform(transformation);
		frame.setX(message.getX());
		frame.setY(message.getY());
		frame.setZ(message.getZ());
		return frame;
	}
	
	public static final int[] axisDegPerSec = new int[] {75, 75, 90, 90, 144, 135, 135};
	public static final double[] axisRadPerSec = new double[] {Math.toRadians(75), Math.toRadians(75), Math.toRadians(90), 
		Math.toRadians(90), Math.toRadians(144), Math.toRadians(135), Math.toRadians(135)};
	public static final int[] axisGradRange = new int[] {170, 120, 170, 120, 170, 120, 175};
	public static final double[] axisRadRange = new double[] {Math.toRadians(170), Math.toRadians(120), Math.toRadians(170), 
		Math.toRadians(120), Math.toRadians(170), Math.toRadians(120), Math.toRadians(175)};
	
	public static double[] calculateAxisRadPerSecWithVelocity(double maxVelocityRel) {
		double[] d = Arrays.copyOf(axisRadPerSec,axisRadPerSec.length);
		for(int i = 0; i < d.length;i++) {
			d[i]*= maxVelocityRel;
		}
		return d;
	}
	
	public static double[] calculateAxisRadPerMilliSecWithVelocity(double maxVelocityRel, int milliseconds) {
		double[] d = Arrays.copyOf(axisRadPerSec,axisRadPerSec.length);
		for(int i = 0; i < d.length;i++) {
			d[i]*= maxVelocityRel *((double)milliseconds/1000.0d);
		}
		return d;
	}
	
	public static double[] radToGradArray(double[] radArray) {
		double[] r = Arrays.copyOf(radArray, radArray.length);
		for(int i = 0; i < r.length;i++) {
			r[i] = Math.toDegrees(radArray[i]);
		}
		return r;
	}
}

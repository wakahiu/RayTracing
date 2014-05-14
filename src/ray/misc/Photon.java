package ray.misc;

import ray.math.Point3;
import ray.misc.Color;
import ray.math.Vector3;


public class Photon{

	public final Point3	position = new Point3();
	public final Color	power = new Color();
	public final Color refIdx = new Color(1.0,1.0,1.0);
	public final Vector3 direction = new Vector3();

	//No need for KDTree flag
	//Default constructor 
	public Photon(){}

	public Photon(Color pow){
		this.power.set( pow );
	}
	//public final Point3 location = new 
	public  void setRefIdx(Color idxNew){
		this.refIdx.set(idxNew);
	}

	//public final Point3 location = new 
	public  void setPower(Color idxPow){
		this.power.set(idxPow);
	}

	public  void resetRefIdx( ){
		this.refIdx.set( new Color(1.0,1.0,1.0) );
	}

	public  Color getRefIdx(){
		return this.refIdx;
	}

	//public final Point3 location = new 
	public  void setPosition(Point3 posNew){
		this.position.set(posNew);
	}

	//public final Point3 location = new 
	public  void setDirection(Vector3 dirNew){
		this.direction.set(dirNew);
	}
}
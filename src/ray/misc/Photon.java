package ray.misc;

import ray.math.Point3;
import ray.misc.Color;
import ray.math.Vector3;


public class Photon{

	public final Point3	position = new Point3();
	public final Color	power = new Color();
	public final Vector3 direction = new Vector3();

	//No need for KDTree flag
	//Default constructor 
	public Photon(){}

	public Photon(Color pow){
		this.power.set( pow );
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
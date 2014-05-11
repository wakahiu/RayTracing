package ray.misc;

import ray.math.Point3;

public class Photon{

	public Point3 position = new Point3();

	//Default constructor 
	public Photon(){}

	//public final Point3 location = new 
	public  void setPosition(Point3 posNew){
		this.position.set(posNew);
	}
}
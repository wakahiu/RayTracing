package ray.light;

import java.util.Random;

import ray.math.Vector3;
import ray.math.Point3;
import ray.misc.Color;

public class PointLight {

	private final Random rndGen = new Random();

	public final Point3 location = new Point3();
	
	public final Color power = new Color(1.,1.,1.);	// diffuse component of the point light source
	
	public PointLight() {

	}
	
	/**
	 * Used internally by the parser to set the position of the point light source. 
	 *
	 * @deprecated
	 */
	public void setLocation(Point3 l) {
		location.set(l);
	}
	

	/**
	 * Used internally by the parser to set the diffuse component of the point light source. 
	 *
	 * @deprecated
	 */
	public void setPower(Color s) {
		power.set(s);
	}
	
	/**
     * The pdf corresponding to chooseSamplePoint.  
     * LRec must describe a visible point on a luminaire.    
     */
    public Vector3 sampleDirection( )
    {
    	double x = rndGen.nextDouble() * 2 - 1.0;
    	double y = rndGen.nextDouble() * 2 - 1.0;
    	double z = rndGen.nextDouble() * 2 - 1.0;
        return new Vector3( x, y, z);
    }
}

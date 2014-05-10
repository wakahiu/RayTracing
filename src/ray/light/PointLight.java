package ray.light;

import java.util.Random;

import ray.math.Vector3;
import ray.math.Point3;
import ray.misc.Color;

public class PointLight {
	
	public double power = 1.0;

	private final Random rndGen = new Random();

	public final Point3 location = new Point3();
	
	public final Color diffuse = new Color(1.,1.,1.);	// diffuse component of the point light source
	
	public final Color specular = new Color(1.,1.,1.);	// specular component of the point light source
	
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
	 * Used internally by the parser to set the power of the point light source. 
	 *
	 * @deprecated
	 */
	public void setPower(double p){
		power = p;
	}

	/**
	 * Used internally by the parser to set the diffuse component of the point light source. 
	 *
	 * @deprecated
	 */
	public void setDiffuse(Color s) {
		diffuse.set(s);
	}
	
	/**
	 * Used internally by the parser to set the specular component of the point light source. 
	 *
	 * @deprecated
	 */
	public void setSpecular(Color s) {
		specular.set(s);
	}

	/**
     * The pdf corresponding to chooseSamplePoint.  
     * LRec must describe a visible point on a luminaire.    
     */
    public Vector3 sampleDirection( )
    {
        return new Vector3(rndGen.nextDouble(), rndGen.nextDouble(),rndGen.nextDouble());
    }
}

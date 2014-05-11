package ray.material;

import ray.brdf.BRDF;
import ray.btdf.BTDF;
import ray.bsdf.BSDF;
import ray.brdf.Lambertian;
import ray.btdf.Transparent;
import ray.bsdf.Transluscent;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

/**
 * A homogeneous reflecting material, which has the same BRDF at all locations.
 * 
 * @author srm
 */
public class Homogeneous implements Material {
	
	BRDF brdf = new Lambertian();
	BTDF btdf = new Transparent();
	BSDF bsdf = new Transluscent();
	
	public Homogeneous() { }

	public void setBRDF(BRDF brdf) { this.brdf = brdf; }
	public void setBTDF(BTDF btdf) { this.btdf = btdf; }

	public BRDF getBRDF(IntersectionRecord iRec) {
		return brdf;
	}

	public BSDF getBSDF(IntersectionRecord iRec) {
		return bsdf;
	}

	public BTDF getBTDF(IntersectionRecord iRec) {
		return btdf;
	}

	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
		outRadiance.set(0, 0, 0);
	}

	public boolean isEmitter() {
		return false;
	}

}

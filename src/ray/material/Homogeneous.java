package ray.material;

import ray.bsdf.BSDF;
import ray.btdf.BTDF;
import ray.brdf.BRDF;
import ray.bsdf.Transluscent;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

/**
 * A homogeneous reflecting and transmitting material, which has the same BRDF/BTDF at all locations.
 * 
 * @author srm
 */
public class Homogeneous implements Material {
	BSDF bsdf = new Transluscent();
		
	public Homogeneous() { }

	public void setBSDF(BSDF bsdf) { 
		this.bsdf = bsdf; 
	}

	public BSDF getBSDF(IntersectionRecord iRec) {
		return bsdf;
	}

	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
		outRadiance.set(0, 0, 0);
	}

	public boolean isEmitter() {
		return false;
	}

}

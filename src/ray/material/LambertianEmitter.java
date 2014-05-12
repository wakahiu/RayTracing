package ray.material;

import ray.brdf.BRDF;
import ray.bsdf.BSDF;
import ray.brdf.Lambertian;
import ray.bsdf.Transluscent;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

/**
 * A Lambertian emitter, which emits the same radiance at all points and in all directions.
 * 
 * @author srm
 */
public class LambertianEmitter implements Material {
	
	Color radiance = new Color();
	BRDF brdf = new Lambertian(new Color(0, 0, 0));
	BSDF bsdf = new Transluscent();
	
	public LambertianEmitter() { }
	
	public void setBRDF(BRDF brdf) { this.brdf = brdf; }
	public void setBSDF(BSDF bsdf) { this.bsdf = bsdf; }
	public void setRadiance(Color emittedRadiance) { this.radiance.set(emittedRadiance); }

	public BRDF getBRDF(IntersectionRecord iRec) {
		return brdf;
	}

	public BSDF getBSDF(IntersectionRecord iRec) {
		return bsdf;
	}

	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
		outRadiance.set(radiance);
	}

	public boolean isEmitter() {
		return true;
	}

}

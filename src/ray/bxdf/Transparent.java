package ray.btdf;

import ray.math.Frame3;
import ray.math.Point2;
import ray.math.Vector3;
import ray.math.Geometry;
import ray.misc.Color;

/**
 * A Lambertian (constant) BTDF, which has value T/pi where T is the transmitance.
 * 
 * @author srm
 */
public class Transparent implements BTDF {
    
    // The material's trasmitance (the fraction of incident irradiance transmitted, for any incident distribution).
    private Color transmittance = new Color(0.0, 0.0, 0.0);
    private Color refraciveIndex = new Color(1.0, 1.0, 1.0);
    
    // For the benefit of the parser
    public Transparent() { }

    public void setTransmittance(Color transmittance) { 
        this.transmittance.set(transmittance); 
    }
    public Color getTransmittance(){return this.transmittance;}
    
    public Transparent(Color transmittance) { this.transmittance.set(transmittance); }

    public void evaluate(Frame3 frame, Vector3 incDir, Vector3 reflDir, Color outBTDFValue) {
        outBTDFValue.set(transmittance);
        outBTDFValue.scale(1 / Math.PI);
    }

    public void generate(Frame3 frame, Vector3 fixedDir, Vector3 dir, Point2 seed, Color outWeight) {
        Geometry.squareToPSAHemisphere(seed, dir);
        frame.frameToCanonical(dir);
        outWeight.set(transmittance);
    }

    public void setRefractiveIndex(Color refraciveIndex) { 
        this.refraciveIndex.set(refraciveIndex); 
    }

    public void getComponets(Color specReflectance, Color diffReflectance, Color transmittance,Color refraciveIndex){
        specReflectance.set(  new Color() );
        diffReflectance.set(  new Color()  );
        transmittance.set( transmittance );
        refraciveIndex.set(this.refraciveIndex);
    }

    /**
     * @param frame frame comes from IntersectionRecord instance, where w component of this frame align with
     *        the surface normal. 
     * @see ray.brdf.BTDF#pdf(ray.math.Frame3, ray.math.Vector3, ray.math.Vector3)
     */
    public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir) {
        return fixedDir.dot(frame.w) / Math.PI;
    }

}

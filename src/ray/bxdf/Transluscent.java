package ray.bsdf;

import ray.math.Frame3;
import ray.math.Point2;
import ray.math.Vector3;
import ray.math.Geometry;
import ray.misc.Color;

/**
 * A Transluscent material. Has both absorption, reflection and transmition.
 * Has (constant) BSDF, which has value (T+R)/2pi where T is the transmitance and R the reflectance.
 * 
 * @author srm
 */
public class Transluscent implements BSDF {
    
    // The material's trasmitance (the fraction of incident irradiance transmitted, for any incident distribution).
    private Color transmittance = new Color(0.0, 0.0, 0.0);
    // The material's diffuse reflectance (the fraction of incident irradiance reflected, for any incident distribution).
    private Color reflectance = new Color(0.0, 0.0, 0.0);
    
    // For the benefit of the parser
    public Transluscent() { }
    public Transluscent(Color transmittance,Color reflectance) { 
        this.transmittance.set(transmittance); 
        this.reflectance.set(reflectance); 
    }

    public void setTransmittance(Color transmittance) { 
        this.transmittance.set(transmittance); 
    }

    public void setReflectance(Color reflectance) { 
        this.reflectance.set(reflectance); 
    }

    public Color getReflectance(){return this.reflectance;}

    public Color getTransmittance(){return this.transmittance;}
    

    //Not sure about these changes
    public void evaluate(Frame3 frame, Vector3 incDir, Vector3 outDir, Color outBSDFValue) {
        outBSDFValue.set(reflectance);
        outBSDFValue.scale(1 / Math.PI);
        //System.out.println("Not sure about this function!");
    }

    public void generate(Frame3 frame, Vector3 fixedDir, Vector3 dir, Point2 seed, Color outWeight) {
        Geometry.squareToPSAHemisphere(seed, dir);
        frame.frameToCanonical(dir);
        outWeight.set(transmittance);
        System.out.println("Not sure about this function!");

    }

    /**
     * @param frame frame comes from IntersectionRecord instance, where w component of this frame align with
     *        the surface normal. 
     * @see ray.brdf.BTDF#pdf(ray.math.Frame3, ray.math.Vector3, ray.math.Vector3)
     */
    public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir) {
        System.out.println("Not sure about this function!");
        return fixedDir.dot(frame.w) / Math.PI;

    }

}

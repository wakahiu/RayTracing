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
    // The material's diffuse and specular reflectance (the fraction of incident irradiance reflected, for any incident distribution).
    private Color specReflectance = new Color(0.0, 0.0, 0.0);
    private Color diffReflectance = new Color(0.0, 0.0, 0.0);
    private Color refraciveIndex = new Color(1.0, 1.0, 1.0);
    
    // For the benefit of the parser
    public Transluscent() { }
    public Transluscent(Color transmittance,Color specReflectance,Color diffReflectance) { 
        this.transmittance.set(transmittance); 
        this.specReflectance.set(specReflectance); 
        this.diffReflectance.set(diffReflectance); 
    }

    public void getComponets(Color specReflectance, Color diffReflectance, Color transmittance,Color refraciveIndex){
        specReflectance.set(this.specReflectance);
        diffReflectance.set(this.diffReflectance);
        transmittance.set(this.transmittance);
        refraciveIndex.set(this.refraciveIndex);

    }

    public void setTransmittance(Color transmittance) { 
        this.transmittance.set(transmittance); 
    }

    public void setSpecReflectance(Color specReflectance) { 
        this.specReflectance.set(specReflectance); 
    }

    public void setDiffReflectance(Color diffReflectance) { 
        this.diffReflectance.set(diffReflectance); 
    }

    public void setRefractiveIndex(Color refraciveIndex) { 
        this.refraciveIndex.set(refraciveIndex); 
    }

    public Color getSpecReflectance(){return this.specReflectance;}
    public Color diffReflectance(){return this.diffReflectance;}
    public Color getTransmittance(){return this.transmittance;}
    

    //Not sure about these changes
    public void evaluate(Frame3 frame, Vector3 incDir, Vector3 outDir, Color outBSDFValue) {
        outBSDFValue.set(specReflectance);
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

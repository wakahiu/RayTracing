package ray.surface;

import java.util.Random;
import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.sampling.IndependentSampler;
import ray.misc.Ray;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
    
    /** Material for this sphere. */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The center of the sphere. */
    protected final Point3 center = new Point3();
    
    /** The radius of the sphere. */
    protected double radius = 1.0;
    
    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {        
    }
    
    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter The center of the new sphere.
     * @param newRadius The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {        
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }
    

    //Siddhartha:   this function  finds a random point on the surface of the sphere and then
    // generates a random normal Ray from it.
    public Ray generateSurfaceNormalRay()
    {

        
        Random random = new Random();

        Vector3 seekDir = new Vector3();
        Vector3 normDir = new Vector3();

        // generate a random point 
        Point2 directSeed = new Point2();

        directSeed.set(random.nextDouble(), random.nextDouble());

        // this seed is used for generating 'seekDir', a direction vector which would help us reach a surface 
        //point on the sphere

        //sampler.sample(0, 0, directSeed); 
        Geometry.squareToPSAHemisphere(directSeed, seekDir);
        seekDir.normalize();

        normDir = seekDir;

        seekDir.scale(this.radius);


        Point3 generatedPoint = new Point3();

        generatedPoint=seekDir.translate(this.center);

        Ray generatedRay= new Ray(generatedPoint, normDir);

        return generatedRay;

    }

    //Siddhartha:  this function generates a random from a point on the surface of the sphere with
    // a spherical distribution

    public Ray generateSurfaceRandomRay(Ray surfaceRay)
    {

        Random random = new Random(1);
        Vector3 randomDir = new Vector3();
        Ray generatedRay = new Ray(surfaceRay.origin, randomDir);

        Point2 directSeed = new Point2();
        //sampler.sample(0, 0, directSeed); 

        directSeed.set(random.nextDouble(), random.nextDouble());
        
        Geometry.squareToPSAHemisphere(directSeed, randomDir);
        randomDir.normalize();

        double dotProduct = (surfaceRay.direction).dot(randomDir);
        
        if (dotProduct>0)
        {
          randomDir.scale(dotProduct);
          generatedRay.set(surfaceRay.origin, randomDir);
        }

        generatedRay.makeOffsetRay();
        return generatedRay;

    }

    public Ray chooseSampleRay(){
        Ray ray = generateSurfaceNormalRay();
        return generateSurfaceRandomRay(ray);

    }


    public void updateArea() {
    	area = 4 * Math.PI * radius*radius;
    	oneOverArea = 1. / area;
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    /**
     * Returns the center of the sphere in the input Point3
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {        
        outPoint.set(center);        
    }
    
    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {        
        this.center.set(center);        
    }
    
    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }
    
    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }
        
    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        // W4160 TODO (A)
        // In this function, you need to test if the given ray intersect with a sphere.
        // You should look at the intersect method in other classes such as ray.surface.Triangle.java
        // to see what fields of IntersectionRecord should be set correctly.
        // The following fields should be set in this function
        //     IntersectionRecord.t
        //     IntersectionRecord.frame
        //     IntersectionRecord.surface
        //
        // Note: Although a ray is conceptually a infinite line, in practice, it often has a length,
        //       and certain rendering algorithm relies on the length. Therefore, here a ray is a 
        //       segment rather than a infinite line. You need to test if the segment is intersect
        //       with the sphere. Look at ray.misc.Ray.java to see the information provided by a ray.
        
        //return false;
        
        /*sc3653 - TODO(A)
        reference - P. Shirley (fundamental of graphics)
         
         
         Given - ray (e + td)
                sphere with center 'c' and radius 'r'
         
         algorithm:
         
         1. calculate e_c= (e-c)
         2. find discriminant (del) of "At^2 + Bt + C=0", where:
            -> A = (d.d)
            -> B = 2* d.e_c
            -> C = (e_c).(e_c) - r^2
         
         3. if 'del' <0, return false
         4. else, compute t(1/2)= [-B (-/+) sqrt(B^2 - 4AC)]/2A
         5. if t1 lies between ray.start and ray.end, t=t1
            else if t2 lies between ray.start and ray.end, t=t2
         6. set outRecord.t, outRecord.frame, outRecord.surface
         
         
        */
        
        
        Vector3 e_c =new Vector3();
        Point3 e=ray.origin;
        Vector3 d= ray.direction;
        
        Point3 c=this.center;
        double r= this.radius;
        
        //step 1
        e_c.sub(e,c);
        
        double A= d.dot(d);
        double B= 2 * e_c.dot(d);
        double C= (e_c).dot(e_c) - r*r ;
        
        
        //step 2
        double del= B*B - 4 * A * C;
        
        //step 3
        if(del<0)
            return false;
        
        //step 4
        double t=0;
        double t1= (-B - Math.sqrt(del))/ (2 * A);
        double t2= (-B + Math.sqrt(del))/ (2 * A);
        
        

        if (t1>t2)
        {
            
            t1= t1+t2;
            t2=t1-t2;
            t1 =t1-t2;
        }
        
        
        //step 5
        if (t1>= ray.start && t1<=ray.end)
            t=t1;
        else if(t2>= ray.start && t2<=ray.end)
            t=t2;
        else
            return false;
            
        
        //step 6
        outRecord.t=t;
        ray.evaluate(outRecord.frame.o,t);
        outRecord.frame.w.sub(outRecord.frame.o,c);
        outRecord.frame.w.normalize();
        outRecord.frame.initFromW();
        
        outRecord.surface=this;
        
        return true;
        
        
        
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);        
    }
    
}

package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
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
        
        //Get the ray attributes.
        double dx = ray.direction.x;
        double dy = ray.direction.y;
        double dz = ray.direction.z;
        
        double ex = ray.origin.x;
        double ey = ray.origin.y;
        double ez = ray.origin.z;
        
        //Some usefull vectors
        Vector3 d = new Vector3( dx, dy, dz );
        Vector3 e = new Vector3( ex, ey, ez );
        
        Vector3 e_c = new Vector3( ex - center.x, ey - center.y, ez - center.z );
        
        //Find out how many solutions are there from the discriminant.
        double dec = d.dot( e_c );
        double dsq = d.squaredLength();
        double ecsq = e_c.squaredLength();
        
        double disc = dec*dec - dsq*(ecsq-radius*radius);
        
        //If the discriminant if negative, the squareroot is imaginary and the
        //line and sphere do not intersect.
        
        if(disc < 0.0 )
            return false;
        
        double t1 = (-dec - Math.sqrt( disc ))/dsq;
        double t2 = (-dec + Math.sqrt( disc ))/dsq;
        
        //t1 < t2. We look only for solutions in range
        if( t1 > ray.end || t2 < ray.start )
            return false;
            
        //Get the point of intersection on the sphere that is closest point to the
        //origin of the ray.
        double t = t1 < ray.start ? t2 : t1;
        
        //Fill out the record
        outRecord.t = t;
        outRecord.surface = this;
        
        //Set the co-ordinate frame's origin.
        Point3 ip = new Point3( ex, ey, ez );       //Intersection point
        ip.scaleAdd( t , d );
        outRecord.frame.o.set( ip.x , ip.y, ip.z );
        
        //Set the normal vector
        outRecord.frame.w.set( ip.x - center.x, ip.y - center.y, ip.z - center.z );
        outRecord.frame.initFromW();
        
        
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

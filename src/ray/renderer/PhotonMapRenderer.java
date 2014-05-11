package ray.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ray.light.PointLight;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Photon;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.surface.Surface;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.material.Material;

public class PhotonMapRenderer implements Renderer {

	private long photonsPerLight = 1000;

	private Random randGenerator = new Random();
	
	public PhotonMapRenderer() { }
	
	public void setPhotonsPerLight(int ppl) {
		photonsPerLight = ppl;
	}
	
	@Override
	public boolean usesPhotons(){
		return true;
	}
	/**
	* Photon tracing pass.
	*/
	@Override
	public void generatePhotonMap(Scene scene){ 
		//Get all the light sources. Point lights and luminaires
		ArrayList<Surface> luminaires = scene.getLuminaires();
		ArrayList<PointLight> pointlights = scene.getPointLights();
		
		//Lights should have power.
		Iterator<Surface> luminairesIter = luminaires.iterator();
		Iterator<PointLight> pointLightsIterator = pointlights.iterator();

		//Cast photons into the scene. 
		//TODO Using some kind of importance sampling to bias towards scene objects?
		while(luminairesIter.hasNext()){
			
			System.err.println("Implement surface luminaires!");
			Surface currLight = luminairesIter.next();
			for(int i = 0; i < photonsPerLight; i++){
				//Generate a photon in a random direction.
				//Cast the photon into the scene.
				//Get it's interacting with the scene.
				//Store it in a photon map.
				
			}
		}

		while(pointLightsIterator.hasNext()){
			
			PointLight currlight = pointLightsIterator.next();

			for(int i = 0; i < photonsPerLight; i++){
				//Sample a random direction.
				//Cast a ray in that direction.
				//Get it's interaction with the scene
				//Store it in the photon map.
				Ray ray = new Ray(currlight.location,currlight.sampleDirection());
				ray.makeOffsetRay();
				Photon photon = new Photon();
				castPhoton(ray,scene, photon);
				//sampleDirection
				//ray.
			}
		}
	}

	private void castPhoton(Ray ray, Scene scene, Photon photon){

		//Find if the ray intersects with any surface.
		IntersectionRecord iRec = new IntersectionRecord();

		if(scene.getFirstIntersection(iRec,ray)){

			Material iMat = iRec.surface.getMaterial();

			//The output radiance is a function 
			//Check if emmiter.
			//?

			//Russian Roulette to determine wheather photon undergoes
			// absorption, reflection - specular or diffuse- or transmision.
			double russianRouletteRV = randGenerator.nextDouble();

			//Diffuse reflection

			//Specular reflection

			//Transmission

			//Absorpion
		}
		//Ray did not meet any object. Do nothing.
	}
	/**
	* Rendering pass
	*/
	@Override
	public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
			int sampleIndex, Color outColor) {
		// find if the ray intersect with any surface
		IntersectionRecord iRec = new IntersectionRecord();

		float length = 0.1f;
		
		if (scene.getFirstIntersection(iRec, ray)) {
			
			Point2 directSeed = new Point2();
            sampler.sample(1, sampleIndex, directSeed);     // this random variable is for incident direction
            
            Material iMat = iRec.surface.getMaterial();
            //Color oColor = iMat.getBRDF(iRec).getDiffuseReflectance();

            // Generate a random incident direction
            Vector3 incDir = new Vector3();
            Geometry.squareToHemisphere(directSeed, incDir);
            iRec.frame.frameToCanonical(incDir);
            
            Ray shadowRay = new Ray(iRec.frame.o, incDir);
            shadowRay.makeOffsetRay();
            
            if ( !scene.getFirstIntersection(iRec, shadowRay) ) {

            	outColor.set(0.2,0.0,0.9);
            } else {
            	// determine the length of the shadow ray
                Vector3 exts = scene.getBoundingBoxExtents();
                if ( iRec.t > length * exts.length() ) 
                	outColor.set(0.8);
                else 
                	outColor.set(0.);
            }
            return;
		}
		
		scene.getBackground().evaluate(ray.direction, outColor);
	}

}

package ray.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ray.light.PointLight;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.Photon;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.surface.Surface;
import ray.bsdf.BSDF;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.material.Material;
import ray.kdtree.KDTree;
import ray.kdtree.KeyDuplicateException;
import ray.kdtree.KeySizeException;

public class PhotonMapRenderer implements Renderer {

	private long photonsPerLight = 1000;

	//Creates a 3 dimensional KDTree.
	KDTree kdt = new KDTree(3);

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
				Photon photon = new Photon(currlight.power);
				photon.power.scale(1.0/photonsPerLight);
				castPhoton(ray,scene, photon);
				//System.out.println(photon.power);
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
			///TODO: Check if emmiter.
			
			//Right now all photons are absorbed.
			//TODO handle other visual effects.
			Point3 sri_point = iRec.frame.o;
			double[] surface_and_ray_interaction_point = {sri_point.x,sri_point.y,sri_point.z};

			//Russian Roulette to determine wheather photon undergoes
			// absorption, reflection - specular or diffuse- or transmision.
			double russianRouletteRV = randGenerator.nextDouble();

			double pSpec = 0.9;
			double pDiff = 0.4;
			double pTrans = 0.0;
			double pTotal = pSpec + pDiff + pTrans;

			try{

				//Specular reflection. 
				//Reflect the photon and mark it as having undergone Caustic reflection.
				if(russianRouletteRV<pSpec/pTotal){
					/*
					//Cast another ray in the reflected direction
					Vector3 refDir = new Vector3(ray.direction);


					Ray refRay = new Ray(sri_point, refDir);
					iRec.frame.canonicalToFrame(refRay.direction);

					//Reflect it.
					//refRay.direction.scaleAdd(1.0,);

					iRec.frame.frameToCanonical(refRay.direction);
					ray.makeOffsetRay();
					Photon diffPhoton = new Photon(currlight.power);
					diffPhoton.power.scale(1.0/photonsPerLight);
					castPhoton(diffDay,scene, diffPhoton);
					*/
				}
					//Diffuse Reflection
				else if(russianRouletteRV<(pSpec+pDiff)/pTotal){

					/*
					//Store this current photon in the global photon map.
					photon.setPosition(sri_point);
					kdt.insert(surface_and_ray_interaction_point,photon);

					//Cast another ray in a random direction
					Ray diffRay = new Ray(sri_point,currlight.sampleDirection());
					diffRay.makeOffsetRay();
					Photon diffPhoton = new Photon(currlight.power);
					diffPhoton.power.scale(1.0/photonsPerLight);
					castPhoton(diffRay,scene, diffPhoton);
					*/
				}
					//Transmission
				else if(russianRouletteRV<(pSpec+pDiff+pTrans)/pTotal){


				}
					//Absorption
				else{	
				}
				photon.setPosition(sri_point);
				kdt.insert(surface_and_ray_interaction_point,photon);

			}catch(KeySizeException ksze){
				System.err.println("");
			}catch(KeyDuplicateException kde){
				System.err.println("x");
			}
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

		int numNear = 50;
		
		if (scene.getFirstIntersection(iRec, ray)) {
			
			Point3 sri_point = iRec.frame.o;
			Color outBSDFValue = new Color();

			//Get the material properties at the point of intersection.
			BSDF bsdf = iRec.surface.getMaterial().getBSDF(iRec);
			bsdf.evaluate(iRec.frame,null,null,outBSDFValue);

			double[] surface_and_ray_interaction_point = {sri_point.x,sri_point.y,sri_point.z};

			double distSq = 0.0;
			Color pow = new Color();
			
			Object [] objPhotons;

			try{

				objPhotons = kdt.nearest(surface_and_ray_interaction_point,numNear);

				float MaxDist = 0;
				for(int i = 0; i < numNear ; i++){
					pow.add( ((Photon)objPhotons[i]).power );
				}
				distSq = ((Photon)objPhotons[numNear-1]).position.distanceSquared(sri_point);

			}catch(KeySizeException ksze){
				System.err.println("");
			}
			pow.scale(1/distSq*30000);
			//System.out.println(pow);
			outColor.set(outBSDFValue);
			
			outColor.scale(pow);
			return;
		}
		
		scene.getBackground().evaluate(ray.direction, outColor);
	}

}

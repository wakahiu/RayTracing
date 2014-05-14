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
import ray.misc.LuminaireSamplingRecord;
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

	//Creates a 3 dimensional KDTree represeting the global photon map.
	KDTree globalPhotonMap = new KDTree(3);

	//Creates a 3 dimensional KDTree represeting the Caustic photon map
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

				//Get center of luminaire surface
				Point3 debugCenter = new Point3();
				currLight.getCenter(debugCenter);


				//Generate a random Luminaire sampling record
				Point2 directSeed = new Point2();
				directSeed.set(randGenerator.nextDouble(),randGenerator.nextDouble());
       			LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
       			currLight.chooseSamplePoint(debugCenter, directSeed, lRec);

				Ray ray = currLight.chooseSampleRay(lRec);
				ray.makeOffsetRay();
				Color power = new Color(1.0,1.0,0.0);
				currLight.getMaterial().emittedRadiance(lRec,power);
				Photon photon = new Photon(power);
				photon.power.scale(1.0/photonsPerLight);
				castPhoton(ray, scene, photon);
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

			//Get the bsdf
			BSDF bsdf = iRec.surface.getMaterial().getBSDF(iRec);
			Color specReflectance = new Color();
			Color diffReflectance  = new Color();
			Color transmittance  = new Color();
			Color refraciveIndex  = new Color();

			bsdf.getComponets(specReflectance, diffReflectance, transmittance,refraciveIndex);

			//Fresnel approximation?

			//Get the specular reflection of the material.
			//Get the diffuse reflection propeties of the material
			//Get the transmittion properties of the material.
			double pSpec = (specReflectance.r+specReflectance.g+specReflectance.b)/3.0;
			double pDiff = (diffReflectance.r+diffReflectance.g+diffReflectance.b)/3.0;
			double pTrans = (transmittance.r+transmittance.g+transmittance.b)/3.0;

			if( (pSpec+pDiff+pTrans) > 1.0 ){
				System.err.println("Photon probabilities must be in the interval [0,1]");
			}
			if( (pSpec+pDiff+pTrans) == 1.0 ){
				System.err.println("Warning: no aboption");
			}

			try{
				
				//Specular reflection. 
				//Reflect the photon and mark it as having undergone Caustic reflection.
				if(russianRouletteRV<pSpec){
					
					//Create a ray in a in  the direction of specular reflection
					Vector3 reffDir = new Vector3(ray.direction);

					//Reorient the direction to have its base aligned to the tangent plane of the surface
					iRec.frame.canonicalToFrame(reffDir);

					double x = reffDir.x;
					double y = reffDir.y;
					double z = reffDir.z;

					reffDir.set(x,y,-z);

					iRec.frame.frameToCanonical(reffDir);

					//Cast another ray in a random direction in the hemisphere above the surface.
					Ray specRay = new Ray(sri_point, reffDir);
					specRay.makeOffsetRay();

					//Readjust power to account for probability of survival
					Color outPower = new Color(photon.power);
					outPower.invScale( pSpec);
					outPower.scale(specReflectance);

					//Finally create a photon and cast it.
					Photon specPhoton = new Photon( outPower );
					castPhoton(specRay,scene, specPhoton);

				}
					//Diffuse Reflection
				else if(russianRouletteRV<(pSpec+pDiff)){

					//Create a ray in a random direction sampled over a hemisphere
					Point2 directSeed = new Point2(); 
					directSeed.set(randGenerator.nextDouble(), randGenerator.nextDouble());
					Vector3 randomDir = new Vector3();
					Geometry.squareToHemisphere(directSeed, randomDir);
					randomDir.normalize();

					//Reorient the direction to have its base aligned to the tangent plane of the surface
					iRec.frame.canonicalToFrame(randomDir);

					//Cast another ray in a random direction in the hemisphere above the surface.
					Ray diffRay = new Ray(sri_point, randomDir);
					diffRay.makeOffsetRay();

					//Readjust power to account for probability of survival
					Color outPower = new Color(photon.power);
					outPower.invScale( pDiff);
					outPower.scale(diffReflectance);

					//Finally create a photon and cast it.
					Photon diffPhoton = new Photon( outPower );
					castPhoton(diffRay,scene, diffPhoton);
					
				}
					//Transmission
				else if(russianRouletteRV<(pSpec+pDiff+pTrans)){

					
					//Readjust power to account for probability of survival
					Color outPower = new Color(photon.power);
					outPower.invScale( pTrans );

					outPower.scale(transmittance);

					//Query the refractive index in which the photon is propagating.
					Color prevRefIdx = photon.getRefIdx();
					Photon transPhoton = new Photon( outPower );

					//reference: http://www.starkeffects.com/snells-law-vector.shtml
					
					Color airRefIdx = new Color(1.0, 1.0, 1.0);
					Vector3 transmitDir = new Vector3( );

					if( refraciveIndex.eq( airRefIdx ) && prevRefIdx.eq(airRefIdx) ){
						transmitDir.set(ray.direction);
						//TODO scale the photon energy.
					}
					else{

						Vector3 n = new Vector3(iRec.frame.w);
						if(prevRefIdx.eq(refraciveIndex) ){
							//Exiting the surface
							transPhoton.resetRefIdx();
							n.scale(-1);		//The normal points outside the material
						}else{
							//Entering the surface
							//transPhoton.power.scale(1.1);
							transPhoton.setRefIdx(refraciveIndex);
						}

						
						Vector3 _n = new Vector3( n );
						_n.scale(-1);

						double eta_1__eta2 = photon.getRefIdx().r/transPhoton.getRefIdx().r;
						double eta1__eta2_2 = eta_1__eta2 * eta_1__eta2;

						Vector3 s= new Vector3(ray.direction);


						Vector3 operand1 = new Vector3();
						operand1.cross(_n, s);

						Vector3 _n_x_s = new Vector3(operand1);
						operand1.cross(n, _n_x_s);
						operand1.scale(eta_1__eta2);

						
						Vector3 sub_op = new Vector3();
						sub_op.cross(n,s);

						double scaleFactor = sub_op.dot(sub_op);
						scaleFactor = scaleFactor * eta1__eta2_2;
						scaleFactor = 1- scaleFactor;
						scaleFactor= Math.sqrt(scaleFactor);

						Vector3 operand2 = new Vector3(n);
						n.scale(scaleFactor);

						transmitDir.set(operand1);
						transmitDir.sub(operand2);
						
					}
					//part ends
					/*
					//Reorient the direction to have its base aligned to the tangent plane of the surface
					Vector3 normal = new Vector3(iRec.frame.w);
					//normal.normalize();

					//Create a ray in  the direction of transmission
					Vector3 incDir = new Vector3(ray.direction);
					//Reverse its direction
					incDir.scale(-1);

					double cosTheta1 = normal.dot(incDir);

					
					if(cosTheta1 < 0){
						//System.out.println("Bad things happened!");
						//outColor.set(1.0,1.0,0.0);
						//return;
					}else{
						//outColor.set(1.0,0.0,1.0);
						//return;
					}
					*/
					//Cast another ray in a random direction in the hemisphere above the surface.
					//Ray transRay = new Ray(sri_point, transDir); //Chandra comment : May 13
					Ray transRay = new Ray(sri_point, transmitDir);
					transRay.makeOffsetRay();
					

					//Finally create a photon and cast it.
					castPhoton(transRay,scene, transPhoton);
					
					//---- end play
				}
					//Absorption
				else{
					//Photon dies. RIP in the photon map
				}
				photon.setPosition(sri_point);
				globalPhotonMap.insert(surface_and_ray_interaction_point,photon);	

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

		int numNear = 40;
		
		if (scene.getFirstIntersection(iRec, ray)) {
			
			Point3 sri_point = iRec.frame.o;
			Color outBSDFValue = new Color();

			//Get the material properties at the point of intersection
			Material material = iRec.surface.getMaterial();
			BSDF bsdf = material.getBSDF(iRec);
			bsdf.evaluate(iRec.frame,null,null,outBSDFValue);
			
			if(material.isEmitter()){
				Color outRadiance = new Color();
				material.emittedRadiance(null,outRadiance);
				outBSDFValue.add(outRadiance);
			}

			double[] surface_and_ray_interaction_point = {sri_point.x,sri_point.y,sri_point.z};

			double distSq = 0.0;
			Color pow = new Color();
			
			Object [] objPhotons;

			//Get the material properties at the point of intersection.
			Color specReflectance = new Color();
			Color diffReflectance  = new Color();
			Color transmittance  = new Color();
			Color refraciveIndex  = new Color();

			bsdf.getComponets(specReflectance, diffReflectance, transmittance,refraciveIndex);

			//Get the specular reflection of the material.
			//Get the diffuse reflection propeties of the material
			//Get the transmittion properties of the material.
			double pSpec = (specReflectance.r+specReflectance.g+specReflectance.b)/3.0;
			double pDiff = (diffReflectance.r+diffReflectance.g+diffReflectance.b)/3.0;
			double pTrans = (transmittance.r+transmittance.g+transmittance.b)/3.0;


			if( (pSpec+pDiff+pTrans) > 1.0 ){
				System.err.println("Photon probabilities must be in the interval [0,1]");
			}
			if( (pSpec+pDiff+pTrans) == 1.0 ){
				System.err.println("Warning: no aboption");
			}

			try{

				double russianRouletteRV = randGenerator.nextDouble();
				//Specular reflection. 
				//Reflect the photon and mark it as having undergone Caustic reflection.
				if(russianRouletteRV<pSpec){
					
					//Create a ray in a in  the direction of specular reflection
					Vector3 reffDir = new Vector3(ray.direction);

					//Reorient the direction to have its base aligned to the tangent plane of the surface
					iRec.frame.canonicalToFrame(reffDir);

					double x = reffDir.x;
					double y = reffDir.y;
					double z = reffDir.z;

					reffDir.set(x,y,-z);

					iRec.frame.frameToCanonical(reffDir);

					//Create the ray emainating from the point on intersection oriented in the 
					//direction of reflection.
					Ray specRay = new Ray(sri_point, reffDir);
					specRay.makeOffsetRay();

					//Castt 
					rayRadiance(scene, specRay, sampler, sampleIndex, outColor);
					outColor.scale(specReflectance);
					return;
					

				}
					//Diffuse Reflection
				else if(russianRouletteRV<(pSpec+pDiff)){
					
				}
					//Transmission
				else if(russianRouletteRV<(pSpec+pDiff+pTrans)){

					//Create the ray emanating from the point of intersection oriented in the 
					//direction of refraction.
					Ray transRay = new Ray(sri_point, ray.direction);
					transRay.makeOffsetRay();


					//Query the refractive index in which the ray is propagating.
					Color prevRefIdx = ray.getRefIdx();



					//reference: http://www.starkeffects.com/snells-law-vector.shtml
					
					Color airRefIdx = new Color(1.0, 1.0, 1.0);
					Vector3 transmitDir = new Vector3( );

					if( refraciveIndex.eq( airRefIdx ) && prevRefIdx.eq(airRefIdx) ){
						transmitDir.set(ray.direction);
					}
					else{

						Vector3 n = new Vector3(iRec.frame.w);

						boolean exit = false;
						if(prevRefIdx.eq(refraciveIndex) ){
							//Exiting the surface
							transRay.resetRefIdx();
							n.scale(-1);		//The normal points outside the material
							//outColor.set(0.0,0.0,1.0);		//Regions which reflect internally are shaded yellow for debug.
							//return;
							exit = true;

						}else{
							//Entering the surface
							transRay.setRefIdx(refraciveIndex);
						}

						
						Vector3 _n = new Vector3( n );
						_n.scale(-1);

						//Get 
						double eta_1__eta2 = ray.getRefIdx().r/transRay.getRefIdx().r;
						double eta1__eta2_2 = eta_1__eta2 * eta_1__eta2;

						Vector3 s= new Vector3(ray.direction);


						Vector3 operand1 = new Vector3();
						operand1.cross(_n, s);

						//Check the critical angle
						double sinTheta1 = operand1.length();
						if(sinTheta1 > 1/eta_1__eta2 && false){
							//Perform a total internal reflection
							//Sid debug this.

							//Create a ray in a in  the direction of specular reflection
							Vector3 reffDir = new Vector3(ray.direction);

							//Reorient the direction to have its base aligned to the tangent plane of the surface
							iRec.frame.canonicalToFrame(reffDir);

							double x = reffDir.x;
							double y = reffDir.y;
							double z = -reffDir.z;

							reffDir.set(x,y,z);

							iRec.frame.frameToCanonical(reffDir);
							transmitDir.set(reffDir);
							transRay.setRefIdx(refraciveIndex);

							transmitDir.set(ray.direction);
							transmitDir.scale(-1);
							//Sid end debug
						}
						else{

							Vector3 _n_x_s = new Vector3(operand1);
							operand1.cross(n, _n_x_s);
							operand1.scale(eta_1__eta2);

							
							Vector3 sub_op = new Vector3();
							sub_op.cross(n,s);

							double scaleFactor = sub_op.dot(sub_op);
							scaleFactor = scaleFactor * eta1__eta2_2;
							scaleFactor = 1- scaleFactor;
							scaleFactor= Math.sqrt(scaleFactor);

							Vector3 operand2 = new Vector3(n);
							n.scale(scaleFactor);

							transmitDir.set(operand1);
							transmitDir.sub(operand2);
						}
						
					}

					transRay.makeOffsetRay();
					//Cast the ray
					rayRadiance(scene, transRay, sampler, sampleIndex, outColor);
					return;
				}
					//AbsorptionrayRadiance
				else{
					//Ray dies
				}


				objPhotons = globalPhotonMap.nearest(surface_and_ray_interaction_point,numNear);

				float MaxDist = 0;
				for(int i = 0; i < numNear ; i++){
					pow.add( ((Photon)objPhotons[i]).power );
				}
				distSq = ((Photon)objPhotons[numNear-1]).position.distanceSquared(sri_point);

			}catch(KeySizeException ksze){
				System.err.println("");
			}
			pow.scale(1.0/distSq*600000);
			outColor.set(outBSDFValue);
			
			outColor.scale(pow);
			//System.out.println(outColor);

			return;
		}
		
		scene.getBackground().evaluate(ray.direction, outColor);
	}

}

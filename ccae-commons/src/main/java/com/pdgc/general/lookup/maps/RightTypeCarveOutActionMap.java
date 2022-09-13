package com.pdgc.general.lookup.maps;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.classificationEnums.RightTypeType;

public class RightTypeCarveOutActionMap {
	 public RightTypeCarveOutActionMap() {}


     /// <summary>
     /// If a right request has a carveout that causes the right owner to be ignored in availability calculations, 
     /// define whether or not to automatically cause the opposing right owner to also be ignored when doing the bidirectional check
     /// </summary>
     /// <param name="request"></param>
     /// <param name="impactingType"></param>
     /// <returns></returns>
     public boolean transferCarveOutIgnore(RightType carveOutOwner, RightType opposingRightType)
     {
         if (carveOutOwner.getRightTypeType() == RightTypeType.EXHIBITION)
         {
             return false;
         }

         if (carveOutOwner.getRightTypeType() == RightTypeType.HOLDBACK)
         {
             return true;
         }

         if (carveOutOwner.getRightTypeType() == RightTypeType.EXCLUSIVE_EXHIBITION && opposingRightType.getRightTypeType() == RightTypeType.HOLDBACK)
         {
             return false;
         }

         if (carveOutOwner.getRightTypeType() == RightTypeType.EXCLUSIVE_EXHIBITION && opposingRightType.getRightTypeType() == RightTypeType.EXHIBITION)
         {
             return true;
         }

         return false;
     }

}

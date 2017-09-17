/**
 * @author Veaceslav Dubenco
 * @since 18.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.extraction.minutiae;

import com.campustechng.aminu.idpenrollment.sourceafis.general.Calc;
import com.campustechng.aminu.idpenrollment.sourceafis.general.DetailLogger;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Parameter;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.Minutia;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.TemplateBuilder;

import java.util.Iterator;

/**
 * 
 */
public final class MinutiaCloudRemover {
	@Parameter(upper = 300)
	public int NeighborhoodRadius = 20;
	@Parameter(upper = 30)
	public int MaxNeighbors = 4;

	public DetailLogger.Hook Logger = DetailLogger.off;

	public void Filter(TemplateBuilder template) {
		int radiusSq = Calc.Sq(NeighborhoodRadius);
		Iterator<Minutia> iter = template.minutiae.iterator();
		while (iter.hasNext()) {
			Minutia minutia = iter.next();
			int count = 0;
			for (Minutia neighbor : template.minutiae) {
				if (Calc.DistanceSq(neighbor.Position, minutia.Position) <= radiusSq) {
					count++;
				}
			}
			if (count - 1 > MaxNeighbors) {
				iter.remove();
			}
		}
		Logger.log(template);
	}
}

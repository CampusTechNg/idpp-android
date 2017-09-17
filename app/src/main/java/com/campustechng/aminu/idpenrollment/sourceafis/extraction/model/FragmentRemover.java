/**
 * @author Veaceslav Dubenco
 * @since 20.10.2012
 */
package com.campustechng.aminu.idpenrollment.sourceafis.extraction.model;

import com.campustechng.aminu.idpenrollment.sourceafis.general.DetailLogger;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.DpiAdjusted;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Nested;
import com.campustechng.aminu.idpenrollment.sourceafis.meta.Parameter;

/**
 * 
 */
public class FragmentRemover implements ISkeletonFilter {
	@DpiAdjusted
	@Parameter(lower = 3, upper = 100)
	public int MinFragmentLength = 22;

	@Nested
	public DotRemover DotRemover = new DotRemover();

	public DetailLogger.Hook Logger = DetailLogger.off;

	@Override
	public void Filter(SkeletonBuilder skeleton) {
		for (SkeletonBuilderMinutia minutia : skeleton.getMinutiae())
			if (minutia.getRidges().size() == 1) {
				Ridge ridge = minutia.getRidges().get(0);
				if (ridge.getEnd().getRidges().size() == 1
						&& ridge.getPoints().size() < MinFragmentLength)
					ridge.Detach();
			}
		DotRemover.Filter(skeleton);
		Logger.log(skeleton);
	}
}
